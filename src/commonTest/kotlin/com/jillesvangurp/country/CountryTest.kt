package com.jillesvangurp.country

import com.jillesvangurp.koiso.country.Country
import com.jillesvangurp.koiso.country.dialCode
import com.jillesvangurp.koiso.country.flag
import com.jillesvangurp.koiso.country.resolveCountry
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class CountryTest {

    @Test
    fun shouldFindCountry() {
        val country = Country.resolveCountry("DE")
        country.shouldNotBeNull()
        country.name shouldBe "Germany"
        country.flag shouldBe "🇩🇪"
        country.dialCode shouldBe "+49"
    }
}
