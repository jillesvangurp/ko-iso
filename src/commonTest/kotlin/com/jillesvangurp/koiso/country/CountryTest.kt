package com.jillesvangurp.koiso.country

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class CountryTest {

    @Test
    fun shouldFindCountry() {
        val country = Country.resolve("DE")
        country.shouldNotBeNull()
        country.name shouldBe "Germany"
        country.flag shouldBe "🇩🇪"
        country.dialCode shouldBe "+49"
    }

    @Test
    fun lookupsShouldBeCaseInsensitive() {
        Country.findByAlpha2("DE")?.alpha2 shouldBe "DE"
        Country.findByAlpha2("de")?.alpha2 shouldBe "DE"
        Country.findByAlpha3("dEu")?.alpha2 shouldBe "DE"
        Country.resolve("DeU")?.alpha2 shouldBe "DE"
    }

    @Test
    fun shouldFindUKAndGreece() {
        Country.findByAlpha2("GB")?.alpha2 shouldBe "GB"
        Country.findByAlpha2("GB")?.alpha2Alias shouldBe "UK"
        Country.findByAlpha2("UK")?.alpha2 shouldBe "GB"
        Country.findByAlpha2("UK")?.alpha2Alias shouldBe "UK"
        Country.findByAlpha2("GR")?.alpha2 shouldBe "GR"
        Country.findByAlpha2("GR")?.alpha2Alias shouldBe "EL"
        Country.findByAlpha2("EL")?.alpha2 shouldBe "GR"
        Country.findByAlpha2("EL")?.alpha2Alias shouldBe "EL"
    }
}
