package com.jillesvangurp.jsondsl.readme

import com.jillesvangurp.koiso.country.Country
import com.jillesvangurp.koiso.country.dialCode
import com.jillesvangurp.koiso.country.flag
import com.jillesvangurp.koiso.country.resolveCountry
import com.jillesvangurp.kotlin4example.SourceRepository
import java.io.File
import kotlin.test.Test

// FIXME adjust
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
                    # KoIso
        
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
            subSection("Hello World") {
                example {
                    // resolves by numeric, alpha2, alpha3 codes. Case insensitive.
                    Country.resolveCountry("de")?.let { country ->
                        println("${country.name} (${country.flag})")
                        println("code: ${country.countryCode}")
                        println("alpha2: ${country.alpha2}")
                        println("alpha3: ${country.alpha3}")
                        println("Phone numbers start with ${country.dialCode}")
                    }
                }.let {
                        +"""
                           Prints the following:
                        """
                            .trimIndent()

                        mdCodeBlock(it.stdOut, type = "text")
                    }
            }
        }
        +"""
                This README uses [kotlin4example](https://github.com/jillesvangurp/kotlin4example) so the examples should always be in a working state.   
            """
            .trimIndent()

        includeMdFile("outro.md")
    }
