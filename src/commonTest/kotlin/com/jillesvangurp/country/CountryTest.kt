package com.jillesvangurp.country

import com.jillesvangurp.koiso.country.Country
import com.jillesvangurp.koiso.country.dialCode
import com.jillesvangurp.koiso.country.findByAlpha2
import com.jillesvangurp.koiso.country.findByAlpha3
import com.jillesvangurp.koiso.country.flag
import com.jillesvangurp.koiso.country.resolve
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
}
