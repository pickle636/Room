package com.quizsquiz.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Pet(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var userId: Int,
    var name: String,
    var birthday: String?
)