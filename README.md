No dessert - linter
===

[![](https://jitpack.io/v/westonal/no-desserts.svg)](https://jitpack.io/#westonal/no-desserts)

This is a linter that enforces zero usages of Android's marketing names for their versions (desserts
until recent years).

This is simply due to the fact the API documentation uses numbers, and the marketing terms are added
in by the IDE as it auto fixes warnings about SDK versions.

It includes a lint quick fix that will inline the integer constant for you.

Usage
===

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    lintChecks 'com.github.westonal:no-desserts:0.1'
}
```

Important, note `lintChecks`, not anything else.

If you don't any expected warnings straight away, in AS, go to File -> "Sync Project with Gradle Files".

This originally came from the Signal Android source https://github.com/signalapp/Signal-Android

This documentation is very useful if you are attempting something like this yourself:
http://googlesamples.github.io/android-custom-lint-rules/api-guide.html
