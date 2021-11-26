package com.thinkingdobby.databaseproject.data

class User (
    val userId: String,
    val nickname: String,
    val contact: String,
    val info: String) {
    constructor() : this("", "", "", "")
}