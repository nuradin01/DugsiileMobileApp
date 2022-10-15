package com.dugsiile.dugsiile.data

import com.dugsiile.dugsiile.data.database.DugsiileDao
import com.dugsiile.dugsiile.data.database.StudentsEntity
import com.dugsiile.dugsiile.data.database.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val dugsiileDao: DugsiileDao
) {
    fun readStudents(): Flow<List<StudentsEntity>> {
        return dugsiileDao.readStudents()
    }
    fun readUser(): Flow<List<UserEntity>> {
        return dugsiileDao.readUser()
    }

    suspend fun insertStudents(studentsEntity: StudentsEntity) {
        dugsiileDao.insertStudents(studentsEntity)
    }

    suspend fun insertUser(userEntity: UserEntity) {
        dugsiileDao.insertUser(userEntity)
    }

    fun clearStudents() {
        dugsiileDao.clearStudents()
    }

    fun clearUser() {
        dugsiileDao.clearUser()
    }
}