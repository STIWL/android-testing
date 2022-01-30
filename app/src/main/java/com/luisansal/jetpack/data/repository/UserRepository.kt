package com.luisansal.jetpack.data.repository

import com.luisansal.jetpack.domain.localdao.UserDao
import com.luisansal.jetpack.data.database.BaseRoomDatabase
import com.luisansal.jetpack.core.domain.entity.UserEntity

import androidx.paging.DataSource

class UserRepository(db: BaseRoomDatabase) {

    private val userDao: UserDao = db.userDao()

    val allUserEntities: List<UserEntity>
        get() = userDao.findAllUsers()

    val allUsersInline: List<UserEntity>
        get() = userDao.findAllUsersInline()

    val allUsersPaging: DataSource.Factory<Int, UserEntity>
        get() = userDao.findAllPaging()

    fun getByNamePaging(names: String): DataSource.Factory<Int, UserEntity> {
        return userDao.findByNamePaging("%$names%")
    }

    fun save(userEntity: UserEntity): Long {
        return userDao.save(userEntity)
    }

    fun getUserByDni(dni: String): UserEntity? {
        return userDao.findOneByDni(dni)
    }

    fun getUserByEmail(email: String): UserEntity? {
        return userDao.findOneByEmail(email)
    }

    fun getUserById(id: Long): UserEntity? {
        return userDao.findOneById(id)
    }

    fun deleteUser(dni: String): Boolean {
        return userDao.deleteUser(dni) == 1
    }

    fun deleteUsers(): Boolean {
        return userDao.deleteAllUser() == 1
    }

}
