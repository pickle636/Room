package com.quizsquiz.room.entities

import androidx.room.Embedded
import androidx.room.Relation


data class UserPets(
    @Embedded
    val user: User,
    @Relation(parentColumn = "id", entityColumn = "userId")
    val list: List<Pet>)


