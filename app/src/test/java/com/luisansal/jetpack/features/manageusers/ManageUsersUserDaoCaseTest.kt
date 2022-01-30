package com.luisansal.jetpack.features.manageusers

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.luisansal.jetpack.data.repository.UserRepository
import com.luisansal.jetpack.core.domain.entity.UserEntity
import com.luisansal.jetpack.domain.usecases.UserUseCase
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ManageUsersUserDaoCaseTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var userUseCase: UserUseCase
    val userRepository : UserRepository = mockk()

    @Before
    fun before() {
        userUseCase = UserUseCase(userRepository)

    }

    @Test
    fun `nuevo usuario`() {
        val user = getMockedUser()

        every { userRepository.save(user) } returns 1

        userUseCase.newUser(user)

        verify {
            userRepository.save(user)
        }
    }

    @Test
    fun `obtener usuario`() {
        val user = getMockedUser()
        val dni = user.dni

        every { userRepository.getUserByDni(dni) } returns user

        userUseCase.getUser(dni)

        verify {
            userRepository.getUserByDni(dni)
        }
    }

    @Test
    fun `validar usuario duplicado`() {
        val user = getMockedUser()
        val dni = user.dni

        every { userRepository.getUserByDni(any()) } returns null

        userUseCase.validateDuplicatedUser(dni) shouldNotBe true
    }

    @Test
    fun `lista de usuarios`() = runBlocking {
        val users = UsersMockDataHelper().getUsers()

        every { userRepository.allUserEntities } returns users

        userUseCase.getAllUser()

        coVerify {
            userRepository.allUserEntities
        }
    }

    @Test
    fun `eliminar usuario`() {
        val dni = "12345678"

        every { userRepository.deleteUser(any()) } returns true

        userUseCase.deleUser(dni)

        verify {
            userRepository.deleteUser(any())
        }

    }

    fun getMockedUser(): UserEntity {
        val userEntity: UserEntity = mockkClass(UserEntity::class)
        every { userEntity.id } returns 1
        every { userEntity.dni } returns "1525896"
        every { userEntity.name } returns "Pepito"
        every { userEntity.lastName } returns "Rodriguez"
        return userEntity
    }
}