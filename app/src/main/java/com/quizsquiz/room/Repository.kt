package com.quizsquiz.room

import com.quizsquiz.room.db.PetDao
import com.quizsquiz.room.db.UserDao
import com.quizsquiz.room.entities.Pet
import com.quizsquiz.room.entities.User
import com.quizsquiz.room.entities.UserPets
import javax.inject.Inject

class Repository @Inject constructor(private val userDao: UserDao, private val petDao: PetDao) {
    suspend fun getUser() : List<UserPets> {
        return userDao.getUsers()
    }

    suspend fun getPets() : List<Pet> {
        return petDao.getPets()
    }

    suspend fun getAllUserIds(): List<Int> {
        return userDao.getAllUserIds()
    }

    suspend fun insertUser(user: User) {
        return userDao.insertUser(user)
    }

    suspend fun insertPet(pet: Pet) {
        return petDao.insertPet(pet)

    }

    suspend fun findUserById(id: Int): User {
        return userDao.findUserById(id)
    }



}