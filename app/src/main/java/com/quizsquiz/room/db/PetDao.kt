package com.quizsquiz.room.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.quizsquiz.room.entities.Pet
import com.quizsquiz.room.entities.PetName
import com.quizsquiz.room.entities.User

@Dao
interface PetDao {
    @Insert
    suspend fun insertAllPets(list: List<Pet>)

    @Insert
    suspend fun insertPet(pet: Pet)

    @Query("SELECT * FROM pet")
    suspend fun getPets(): List<Pet>

    @Query("SELECT * FROM pet WHERE id = :userId")
    suspend fun getUserById(userId: Int): List<Pet>

    @Query("DELETE FROM pet")
    suspend fun deleteAllPets()
    @Transaction
    suspend fun insertUserIdInPet(user: User, list: List<Pet>) {
        deleteAllPets()
        val pets = mutableListOf<Pet>()
        for (p in list) {
            p.userId = user.id
            pets.add(p)
        }
        insertAllPets(pets)
    }

    @Query("SELECT * FROM pet")
    suspend fun getPetNames(): List<PetName>

    @Query("SELECT * FROM pet")
    suspend fun getPets1(): List<Pet>
}