package com.quizsquiz.room.hilt

import android.content.Context
import androidx.room.Dao
import com.quizsquiz.room.Repository
import com.quizsquiz.room.db.DatabaseRoom
import com.quizsquiz.room.db.PetDao
import com.quizsquiz.room.db.UserDao
import com.quizsquiz.room.ui.MainViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Module
@InstallIn(ApplicationComponent::class)
class ApplicationModule {
    @Provides
    fun provideUserDao(@ApplicationContext appContext: Context) : UserDao {
        return DatabaseRoom.getInstance(appContext).userDao
    }
    @Provides
    fun providePetDao(@ApplicationContext appContext: Context) : PetDao {
        return DatabaseRoom.getInstance(appContext).petDao
    }

    @Provides
    fun provideBookDBRepository(userDao: UserDao, petDao: PetDao) = Repository(userDao, petDao)

}