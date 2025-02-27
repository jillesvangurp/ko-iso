package com.jillesvangurp.language

import com.jillesvangurp.koiso.language.Language
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class LanguageTest {
    @Test
    fun shouldLookUpLanguageCaseInsensitive() {
        val lang = Language.resolve("dEu")
        lang.shouldNotBeNull()
        lang.name shouldBe "German"
    }

    @Test
    fun shouldFindLanguages() {
        val languages = Language.search("deutsch", fuzzy = true)
        println(languages.joinToString(",") { l -> "${l.code} (${l.name})" })

        languages.first().code shouldBe "de"
    }
}
