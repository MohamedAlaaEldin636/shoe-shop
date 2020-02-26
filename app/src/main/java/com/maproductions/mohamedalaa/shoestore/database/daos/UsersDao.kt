/*
 * Copyright (c) 2019 Mohamed Alaa
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.maproductions.mohamedalaa.shoestore.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.maproductions.mohamedalaa.shoestore.common_value_objects.User

@Dao
interface UsersDao {

    // -- Query -- //

    @Query("SELECT * FROM users")
    fun queryAll(): LiveData<List<User>>

    @Query("SELECT password FROM users WHERE eMailAddress = :eMailAddress")
    suspend fun queryPasswordWhereEMailAddress(eMailAddress: String): String

    // -- Insert -- //

    @Insert(entity = User::class)
    suspend fun insert(user: User): Long

    // -- Update -- //

    @Query("UPDATE users SET password = :password WHERE eMailAddress = :eMailAddress")
    suspend fun updatePasswordWhereEMailAddress(password: String, eMailAddress: String): Int

}
