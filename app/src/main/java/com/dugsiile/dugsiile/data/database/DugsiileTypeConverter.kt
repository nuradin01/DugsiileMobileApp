package com.dugsiile.dugsiile.data.database

import androidx.room.TypeConverter
import com.dugsiile.dugsiile.models.Student
import com.dugsiile.dugsiile.models.StudentData
import com.dugsiile.dugsiile.models.UserData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DugsiileTypeConverter {

    var gson = Gson()

    @TypeConverter
    fun studentToString(student: Student): String {
        return gson.toJson(student)
    }

    @TypeConverter
    fun stringToStudent(data: String): Student {
        val listType = object : TypeToken<Student>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun studentDataToString(studentData: StudentData): String {
        return gson.toJson(studentData)
    }

    @TypeConverter
    fun stringToStudentData(data: String): StudentData {
        val listType = object : TypeToken<StudentData>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun userToString(user:UserData): String {
        return gson.toJson(user)
    }

    @TypeConverter
    fun stringToUser(data: String): UserData {
        val listType = object : TypeToken<UserData>() {}.type
        return gson.fromJson(data, listType)
    }
}