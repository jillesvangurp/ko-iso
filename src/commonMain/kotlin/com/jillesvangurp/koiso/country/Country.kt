package com.jillesvangurp.koiso.country

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
data class Country(
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

val Country.flag get() = Country.countryMeta[alpha2]?.flag
val Country.dialCode get() = Country.countryMeta[alpha2]?.dialCode

fun Country.Companion.findByAlpha2(alpha2: String): Country? {
    return countryAlpha2Map[alpha2.uppercase()]
}

fun Country.Companion.findByAlpha3(alpha3: String): Country? {
    return countryAlpha3Map[alpha3.uppercase()]
}

fun Country.Companion.findByCountryCode(code: String): Country? {
    return code.toIntOrNull()?.let { countryCodeMap[it] }
}

fun Country.Companion.resolveCountry(codeOrName: String): Country? {
    return findByAlpha2(codeOrName)
        ?: findByAlpha3(codeOrName)
        ?: findByCountryCode(codeOrName)
        ?: countries.find { it.name.equals(codeOrName, ignoreCase = true) }
}
