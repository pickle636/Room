package com.quizsquiz.room.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.quizsquiz.room.entities.User
import com.quizsquiz.room.entities.UserPets

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM user")
    suspend fun getUsers() : List<UserPets>

    @Query("SELECT * FROM user WHERE id = :id" )
    suspend fun findUserById(id: Int): User

    @Query("SELECT id FROM user")
    fun getAllUserIds(): List<Int>



}