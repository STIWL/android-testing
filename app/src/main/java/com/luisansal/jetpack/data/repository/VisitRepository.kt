package com.luisansal.jetpack.data.repository

import com.luisansal.jetpack.data.database.BaseRoomDatabase
import com.luisansal.jetpack.domain.entity.UserVisitJoin
import com.luisansal.jetpack.domain.entity.VisitEntity

class VisitRepository(db: BaseRoomDatabase) {

    private val userVisitDao = db.userVisitDao()
    private val userDao = db.userDao()
    private val visitDao = db.visitDao()

    fun getByUser(email: String): List<VisitEntity>? {
        val user = userDao.findOneByEmail(email);
        return user?.id?.let { userVisitDao.findVisitsForUser(it) }
    }

    fun saveUser(visitId: Long, userId: String): Boolean {
        val userVisitJoin = UserVisitJoin(userId, visitId)
        return userVisitDao.save(userVisitJoin) > 0
    }

    fun deleteAllByUser(userId: String) {
        userVisitDao.deleteAllByUser(userId)
    }

    fun deleteAll() {
        userVisitDao.deleteAll()
    }

    fun save(visitEntity: VisitEntity): Long {
        return visitDao.save(visitEntity)
    }
}
