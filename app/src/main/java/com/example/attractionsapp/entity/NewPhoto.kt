package com.example.attractionsapp.entity

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class NewPhoto (
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int? = null,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "url")  val url: String
)