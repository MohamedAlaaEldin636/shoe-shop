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
import androidx.room.Update
import com.maproductions.mohamedalaa.shoestore.common_value_objects.Shoe

@Dao
interface ShoesDao {

    // -- Query -- //

    @Query("SELECT * FROM shoes")
    fun queryAll(): LiveData<List<Shoe>>

    // -- Insert -- //

    @Insert(entity = Shoe::class)
    suspend fun insert(shoe: Shoe): Long

    // -- Update -- //

    @Update(entity = Shoe::class)
    suspend fun update(shoe: Shoe): Int

    // -- Delete -- //

    @Query("DELETE FROM shoes WHERE id = :shoeId")
    suspend fun delete(shoeId: Int): Int

}
