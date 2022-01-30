package com.luisansal.jetpack.features.manageusers

import com.luisansal.jetpack.features.manageusers.validation.UserValidation
import org.amshove.kluent.shouldBe
import org.junit.Test

class UserDaoValidationTest {

    @Test
    fun `Cantidad de digitos de Dni válidl`(){
        val dni = "70558281"
        UserValidation.validateDni(dni) shouldBe true
    }
}