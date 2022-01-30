package com.luisansal.jetpack.features.manageusers

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.luisansal.jetpack.core.domain.entity.UserEntity
import com.luisansal.jetpack.utils.readString

class UsersMockDataHelper {

    fun getUsers(): List<UserEntity> {
        val jsonString = readString("features/manageusers/users.json")
        val users = Gson().fromJson<List<UserEntity>>(jsonString, object : TypeToken<List<UserEntity?>?>() {}.type)
        return users
    }

    fun getUser(): UserEntity {
        val jsonString = readString("features/manageusers/users.json")
        val users = Gson().fromJson<List<UserEntity>>(jsonString, object : TypeToken<List<UserEntity?>?>() {}.type)
        return users[0]
    }

}