package com.jillesvangurp.koiso.locale

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import com.jillesvangurp.koiso.country.Country
import com.jillesvangurp.koiso.language.Language
import com.jillesvangurp.koiso.language.LanguageSource

// Stub implementations for testing purposes.
class TestLanguage(
    override val code: String
) : Language(code, listOf("English"), listOf("English"), LanguageSource.ISO_639_2) {}

class TestCountry(
    override val name: String
) : Country(name) {}

class LocaleTest {

    // Stub resolvers
    private fun resolveLanguage(langCode: String): Language? {
        return when (langCode.lowercase()) {
            "en" -> TestLanguage("en")
            "fr" -> TestLanguage("fr")
            else -> null
        }
    }

    private fun resolveCountry(countryCode: String): Country? {
        return when (countryCode.uppercase()) {
            "US" -> TestCountry("United States")
            "FR" -> TestCountry("France")
            else -> null
        }
    }

    @Test
    fun testLocaleWithLanguageOnly() {
        val locale = Locale.parse("en", ::resolveLanguage, ::resolveCountry)
        assertNotNull(locale)
        assertEquals("en", locale.toLanguageTag())
    }

    @Test
    fun testLocaleWithLanguageAndCountry() {
        val locale = Locale.parse("en-US", ::resolveLanguage, ::resolveCountry)
        assertNotNull(locale)
        // Country name "United States" is uppercased in the result.
        assertEquals("en-UNITED STATES", locale.toLanguageTag())
    }

    @Test
    fun testLocaleWithLanguageCountryAndVariant() {
        val locale = Locale.parse("fr-FR-variant", ::resolveLanguage, ::resolveCountry)
        assertNotNull(locale)
        assertEquals("fr-FRANCE-variant", locale.toLanguageTag())
    }

    @Test
    fun testLocaleInvalidLanguage() {
        val locale = Locale.parse("xx-US", ::resolveLanguage, ::resolveCountry)
        assertNull(locale)
    }
}
