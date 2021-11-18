package com.thinkingdobby.databaseproject.data

import android.os.Parcel
import android.os.Parcelable

class PetPost() : Parcelable {
    var postId = ""
    var writeTime: Any = "" // 정렬을 위한 시간
    var writer = "" // 계정 생성 시 writer 정보 추가 - 게시자 정보 확인 시 게시자 이름으로 정보 검색

    var location = ""
    var time = ""
    var breed = ""
    var name = ""
    var sex = ""
    var length = ""

    var info = ""   // 특징
    var stat = ""   // 상태

    var find: Boolean = false

    constructor(parcel: Parcel) : this() {
        postId = parcel.readString()!!
        writer = parcel.readString()!!
        location = parcel.readString()!!
        time = parcel.readString()!!
        breed = parcel.readString()!!
        name = parcel.readString()!!
        sex = parcel.readString()!!
        length = parcel.readString()!!
        info = parcel.readString()!!
        stat = parcel.readString()!!
        find = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(postId)
        parcel.writeString(writer)
        parcel.writeString(location)
        parcel.writeString(time)
        parcel.writeString(breed)
        parcel.writeString(name)
        parcel.writeString(sex)
        parcel.writeString(length)
        parcel.writeString(info)
        parcel.writeString(stat)
        parcel.writeByte(if (find) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PetPost> {
        override fun createFromParcel(parcel: Parcel): PetPost {
            return PetPost(parcel)
        }

        override fun newArray(size: Int): Array<PetPost?> {
            return arrayOfNulls(size)
        }
    }
}