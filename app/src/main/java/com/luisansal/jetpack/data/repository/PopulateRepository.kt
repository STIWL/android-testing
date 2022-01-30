package com.luisansal.jetpack.data.repository

import android.util.Log
import com.luisansal.jetpack.data.database.BaseRoomDatabase
import com.luisansal.jetpack.core.domain.entity.UserEntity
import com.luisansal.jetpack.domain.entity.UserVisitJoin
import com.luisansal.jetpack.domain.entity.VisitEntity
import com.luisansal.jetpack.domain.model.maps.LatLng
import java.util.ArrayList

class PopulateRepository(db : BaseRoomDatabase) {

    private val userVisitDao = db.userVisitDao()
    private val userDao = db.userDao()
    private val visitDao = db.visitDao()

    fun start(){
        if(userVisitDao.count() <= 0)
            userVisitDao.deleteAll()
        if(userDao.count() <= 0)
            userDao.deleteAll()

        var user = UserEntity()
        user.names = "John"
        user.lastNames = "Doe"
        user.dni = "05159410"
        user.email = "johndoe@gmail.com"
        val userId = userDao.save(user)

        user = UserEntity()
        user.names = "Luis Alberto"
        user.lastNames = "Sánchez Saldaña"
        user.dni = "70668281"
        user.email = "luis@gmail.com"
        userDao.save(user)

        val users = ArrayList<UserEntity>()
        for (i in 0..500) {
            user = UserEntity()
            user.names = "User" + (i + 1)
            user.lastNames = "Apell" + (i + 1)
            user.dni = "dni" + (i + 1)
            users.add(user)
        }
        userDao.saveAll(users)

        for (i in 0..1000) {
            val visit = VisitEntity(location = LatLng(i*-2.0, i*3.0))
            val visitId = visitDao.save(visit)

            val userVisitJoin = UserVisitJoin(userId.toString(),visitId)
            userVisitDao.save(userVisitJoin)
        }

        Log.i("DB_ACTIONS", "Database Populated")

    }
}