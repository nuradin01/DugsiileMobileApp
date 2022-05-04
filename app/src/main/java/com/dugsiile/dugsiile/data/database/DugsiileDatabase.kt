package com.dugsiile.dugsiile.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [StudentsEntity::class, UserEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DugsiileTypeConverter::class)
abstract class DugsiileDatabase: RoomDatabase() {

    abstract fun dugsiileDao(): DugsiileDao

}