# MAPACHE
# Library for createing state-based navigation in android app

## Usage without Kotlin

### Add dependency and annotation processor
For usage this library add following dependencies to your build script (_build.gradle_)
```groovy
dependencies {
    annotationProcessor "name.wildswift.android:mapache-navigation:0.1.5"
    implementation "name.wildswift.android:mapache-library:0.1.5"
}
```

### Configure annotation processor
And setup annotation processor argument
```groovy
android {
    // ...
    defaultConfig {
        // ...
        javaCompileOptions {
            annotationProcessorOptions {
                arguments "mapache.configs.location": project.projectDir.path
            }
        }
    }
}
```

## Usage with Kotlin

### Add Kotlin support
Add following lines to your build script (_build.gradle_)
```groovy
buildscript {
    dependencies {
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.71"
    }
}

apply plugin: 'kotlin-android'
```

### Add Kotlin annotation processor support
Add following lines to your build script (_build.gradle_)
```groovy
apply plugin: 'kotlin-kapt'
```


### Add dependencies and annotation processor
For usage this library add following dependencies to your build script (_build.gradle_)
```groovy
dependencies {
    kapt "name.wildswift.android:android-annotation-processor:0.8.2"
    implementation "name.wildswift.android:android-annotations:0.8.0"
}
```

### Configure annotation processor
And setup annotation processor argument
```groovy
kapt {
    arguments {
        arg("mapache.configs.location", project.projectDir.path)
    }
}
```

> :warning: _IN PROGRESS_
