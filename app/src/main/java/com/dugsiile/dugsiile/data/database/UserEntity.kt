package com.dugsiile.dugsiile.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dugsiile.dugsiile.models.UserData


@Entity(tableName = "user_table")
data class UserEntity(
    var User : UserData
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}
