package com.gazura.projectcapstone.data.profile

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val email: String,
    val name: String,
    val photo: String
)
