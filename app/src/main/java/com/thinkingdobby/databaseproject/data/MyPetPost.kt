package com.thinkingdobby.databaseproject.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "MyPet")
class MyPetPost (@PrimaryKey var postId: Long?,
    @ColumnInfo(name = "writeTime") var writeTime: String?,
    @ColumnInfo(name = "petName") var petName: String?,
    @ColumnInfo(name = "petInfo") var petInfo: String?,
    @ColumnInfo(name = "petBreed") var petBreed: String?,
    @ColumnInfo(name = "petSex") var petSex: String?,
    @ColumnInfo(name = "petLength") var petLength: String?,
    @ColumnInfo(name = "petImage", typeAffinity = ColumnInfo.BLOB) var petImage: ByteArray?,
    @ColumnInfo(name = "imgOt") var imgOt: Int?) : Parcelable {
    constructor() : this(null, "", "", "", "", "", "", null, null)
}