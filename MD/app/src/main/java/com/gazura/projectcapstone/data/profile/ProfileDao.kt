package com.gazura.projectcapstone.data.profile

import androidx.room.*

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profile WHERE email = :email")
    fun getProfileByEmail(email: String): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateProfile(profile: ProfileEntity)

    @Query("UPDATE profile SET name = :name, photo = :photo WHERE email = :email")
    suspend fun updateProfile(email: String, name: String, photo: String)

}

