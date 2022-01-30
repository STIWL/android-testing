package com.luisansal.jetpack.features.manageusers.integration

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.luisansal.jetpack.base.BaseIntegrationTest
import com.luisansal.jetpack.core.domain.entity.UserEntity
import com.luisansal.jetpack.domain.usecases.UserUseCase
import io.mockk.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.test.inject

class ManageUsersPresenterTest : BaseIntegrationTest(){

    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var newUserPresenter: NewUserPresenter
    val mView: NewUserMVP.View = mockk(relaxed = true)
    val userUseCase: UserUseCase by inject()

    @Before
    fun before() {
        newUserPresenter = NewUserPresenter(mView, userUseCase)
    }

    fun getMockedUser(): UserEntity {
        val userEntity: UserEntity = mockkClass(UserEntity::class)
        every { userEntity.id } returns 1
        every { userEntity.dni } returns "15258965"
        every { userEntity.name } returns "Pepito"
        every { userEntity.lastName } returns "Rodriguez"
        return userEntity
    }

    @Test
    fun `nuevo usuario`() {
        val user = getMockedUser()

        newUserPresenter.newUser(user)
        waitUiThread()
        verify {
            mView.notifyUserSaved(any())
        }

    }


}