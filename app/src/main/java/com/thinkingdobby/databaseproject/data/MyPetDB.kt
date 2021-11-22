package com.thinkingdobby.databaseproject.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MyPetPost::class], version = 1)
abstract class MyPetDB : RoomDatabase() {
    abstract fun myPetDao(): MyPetDao

    companion object {
        private var INSTANCE: MyPetDB? = null

        fun getInstance(context: Context): MyPetDB? {
            if (INSTANCE == null) {
                synchronized(MyPetDB::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        MyPetDB::class.java, "MyPet.db"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}