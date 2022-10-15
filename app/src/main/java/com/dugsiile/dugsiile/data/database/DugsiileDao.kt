package com.dugsiile.dugsiile.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DugsiileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(studentsEntity: StudentsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(userEntity: UserEntity)

    @Query("SELECT * FROM students_table ORDER BY id ASC")
    fun readStudents(): Flow<List<StudentsEntity>>

    @Query("SELECT * FROM user_table ORDER BY id ASC")
    fun readUser(): Flow<List<UserEntity>>

    @Query("DELETE FROM students_table")
    fun clearStudents()

    @Query("DELETE FROM user_table")
    fun clearUser()
}