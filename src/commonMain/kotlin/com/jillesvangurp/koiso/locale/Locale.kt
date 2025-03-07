package com.jillesvangurp.koiso.locale

import com.jillesvangurp.koiso.country.Country
import com.jillesvangurp.koiso.language.Language

/**
 * Represents a locale with language, optional country, and optional variant.
 */
data class Locale(
    val language: Language,
    val country: Country? = null,
    val variant: String? = null
) {
    /**
     * Returns a BCP-47 compliant language tag.
     * The country is formatted using its upper-case country code if available.
     */
    fun toLanguageTag(): String {
        return buildString {
            append(language.code.lowercase())
            country?.let {
                append("-")
                append(it.name.uppercase())
            }
            variant?.let {
                append("-")
                append(it)
            }
        }
    }

    companion object {
        /**
         * Parses a BCP-47 tag (e.g. "en-US" or "en") into a Locale.
         * The [resolveLanguage] and [resolveCountry] functions are used to map string codes
         * to their corresponding Language and Country objects.
         *
         * Returns null if the language part of the tag cannot be resolved.
         */
        fun parse(
            tag: String,
            resolveLanguage: (String) -> Language?,
            resolveCountry: (String) -> Country?
        ): Locale? {
            val parts = tag.split("-", "_")
            if (parts.isEmpty()) return null
            val language = resolveLanguage(parts[0].lowercase()) ?: return null
            val country = if (parts.size >= 2) resolveCountry(parts[1].uppercase()) else null
            val variant = if (parts.size >= 3) parts.subList(2, parts.size).joinToString("-") else null
            return Locale(language, country, variant)
        }
    }
}
