package com.luisansal.jetpack.features.manageusers

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.luisansal.jetpack.core.domain.entity.UserEntity
import com.luisansal.jetpack.domain.usecases.UserUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkClass
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ManageUsersPresenterTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var newUserPresenter: NewUserPresenter
    val mView: NewUserMVP.View = mockk(relaxed = true)
    val userUseCase: UserUseCase = mockk()

    @Before
    fun before() {
        newUserPresenter = NewUserPresenter(mView, userUseCase)
    }

    fun getMockedUser(): UserEntity {
        val userEntity: UserEntity = mockkClass(UserEntity::class)
        every { userEntity.id } returns 1
        every { userEntity.dni } returns "15258968"
        every { userEntity.name } returns "Pepito"
        every { userEntity.lastName } returns "Rodriguez"
        return userEntity
    }

    @Test
    fun `nuevo usuario`() {

        val user = getMockedUser()

        every { userUseCase.newUser(user) } returns user

        newUserPresenter.newUser(user)

        verify {
            mView.notifyUserSaved(any())
        }

    }

    @Test
    fun `eliminar usuario`(){
        val dni = "12345678"

        every { userUseCase.deleUser(any()) } returns true

        newUserPresenter.deleteUser(dni)

        verify {
            mView.notifyUserDeleted()
            mView.resetView()
        }

    }


}