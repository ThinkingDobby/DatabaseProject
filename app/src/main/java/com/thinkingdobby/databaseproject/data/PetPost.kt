package com.thinkingdobby.databaseproject.data

class PetPost {
    var postId = ""
    var writeTime: Any = "" // 정렬을 위한 시간
    var writer = "" // 계정 생성 시 writer 정보 추가 - 게시자 정보 확인 시 게시자 이름으로 정보 검색

    var location = ""
    var lostTime = ""
    var breed = ""
    var name = ""
    var sex = ""
    var length = ""

    var info = ""   // 특징
    var stat = ""   // 상태

    var find: Boolean = false
}