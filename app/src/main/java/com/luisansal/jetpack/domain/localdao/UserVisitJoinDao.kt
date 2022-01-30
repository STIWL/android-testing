package com.luisansal.jetpack.domain.localdao

import com.luisansal.jetpack.domain.entity.UserVisitJoin
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.luisansal.jetpack.core.domain.entity.UserEntity
import com.luisansal.jetpack.domain.entity.VisitEntity

@Dao
interface UserVisitJoinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(userVisitJoin: UserVisitJoin) : Long

    @Query("DELETE FROM tbluser_tblvisit_join")
    fun deleteAll()

    @Query("select count(*) FROM tbluser_tblvisit_join")
    fun count() : Long

    @Query("DELETE FROM tbluser_tblvisit_join where userId = :userId")
    fun deleteAllByUser(userId :String)

    @Query("""
               SELECT * FROM tbluser
               INNER JOIN tbluser_tblvisit_join ON tbluser_tblvisit_join.userId = tbluser.id
               WHERE tbluser_tblvisit_join.visitId=:visitId
               """)
    fun findUsersForVisit(visitId: Long): List<UserEntity>

    @Query("""
               SELECT * FROM tblvisit
               INNER JOIN tbluser_tblvisit_join ON tbluser_tblvisit_join.visitId = tblvisit.id
               WHERE tbluser_tblvisit_join.userId=:userId
               """)
    fun findVisitsForUser(userId: String): List<VisitEntity>
}
