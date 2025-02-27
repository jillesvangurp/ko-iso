# KoIso

Simple abstractions for working with country and language codes in kotlin.

Over the years I've dealt with multiple projects where I needed to handle country or language code information. There are libraries, csv files, json files, etc for many platforms but nothing really convenient and maintained for Kotlin multiplatform. 

This solves that and I plan to keep it fresh. The relevant standards change once in a while. If you notice something is out of date, please create an issue or pull request and I'll fix it.

## Features

- Country class to represent countries. Has all the common ISO 3166 codes. Based on [this project](https://github.com/lukes/ISO-3166-Countries-with-Regional-Codes). My intention is to copy upstream changes if/when they happen. Note. this data set is licensed under Creative Commons Attribution-ShareAlike 4.0 International License. 
- Additional flag and phone dial prefix extension properties added via alpha2 code via this [gist](https://gist.github.com/devhammed/78cfbee0c36dfdaa4fce7e79c0d39208)
- Language class for resolving ISO 639-2 and 639-3 languages by their 2/3 letter code
- Language search on name and codes. Also supports fuzzy search via a boolean fuzzy parameter. 

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

## Examples

### Resolving country codes

```kotlin
// resolves by numeric, alpha2, alpha3 codes. Case insensitive.
Country.resolve("de")?.let { country ->
  println("${country.name} (${country.flag})")
  println("code: ${country.countryCode}")
  println("alpha2: ${country.alpha2}")
  println("alpha3: ${country.alpha3}")
  println("Phone numbers start with ${country.dialCode}")
}
```

Prints the following:

```text
Germany (🇩🇪)
code: 276
alpha2: DE
alpha3: DEU
Phone numbers start with +49
```

### Resolving language codes

```kotlin
// finds the best language by its 2/3 letter code
// using iso-639-2 and iso-639-3 languages
// included in this repository
println(Language.resolve("de"))
```

Prints the following:

```text
de: German/Deutsch
```

### Fuzzy searching languages by name

```kotlin
// Deutsch misspelled; best match would be de
println(
  Language.search(
    query = "deUtSh",
    fuzzy = true,
  ).joinToString("\n"),
)
```

Prints the following:

```text
de: German/Deutsch
ger: German/Deutsch
deu: German/Deutsch
nl: Dutch/Flemish
dut: Dutch/Flemish
nld: Dutch/Flemish
da: Danish/Dansk
nr: Ndebele/South
cy: Welsh/Cymraeg
dan: Danish/Dansk
nbl: Ndebele/South
wel: Welsh/Cymraeg
cym: Welsh/Cymraeg
```

This README uses [kotlin4example](https://github.com/jillesvangurp/kotlin4example) so the examples should always be in a working state.   

## Multi platform

This is a Kotlin multi platform library that should work on all kotlin platforms (jvm, js, wasm, native, ios, android, etc).

