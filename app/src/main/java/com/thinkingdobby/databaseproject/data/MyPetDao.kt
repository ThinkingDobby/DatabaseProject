package com.thinkingdobby.databaseproject.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MyPetDao {
    @Query("SELECT * FROM MyPet ORDER BY writeTime")
    fun getAll(): LiveData<List<MyPetPost>>

    @Query("UPDATE MyPet Set petName = :petName, writeTime = :writeTime, petBreed = :petBreed, petSex = :petSex, petLength = :petLength, petInfo = :petInfo, petImage = :petImage, imgOt = :imgOt WHERE postId = :num")
    fun updateByMyPetPostNo(num: Long, writeTime: String, petName: String, petBreed: String, petSex: String, petLength: String, petInfo: String, petImage: ByteArray, imgOt: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(myPet: MyPetPost)

    @Delete
    fun delete(myPet: MyPetPost)

    @Query("DELETE FROM MyPet")
    fun deleteAll()
}