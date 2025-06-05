package com.jillesvangurp.koiso.continent

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Continents as defined by **UN M49** numeric region codes, with the
 * widely-used 2-letter continent codes (BCP-47 / GeoIP, e.g. `EU`, `NA`).
 *
 * | Continent        | 2-letter | UN M49 |
 * |------------------|:--------:|:------:|
 * | Africa           | AF | 002 |
 * | Antarctica       | AN | 010 |
 * | Asia             | AS | 142 |
 * | Europe           | EU | 150 |
 * | North America    | NA | 003 |
 * | South America    | SA | 005 |
 * | Oceania          | OC | 009 |  [oai_citation:0‡Wikipedia](https://en.wikipedia.org/wiki/UN_M49?utm_source=chatgpt.com) [oai_citation:1‡fastly.com](https://www.fastly.com/documentation/reference/vcl/variables/geolocation/client-geo-continent-code/?utm_source=chatgpt.com)
 *
 */
@Serializable
enum class Continent(
    /** The conventional 2-letter continent code (`EU`, `AS`, …) */
    @SerialName("alpha-2") val alpha2: String,
    /** The 3-digit UN M49 numeric code. */
    val m49: Int,
    val english: String,
    val german: String,
    val french: String,
    val spanish: String,
    val chinese: String,
    val regions: List<String>
) {
    AFRICA(
        "AF", 2,
        "Africa", "Afrika", "Afrique", "África", "非洲",
        listOf("Northern Africa", "Sub-Saharan Africa", "Eastern Africa", "Western Africa", "Middle Africa", "Southern Africa")
    ),
    ANTARCTICA(
        "AN", 10,
        "Antarctica", "Antarktis", "Antarctique", "Antártida", "南极洲",
        listOf("Antarctic Region")
    ),
    ASIA(
        "AS", 142,
        "Asia", "Asien", "Asie", "Asia", "亚洲",
        listOf("Central Asia", "Eastern Asia", "Southeastern Asia", "Southern Asia", "Western Asia")
    ),
    EUROPE(
        "EU", 150,
        "Europe", "Europa", "Europe", "Europa", "欧洲",
        listOf("Northern Europe", "Southern Europe", "Eastern Europe", "Western Europe")
    ),
    NORTH_AMERICA(
        "NA", 3,
        "North America", "Nordamerika", "Amérique du Nord", "América del Norte", "北美洲",
        listOf("Northern America", "Central America", "Caribbean")
    ),
    SOUTH_AMERICA(
        "SA", 5,
        "South America", "Südamerika", "Amérique du Sud", "América del Sur", "南美洲",
        listOf("Andean States", "Southern Cone", "Brazil", "Caribbean South America")
    ),
    OCEANIA(
        "OC", 9,
        "Oceania", "Ozeanien", "Océanie", "Oceanía", "大洋洲",
        listOf("Australia and New Zealand", "Melanesia", "Micronesia", "Polynesia")
    ),
    OTHER_TERRITORIES(
        "OT", -1,
        "Other Territories", "Andere Gebiete", "Autres territoires", "Otros territorios", "其他地区",
        emptyList()
    );

    companion object {
        /** Lookup by 2-letter code (case-insensitive). */
        fun fromAlpha2(code: String): Continent? =
            entries.firstOrNull { it.alpha2.equals(code, ignoreCase = true) }

        /** Lookup by [UN&nbsp;M49](https://en.wikipedia.org/wiki/UN_M49) numeric code. */
        fun fromM49(code: Int): Continent? =
            entries.firstOrNull { it.m49 > 0 && it.m49 == code }

        /** Flexible resolver accepting either alpha-2 code, UN M49 numeric code or enum name. */
        fun resolve(codeOrName: String): Continent? =
            fromAlpha2(codeOrName)
                ?: codeOrName.toIntOrNull()?.let { fromM49(it) }
                ?: entries.firstOrNull { it.name.equals(codeOrName, ignoreCase = true) }
    }
}
