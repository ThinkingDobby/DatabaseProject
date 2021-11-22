package com.thinkingdobby.databaseproject.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MyPetDao {
    @Query("SELECT * FROM MyPet ORDER BY writeTime")
    fun getAll(): LiveData<List<MyPetPost>>

    @Query("SELECT * FROM MyPet WHERE postId = :num")
    fun getByMyPetPostNo(num: Int): LiveData<List<MyPetPost>>

    @Query("UPDATE MyPet Set petImage = :newImage WHERE postId = :num")
    fun updateByMyPetPostNo(num: Int, newImage: ByteArray)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(myPet: MyPetPost)

    @Delete
    fun delete(myPet: MyPetPost)

    @Query("DELETE FROM MyPet")
    fun deleteAll()
}