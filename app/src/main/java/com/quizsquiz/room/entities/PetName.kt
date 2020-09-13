package com.quizsquiz.room.entities

import androidx.room.Embedded
import androidx.room.Relation


data class PetName(
    @Embedded val user: User,
    @Relation(entity = Pet::class, parentColumn = "id", entityColumn = "userId", projection = ["name"]) val names: List<String>
)