package com.dugsiile.dugsiile.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dugsiile.dugsiile.models.Student

@Entity(tableName = "students_table")
data class StudentsEntity(
    var student: Student
){
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}
