package com.luisansal.jetpack.domain.localdao

import com.luisansal.jetpack.core.domain.entity.UserEntity

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(userEntity: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(userEntities: List<UserEntity>)

    @Query("DELETE FROM tbluser")
    fun deleteAll()

    @Query("select count(*) FROM tbluser")
    fun count() : Long

    @Query("SELECT * from tbluser ORDER BY names ASC")
    fun findAllUsers(): List<UserEntity>

    @Query("SELECT * from tbluser ORDER BY names ASC")
    fun findAllUsersInline(): List<UserEntity>

    // The Integer type parameter tells Room to use a PositionalDataSource
    // object, with position-based loading under the hood.
    @Query("SELECT * FROM tbluser ORDER BY names asc")
    fun findAllPaging(): DataSource.Factory<Int, UserEntity>

    @Query("SELECT * FROM tbluser WHERE names like :names ORDER BY names asc")
    fun findByNamePaging(names: String): DataSource.Factory<Int, UserEntity>

    @Query("SELECT * from tbluser where dni = :dni")
    fun findOneByDni(dni: String): UserEntity?

    @Query("SELECT * from tbluser where email = :email")
    fun findOneByEmail(email: String): UserEntity?

    @Query("SELECT * from tbluser where id = :id")
    fun findOneById(id: Long): UserEntity?

    @Query("DELETE FROM tbluser where dni = :dni")
    fun deleteUser(dni: String): Int

    @Query("DELETE FROM tbluser")
    fun deleteAllUser(): Int

}
