# Android Gradle plugin 3.0 and above
The library doesn't work with Android gradle plugin version 3.0 and above because 
the new version of the plugin uses aapt2 which packs resources into .flat binary format, 
so packed resources are unavailable for the library. As a temporary solution you can disable aapt2 
by setting android.enableAapt2=false in your gradle.properties file.

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
    classpath "gradle.plugin.android-text-resolver:buildSrc:1.2.0"
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

# Configuration
You can configure pattern, which will be used to find string references, for example, to use `[]` instead of `{{}}` add next code into your `build.gradle`
```gradle
android {
  textresolver {
    pattern = /\[(.*?)\]/
  }
}
```

# Sources
The plugin code can be found [here](https://github.com/icesmith/android-text-resolver/blob/master/buildSrc/src/main/groovy/com/icesmith/androidtextresolver/AndroidTextResolverPlugin.groovy)