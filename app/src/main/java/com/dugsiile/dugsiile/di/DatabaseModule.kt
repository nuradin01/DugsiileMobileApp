package com.dugsiile.dugsiile.di

import android.content.Context
import androidx.room.Room
import com.dugsiile.dugsiile.data.database.DugsiileDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        DugsiileDatabase::class.java,
        "dugsiile_database"
    ).build()

    @Singleton
    @Provides
    fun provideDao(database: DugsiileDatabase) = database.dugsiileDao()

}