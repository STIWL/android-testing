package com.luisansal.jetpack.domain.localdao

import com.luisansal.jetpack.domain.entity.VisitEntity

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface VisitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(visitEntity: VisitEntity) : Long

    @Query("DELETE FROM tblvisit")
    fun deleteAll()

    @Query("SELECT * from tblvisit where id = :id")
    fun findOneById(id: Long?): LiveData<VisitEntity>
}
