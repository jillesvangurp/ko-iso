Simple abstractions for working with country and language codes in kotlin.

Over the years I've dealt with multiple projects where I needed to handle country or language code information. There are libraries, csv files, json files, etc for many platforms but nothing really convenient and maintained for Kotlin multiplatform. 

This solves that and I plan to keep it fresh. The relevant standards change once in a while. If you notice something is out of date, please create an issue or pull request and I'll fix it.

Because this is multiplatform, I'm not working with resource files that are loaded and parsed but simple string literals that contain json formatted languages and countries.

## Features

- Country class to represent countries. Has all the common ISO 3166 codes. Based on [this project](https://github.com/lukes/ISO-3166-Countries-with-Regional-Codes). My intention is to copy upstream changes if/when they happen. Note. this data set is licensed under Creative Commons Attribution-ShareAlike 4.0 International License. 
- Additional flag and phone dial prefix extension properties added via alpha2 code via this [gist](https://gist.github.com/devhammed/78cfbee0c36dfdaa4fce7e79c0d39208)
- Language class for resolving ISO 639-2 and 639-3 languages by their 2/3 letter code. Uses [this](https://github.com/freearhey/iso-639-3/blob/master/index.json) for ISO 639-2 and (this)[https://github.com/freearhey/iso-639-3] for some more obscure iso-639-3 languages.
- Language search on name and codes. Also supports fuzzy search via a boolean fuzzy parameter. 

I may add additional / related meta data and features if it can be done by not including huge data files. Ideas / PRs for this are welcome of course. Anything further (like exhaustive translations of language and country names by code) would best be hosted in a separate library. E.g. hooking up geonames based translations is tempting but out of scope for this library.

## Status and API stability

Despite the relative young age of this library things should be usable for any project. 

The API should remain stable but there may be additions/tweaks. Also, the underlying language and country information may be updated as needed and there may be periodic updates to keep the build dependencies fresh and keep things working with the current stable version of Kotlin.

By design there are very few dependencies beyond kotlinx.serialization and a related dependency (com.jillesvangurp:kotlinx-serialization-extensions) that adds a sane default configuration for Json. Any PR that adds additional dependencies will not be accepted unless there's a really good reason.

## Gradle

This library is published to our own maven repository.

```kotlin
repositories {
    mavenCentral()
    maven("https://maven.tryformation.com/releases") {
        // optional but it speeds up the gradle dependency resolution
        content {
            includeGroup("com.jillesvangurp")
            includeGroup("com.tryformation")
        }
    }
}
```

And then you can add the dependency:

```kotlin
    // check the latest release tag for the latest version
    implementation("com.jillesvangurp:ko-iso:1.x.y")
```

Alternatively just lift the relevant classes and copy them to your repository. Fine by me as long as you respect License and copyright notices. 