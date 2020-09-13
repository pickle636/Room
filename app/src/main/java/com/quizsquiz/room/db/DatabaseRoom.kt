package com.quizsquiz.room.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.quizsquiz.room.entities.Pet
import com.quizsquiz.room.entities.User


@Database(entities = [User::class, Pet::class], version = 2)
abstract class DatabaseRoom: RoomDatabase() {

    abstract val userDao: UserDao
    abstract val petDao: PetDao

    companion object {
        @Volatile
        private var INSTANCE: DatabaseRoom? = null

        fun getInstance(context: Context): DatabaseRoom {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        DatabaseRoom::class.java,
                        "database"
                    )
//                        .fallbackToDestructiveMigration()
                        .addMigrations(MIGRATION_1_2)
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}

val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE pet ADD COLUMN birthday TEXT")
    }
}