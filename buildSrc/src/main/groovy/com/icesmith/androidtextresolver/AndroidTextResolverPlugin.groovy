package com.icesmith.androidtextresolver

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.tasks.TaskContainer

import javax.annotation.Nullable

class AndroidTextResolverPlugin implements Plugin<Project> {
    void apply(Project project) {
        def android = project.getProperties().get('android')
        if (android == null) {
            throw new Exception('the plugin can be applied only to android projects')
        }

        android.extensions.create('textresolver', AndroidTextResolverPluginExtension)

        if (android.hasProperty('libraryVariants')) {
            android.libraryVariants.all { variant ->
                String variantName = variant.name.substring(0, 1).toUpperCase() + variant.name.substring(1)
                String packageTaskName = "package${variantName}Resources"

                TaskContainer tasks = project.getTasks()
                tasks.getByName(packageTaskName).doLast {
                    String resDirPath = "${project.buildDir}/intermediates/bundles/${variant.name}/res"
                    File resDir = new File(resDirPath)
                    AndroidTextResolverPluginExtension extension = android.extensions.textresolver
                    processResourcesDirectory resDir, extension
                }
            }
        } else if (android.hasProperty('applicationVariants')) {
            android.applicationVariants.all { variant ->
                variant.mergeResources.doLast {
                    File resDir = variant.getMergeResources().outputDir
                    AndroidTextResolverPluginExtension extension = android.extensions.textresolver
                    processResourcesDirectory resDir, extension
                }
            }
        }
    }

    private static void processResourcesDirectory(File resDir, AndroidTextResolverPluginExtension extension) {
        Map<String, String> defaultStringMap = null
        File defaultValuesFile = new File(resDir, "values/values.xml")
        if (defaultValuesFile.exists()) {
            defaultStringMap = processValuesFile defaultValuesFile, extension, null
        }

        resDir.eachDir { dir ->
            if (dir.name.startsWith("values") && dir.name != "values") {
                File valuesFile = new File(dir, dir.name + ".xml")
                processValuesFile valuesFile, extension, defaultStringMap
            }
        }
    }

    @Nullable
    private static Map<String, String> processValuesFile(File valuesFile,
                                                         AndroidTextResolverPluginExtension extension,
                                                         @Nullable Map<String, String> defaultStringMap) {
        Node rootNode = new XmlParser().parse(valuesFile)

        Map<String, String> stringMap = [:]
        for (Node stringNode : rootNode.string) {
            String stringName = stringNode.attribute("name")
            stringMap[stringName] = stringNode.text();
        }

        boolean changed = false
        for (Node stringNode : rootNode.string) {
            String text = stringNode.text()
            String updatedText = processString text, extension.pattern, stringMap, defaultStringMap
            if (updatedText != text) {
                stringNode.setValue(updatedText)
                changed = true
            }
        }

        if (changed) {
            valuesFile.withWriter("UTF-8") { out ->
                PrintWriter writer = new PrintWriter(out)
                XmlNodePrinter printer = new XmlNodePrinter(writer)
                printer.preserveWhitespace = true
                printer.print(rootNode)
            }
        }

        return stringMap
    }

    private static String processString(String str,
                                        String pattern,
                                        Map<String, String> stringMap,
                                        @Nullable Map<String, String> defaultStringMap) {


        return str.replaceAll(pattern) { fullMatch, stringName ->
            def String referencedString = stringMap[stringName]
            if (referencedString == null && defaultStringMap != null) {
                referencedString = defaultStringMap[stringName]
            }

            if (referencedString != null) {
                return processString(referencedString, pattern, stringMap, defaultStringMap)
            } else {
                throw new Exception("unknown string reference: " + stringName + " from: " + str);
            }
        }
    }
}

class AndroidTextResolverPluginExtension {
    String pattern = /\{\{(.*?)\}\}/
}
