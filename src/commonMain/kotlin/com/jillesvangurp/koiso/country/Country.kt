package com.jillesvangurp.koiso.country

import com.jillesvangurp.koiso.continent.Continent
import com.jillesvangurp.serializationext.DEFAULT_JSON
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
internal data class CountryMeta(
    val code: String,
    val flag: String,
    @SerialName("dial_code")
    val dialCode: String,
)

@Serializable
/**
 * Representation of a nation as defined in
 * [ISO&nbsp;3166‑1](https://en.wikipedia.org/wiki/ISO_3166-1).
 */
data class Country(
    /** The localized name of the country */
    val name: String,
    @SerialName("alpha-2")
    val alpha2: String,
    @SerialName("alpha-3")
    val alpha3: String,
    @SerialName("country-code")
    val countryCode: Int,
    val region: String? = null,
    @SerialName("iso_3166-2")
    val iso31662: String,
    @SerialName("sub-region")
    val subRegion: String? = null,
    @SerialName("intermediate-region")
    val intermediateRegion: String? = null,
    /**
     * UN M49 numeric region code
     */
    @SerialName("region-code")
    @Serializable(with = NullableIntSerializer::class)
    val regionCode: Int? = null,
    /**
     * UN M49 numeric sub region code
     */
    @SerialName("sub-region-code")
    @Serializable(with = NullableIntSerializer::class)
    val subRegionCode: Int? = null,
    @SerialName("intermediate-region-code")
    val intermediateRegionCode: String? = null
) {
    /**
     * Unofficial alternative 2-letter codes such as `UK` for Great Britain or
     * `EL` for Greece. Returns `null` if no alias exists.
     */
    val alpha2Alias get() = if(alpha2 in commonAlpha2AliasCodes.values) commonAlpha2AliasCodes.entries.first { it.value == alpha2 }.key else null
    // original data uses "" instead of null as it should have
    object NullableIntSerializer : KSerializer<Int?> {
        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("NullableInt", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): Int? {
            return decoder.decodeString().takeIf { it.isNotBlank() }?.toIntOrNull()
        }

        override fun serialize(encoder: Encoder, value: Int?) {
            if (value != null) {
                encoder.encodeInt(value)
            } else {
                encoder.encodeString("")
            }
        }
    }

    companion object {
        /** All countries parsed from the bundled dataset. */
        val countries: List<Country> by lazy {
            DEFAULT_JSON.decodeFromString(ListSerializer(serializer()), allCountriesJson)
        }

        internal val countryMeta by lazy {
            DEFAULT_JSON.decodeFromString(ListSerializer(CountryMeta.serializer()), countryMetaData).associateBy { it.code }
        }

        val countryCodeMap by lazy {
            countries.associateBy { it.countryCode }
        }
        val countryAlpha2Map by lazy {
            countries.associateBy { it.alpha2 }
        }
        val countryAlpha3Map by lazy {
            countries.associateBy { it.alpha3 }
        }
    }
}

/** Emoji flag for this country if available. */
val Country.flag get() = Country.countryMeta[alpha2]?.flag

/** International dialing prefix in the form `+xx`. */
val Country.dialCode get() = Country.countryMeta[alpha2]?.dialCode

/** Lookup a country by its [ISO&nbsp;3166‑1 alpha‑2](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2) code. */
fun Country.Companion.findByAlpha2(alpha2: String): Country? {
    return countryAlpha2Map[alpha2.uppercase()] ?: commonAlpha2AliasCodes[alpha2]?.let { countryAlpha2Map[it] }
}

/** Lookup a country by its [ISO&nbsp;3166‑1 alpha‑3](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-3) code. */
fun Country.Companion.findByAlpha3(alpha3: String): Country? {
    return countryAlpha3Map[alpha3.uppercase()]
}

/** Lookup a country by its numeric [ISO&nbsp;3166‑1](https://en.wikipedia.org/wiki/ISO_3166-1_numeric) code. */
fun Country.Companion.findByCountryCode(code: String): Country? {
    return code.toIntOrNull()?.let { countryCodeMap[it] }
}

/** Resolve [codeOrName] using alpha‑2, alpha‑3, numeric codes or a case-insensitive country name. */
fun Country.Companion.resolve(codeOrName: String): Country? {
    return findByAlpha2(codeOrName)
        ?: findByAlpha3(codeOrName)
        ?: findByCountryCode(codeOrName)
        ?: countries.find { it.name.equals(codeOrName, ignoreCase = true) }
}

/** Returns the [Continent] this country belongs to. */
val Country.continent: Continent
    get() = when (region) {
        "Africa" -> Continent.AFRICA
        "Americas" -> when (subRegion) {
            "Northern America" -> Continent.NORTH_AMERICA
            "South America" -> Continent.SOUTH_AMERICA
            "Latin America and the Caribbean" -> Continent.SOUTH_AMERICA
            else -> Continent.OTHER_TERRITORIES
        }
        "Asia" -> Continent.ASIA
        "Europe" -> Continent.EUROPE
        "Oceania" -> Continent.OCEANIA
        else -> Continent.OTHER_TERRITORIES
    }
