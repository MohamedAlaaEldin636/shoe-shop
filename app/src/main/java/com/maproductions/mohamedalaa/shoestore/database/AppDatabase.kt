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

package com.maproductions.mohamedalaa.shoestore.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.maproductions.mohamedalaa.shoestore.common_value_objects.Shoe
import com.maproductions.mohamedalaa.shoestore.common_value_objects.User
import com.maproductions.mohamedalaa.shoestore.database.daos.ShoesDao
import com.maproductions.mohamedalaa.shoestore.database.daos.UsersDao

@Database(
    entities = [
        User::class,
        Shoe::class
    ],
    version = AppDatabase.DATABASE_VERSION,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usersDao(): UsersDao

    abstract fun shoesDao(): ShoesDao

    companion object {

        internal const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "textEditor-db"

        // For singleton instantiation isa.
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .build()
        }

    }

}

