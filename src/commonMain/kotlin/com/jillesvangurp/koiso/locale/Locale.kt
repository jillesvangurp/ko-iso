package com.jillesvangurp.koiso.locale

import com.jillesvangurp.koiso.country.Country
import com.jillesvangurp.koiso.language.Language

/**
 * Representation of a locale with a language, an optional country, and an optional variant.
 * Language tags are formatted according to
 * [BCP&nbsp;47](https://en.wikipedia.org/wiki/IETF_language_tag).
 */
data class Locale(
    val language: Language,
    val country: Country? = null,
    val variant: String? = null
) {
    /**
     * Returns a [BCP&nbsp;47](https://en.wikipedia.org/wiki/IETF_language_tag) language tag.
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
         * Parse a [BCP&nbsp;47](https://en.wikipedia.org/wiki/IETF_language_tag) tag (e.g. `en-US`)
         * into a [Locale]. The provided [resolveLanguage] and [resolveCountry] functions
         * convert the textual codes to [Language] and [Country] instances. Returns `null`
         * when the language part cannot be resolved.
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
