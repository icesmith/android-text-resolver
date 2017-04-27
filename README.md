# Description
The plugin allows you to refer one string from another. The plugin [located](https://plugins.gradle.org/plugin/com.icesmith.androidtextresolver) on Gradle plugin portal.
# Integration
Just add the next code into you app or library module level build.gradle
```gradle
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "gradle.plugin.android-text-resolver:buildSrc:1.0.3"
  }
}

apply plugin: "com.icesmith.androidtextresolver"
```
# Usage
Use `{{string_id}}` syntax to refer strings. For example
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="super">Super</string>
    <string name="app_name">My {{super}} App</string>
    <string name="app_description">Name of my application is: {{app_name}}</string>
</resources>
```

# Sources
The plugin code can be found [here](https://github.com/icesmith/android-text-resolver/blob/master/buildSrc/src/main/groovy/com/icesmith/androidtextresolver/AndroidTextResolverPlugin.groovy)