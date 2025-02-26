# JsonDsl

This is an **opinionated** template for creating kotlin multi platform library projects.

This works for me and might help you bootstrap your kotlin projects.

## Batteries included

My goal with this is to waste less time setting up new projects. Kotlin multiplatform can be a bit fiddly/challenging to get going with and there are a lot of things that I want to add to projects. This gets me there with minimal fiddling.

- Gradle wrapper with recent version of gradle & kts dialect
- [Refresh versions plugin](https://splitties.github.io/refreshVersions/) - Great way to manage dependencies and stay on top of updates.
- [kotlin4example](https://github.com/jillesvangurp/kotlin4example) integrated to generate the readme and any other documentation you are going to write. This is all driven via the tests.
- Some dependencies for testing (junit, kotest-assertions, etc.) and test setup for junit
- generic publish script that tags and publishes
- Github action that builds your stuff generated using [github-workflows-kt](https://github.com/typesafegithub/github-workflows-kt). Setup to cache gradle and konan related files to speed up your builds.
- LICENSE file (MIT)

## Usage & project create/update checklist

- [ ] Go to Github and push the "Use this template" button. This will create a new project based on this template
- [ ] Change your project name by changing `rootProject.name = "my-new-kmp-project"` in settings.gradle.kts. 
- [ ] Override the group name in gradle.properties
- [ ] Review default maven repo for releases and other things in build.gradle.kts
- [ ] Update & review the [License](License); change the copyright year and owner.
- [ ] Run `./gradlew refreshVersions` and update versions.properties
- [ ] Add your own dependencies
- [ ] If needed, run `./gradlew kotlinUpgradeYarnLock`
- [ ] If a newer version of gradle is available, run `./gradlew wrapper --gradle-version 8.11.1` (substitute latest version)
- [ ] Start coding and write your own README.md and other documentation by modifying the `ReadmeGenerationTest`

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
    implementation("com.jillesvangurp:my-new-kmp-project:1.x.y")
```

## Example

The main feature of [kotlin4example](https://github.com/jillesvangurp/kotlin4example) is of course integrating code samples into your documentation.   

### Hello World

```kotlin
println("Hello World!") 
```

And you can actually grab the output and show it in another code block:

```text
Hello World!
```

## Multi platform

This is a Kotlin multi platform library that should work on all kotlin platforms (jvm, js, ios, android, etc).

