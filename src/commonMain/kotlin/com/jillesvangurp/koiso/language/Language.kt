package com.jillesvangurp.koiso.language

import com.jillesvangurp.serializationext.DEFAULT_JSON
import kotlin.math.exp
import kotlin.math.min
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer

@Serializable
internal data class Iso639_2_LanguageMetaData(
    // used for parsing the json
    val english: List<String>,
    val native: List<String>
)

@Serializable
internal data class Iso639_3_LanguageMetaData(
    val code: String,
    val name: String
)

enum class LanguageSource {
    ISO_639_2,
    ISO_639_3,
}

data class Language(
    val code: String,
    val english: List<String>,
    val native: List<String>,
    val source: LanguageSource
) {
    val name get() = english.first()

    override fun toString(): String = "$code: ${(english+native).distinct().joinToString("/")}"

    val isAlpha2 get() = code.length == 2
    val isAlpha3 get() = code.length == 3

    companion object {
        private val iso639_2_languages by lazy {
            DEFAULT_JSON.decodeFromString(
                MapSerializer(
                    String.serializer(),
                    Iso639_2_LanguageMetaData.serializer(),
                ),
                iso639_2,
            ).map { (code, lm) ->
                Language(code, lm.english, lm.native, LanguageSource.ISO_639_2)
            }.associateBy { it.code }

        }

        private val iso639_3_languages by lazy {
            DEFAULT_JSON.decodeFromString(
                ListSerializer(Iso639_3_LanguageMetaData.serializer()),
                iso639_3,
            ).let {
                it.map { Language(it.code, listOf(it.name), listOf(), LanguageSource.ISO_639_3) }
                    .associateBy { it.code }
            }
        }

        fun resolve(code: String): Language? {
            val lowercaseCode = code.lowercase()
            return iso639_2_languages[lowercaseCode] ?: iso639_3_languages[lowercaseCode]
        }

        fun search(query: String, fuzzy: Boolean = false): List<Language> {
            fun levenshteinDistance(s1: String, s2: String): Int {
                val previous = IntArray(s2.length + 1) { it }
                val current = IntArray(s2.length + 1)

                for (i in 1..s1.length) {
                    current[0] = i
                    for (j in 1..s2.length) {
                        val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                        current[j] = min(
                            current[j - 1] + 1,
                            min(previous[j] + 1, previous[j - 1] + cost)
                        )
                    }
                    previous.indices.forEach { previous[it] = current[it] }
                }
                return current[s2.length]
            }

            fun fuzzyMatch(query: String, str: String): Double {
                val levenshtein = levenshteinDistance(query, str)
                val maxLen = maxOf(query.length, str.length)

                val threshold = when {
                    maxLen <= 3 -> 1  // Allow 1 edit for very short words
                    maxLen <= 5 -> 2  // Allow 2 edits for short words
                    maxLen <= 8 -> 3  // Allow 3 edits for medium words
                    else -> 4         // Allow 4 edits for longer words
                }

                return when {
                    levenshtein == 0 -> 1.0
                    query in str -> query.length.toDouble() / str.length
                    levenshtein > threshold -> 0.0  // Discard matches beyond threshold
                    else -> 1.0 - (levenshtein.toDouble() / maxLen)  // Normalize score to 0-1 range
                }
            }

            fun Language.match(query: String, fuzzy: Boolean = false): Double {
                return when {
                    code == query -> 1.0
                    name == query -> 0.8
                    query in english -> 0.7
                    query in native -> 0.6
                    fuzzy -> (english + native).map { fuzzyMatch(query, it.lowercase()) }.maxOrNull() ?: 0.0
                    else -> 0.0
                } * if (source == LanguageSource.ISO_639_3) 0.9 else 1.0 * if(isAlpha2) 1.0 else 0.9
            }

            fun Collection<Language>.rank(query: String, fuzzy: Boolean = false) =
                map { it to it.match(query, fuzzy) }
                    .filter { it.second > 0 }
                    .sortedByDescending { it.second }
                    .map { it.first }

            val normalized = query.lowercase()
            val resolved = resolve(normalized)

            return listOfNotNull(resolved) + iso639_2_languages.values.rank(normalized, fuzzy).ifEmpty {
                iso639_3_languages.values.rank(normalized, fuzzy)
            }
        }
//        fun search(query: String, fuzzy: Boolean = false): List<Language> {
//            fun levenshteinDistance(s1: String, s2: String): Int {
//                val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
//                for (i in 0..s1.length) dp[i][0] = i
//                for (j in 0..s2.length) dp[0][j] = j
//
//                for (i in 1..s1.length) {
//                    for (j in 1..s2.length) {
//                        dp[i][j] = min(
//                            dp[i - 1][j - 1] + if (s1[i - 1] == s2[j - 1]) 0 else 1,
//                            min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
//                        )
//                    }
//                }
//                return dp[s1.length][s2.length]
//            }
//
//            fun fuzzyMatch(query: String, str: String): Double {
//                val levenshtein = levenshteinDistance(query, str)
//                val maxLen = maxOf(query.length, str.length)
//
//                return when {
//                    levenshtein == 0 -> 1.0
//                    query in str -> query.length.toDouble() / str.length
//                    else -> {
//                        val threshold = when {
//                            maxLen <= 3 -> 1  // Allow 1 edit for very short words
//                            maxLen <= 5 -> 2  // Allow 2 edits for short words
//                            maxLen <= 8 -> 3  // Allow 3 edits for medium words
//                            else -> 4         // Allow 4 edits for longer words
//                        }
//                        if (levenshtein <= threshold) {
//                            1.0 - (levenshtein.toDouble() / maxLen)  // Normalize score to 0-1 range
//                        } else {
//                            0.0
//                        }
//                    }
//                }
//            }
//
//            fun Language.match(query: String, fuzzy: Boolean = false): Double {
//                return when {
//                    code == query -> if (isAlpha2) 1.0 else 0.9
//                    name == query -> 0.8
//                    query in english -> 0.7
//                    query in native -> 0.6
//                    fuzzy -> {
//                        (english + native).map { it.lowercase() }.maxOf {
//                            fuzzyMatch(query, it)
//                        } * 0.5
//                    }
//
//                    else -> 0.0
//                }.let { score ->
//                    // demote obscure languages
//                    if (source == LanguageSource.ISO_639_3) {
//                        score * 0.9
//                    } else {
//                        score
//                    }
//                }
//            }
//
//            fun Collection<Language>.rank(query: String, fuzzy: Boolean = false) = mapNotNull {
//                val score = it.match(query, fuzzy)
//                if (score > 0) it to score else null
//            }
//
//            val normalized = query.lowercase()
//
//            val resolved = resolve(normalized)
//            return if(resolved != null) {
//                listOf(resolved)
//            } else {
//                iso639_2_languages.values.rank(normalized, fuzzy).ifEmpty {
//                    iso639_3_languages.values.rank(normalized, fuzzy)
//                }.sortedByDescending { it.second }.map { it.first }
//            }
//        }
    }
}
