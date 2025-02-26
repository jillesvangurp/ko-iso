@file:OptIn(ExperimentalWasmDsl::class)

import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    `maven-publish`
}

// FIXME adjust as needed
val ghUser = "jillesvangurp"
val ghProjectName = "kmp-template"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io") {
        content {
            includeGroup("com.github.jillesvangurp")
        }
    }
    // FIXME adjust as needed
    // remove this if you don't use any of our multiplatform libraries
    maven("https://maven.tryformation.com/releases")
}

kotlin {
    jvm {
        // should work for android as well
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }
    js(IR) {
        nodejs {
            testTask {
                useMocha {
                    // javascript is a lot slower than Java, we hit the default timeout of 2000
                    timeout = "60s"
                }
            }
        }
    }
    linuxX64()
    linuxArm64()
    mingwX64()
    macosX64()
    macosArm64()
    iosArm64()
    iosSimulatorArm64()
    iosX64()
    iosSimulatorArm64()
    wasmJs {
        browser()
        nodejs()
        d8()
    }
    // blocked on kotest assertions wasm release
//    wasmWasi()

    sourceSets {

        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:_")
                implementation("com.jillesvangurp:kotlinx-serialization-extensions:_")
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation("io.kotest:kotest-assertions-core:_")
            }
        }

        jvmMain  {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }
        jvmTest {
            dependencies {
                implementation("ch.qos.logback:logback-classic:_")
                implementation("com.github.jillesvangurp:kotlin4example:_")
                implementation("org.junit.jupiter:junit-jupiter:_")
                runtimeOnly("org.junit.platform:junit-platform-launcher")
            }
        }

        jsMain  {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }

        jsTest  {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }

        wasmJsTest {
            dependencies {
                implementation(kotlin("test-wasm-js"))
            }
        }

        all {
            languageSettings {
                languageVersion = "1.9"
                apiVersion = "1.9"
            }
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
    }
}

tasks.named("iosSimulatorArm64Test") {
    // requires IOS simulator and tens of GB of other stuff to be installed
    // so keep it disabled
    enabled = false
}


publishing {
    publications {
        withType<MavenPublication> {
            pom {
                url.set("https://github.com/$ghUser/$ghProjectName")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/$ghUser/$ghProjectName/blob/master/LICENSE")
                    }
                }

                developers {
                    developer {
                        id.set("jillesvangurp")
                        name.set("Jilles van Gurp")
                        email.set("jilles@no-reply.github.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/$ghUser/$ghProjectName.git")
                    developerConnection.set("scm:git:ssh://github.com:$ghUser/$ghProjectName.git")
                    url.set("https://github.com/$ghUser/$ghProjectName")
                }
            }
        }
    }
    repositories {
        // setup publishing repo for https://maven.tryformation.com/releases
        maven {
            // GOOGLE_APPLICATION_CREDENTIALS env var must be set for this to work
            // public repository is at https://maven.tryformation.com/releases
            url = uri("gcs://mvn-public-tryformation/releases")
            name = "FormationPublic"
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    // run tests in parallel
    systemProperties["junit.jupiter.execution.parallel.enabled"] = "true"
    // executes test classes concurrently
    systemProperties["junit.jupiter.execution.parallel.mode.default"] = "concurrent"
    // executes tests inside a class concurrently
    systemProperties["junit.jupiter.execution.parallel.mode.classes.default"] = "concurrent"
    systemProperties["junit.jupiter.execution.parallel.config.strategy"] = "dynamic"
    // random order of test class execution
    systemProperties["junit.jupiter.testclass.order.default"] = "org.junit.jupiter.api.ClassOrderer\$Random"

    testLogging.exceptionFormat = TestExceptionFormat.FULL
    testLogging.events = setOf(
        TestLogEvent.FAILED,
        TestLogEvent.PASSED,
        TestLogEvent.SKIPPED,
        TestLogEvent.STANDARD_ERROR,
        TestLogEvent.STANDARD_OUT
    )
    addTestListener(object : TestListener {
        val failures = mutableListOf<String>()
        override fun beforeSuite(desc: TestDescriptor) {
        }

        override fun afterSuite(desc: TestDescriptor, result: TestResult) {
        }

        override fun beforeTest(desc: TestDescriptor) {
        }

        override fun afterTest(desc: TestDescriptor, result: TestResult) {
            if (result.resultType == TestResult.ResultType.FAILURE) {
                val report =
                    """
                    TESTFAILURE ${desc.className} - ${desc.name}
                    ${
                        result.exception?.let { e ->
                            """
                            ${e::class.simpleName} ${e.message}
                        """.trimIndent()
                        }
                    }
                    -----------------
                    """.trimIndent()
                failures.add(report)
            }
        }
    })
}



