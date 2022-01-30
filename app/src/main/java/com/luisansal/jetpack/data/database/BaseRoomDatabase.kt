package com.luisansal.jetpack.data.database

import android.content.Context
import android.os.AsyncTask
import com.luisansal.jetpack.domain.localdao.UserDao
import com.luisansal.jetpack.domain.localdao.UserVisitJoinDao
import com.luisansal.jetpack.domain.localdao.VisitDao
import com.luisansal.jetpack.core.domain.entity.UserEntity
import com.luisansal.jetpack.domain.entity.VisitEntity
import com.luisansal.jetpack.domain.converter.LatLngConverter
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.luisansal.jetpack.domain.converter.OwnLatLngConverter
import com.luisansal.jetpack.domain.entity.UserVisitJoin

@Database(entities = [UserEntity::class, VisitEntity::class, UserVisitJoin::class], version = 9)
@TypeConverters(LatLngConverter::class,OwnLatLngConverter::class)
abstract class BaseRoomDatabase : RoomDatabase() {

    var isTest = false

    abstract fun userDao(): UserDao

    abstract fun visitDao(): VisitDao

    abstract fun userVisitDao(): UserVisitJoinDao

    private class PopulateDbAsync(baseRoomDatabase: BaseRoomDatabase) : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg voids: Void): Void? {

            return null
        }
    }

    companion object {

        @Volatile
        private var INSTANCE: BaseRoomDatabase? = null

        fun getDatabase(context: Context): BaseRoomDatabase? {
            if (INSTANCE == null) {
                synchronized(BaseRoomDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder<BaseRoomDatabase>(context, BaseRoomDatabase::class.java, "myDatabase")
                                .fallbackToDestructiveMigration()
                                .allowMainThreadQueries()
                                .addCallback(sRoomDatabaseCallback)
                                .build()
                    }
                }
            }
            return INSTANCE
        }

        private val sRoomDatabaseCallback = object : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                if (!INSTANCE?.isTest!!)
                    PopulateDbAsync(INSTANCE!!).execute()
            }
        }
    }
}