package com.jillesvangurp.jsondsl.readme

import com.jillesvangurp.kotlin4example.SourceRepository
import java.io.File
import kotlin.test.Test

const val githubLink = "https://github.com/formation-res/pg-docstore"

val sourceGitRepository =
    SourceRepository(
        repoUrl = githubLink,
        sourcePaths = setOf("src/commonMain/kotlin", "src/commonTest/kotlin", "src/jvmTest/kotlin")
    )

class ReadmeGenerationTest {

    @Test
    fun `generate docs`() {
        File(".", "README.md")
            .writeText(
                """
            # JsonDsl

        """.trimIndent().trimMargin() +
                    "\n\n" +
                    readmeMd.value
            )
    }
}

val readmeMd =
    sourceGitRepository.md {
        includeMdFile("intro.md")

        section("Example") {
            +"""
            The main feature of [kotlin4example](https://github.com/jillesvangurp/kotlin4example) is of course integrating code samples into your documentation.   
        """
                .trimIndent()
            subSection("Hello World") {
                example { println("Hello World!") }
                    .let {
                        +"""
                   And you can actually grab the output and show it in another code block:
                """
                            .trimIndent()

                        mdCodeBlock(it.stdOut, type = "text")
                    }
            }
        }
        includeMdFile("outro.md")
    }
