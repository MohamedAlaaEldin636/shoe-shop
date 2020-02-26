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
import androidx.lifecycle.AndroidViewModel
import com.maproductions.mohamedalaa.shoestore.common_value_objects.Shoe
import com.maproductions.mohamedalaa.shoestore.common_value_objects.User
import mohamedalaa.mautils.core_android.extensions.application

// -- AppDatabase -- //

private fun Context.appDatabase() = AppDatabase.getInstance(this)

private fun AndroidViewModel.appDatabase() = application.appDatabase()

// -- DAOs -- //

private fun AndroidViewModel.usersDao() = appDatabase().usersDao()

private fun AndroidViewModel.shoesDao() = appDatabase().shoesDao()

// -- Users Dao -- //

fun AndroidViewModel.usersQueryAll() = usersDao().queryAll()

suspend fun AndroidViewModel.usersQueryPasswordWhereEMailAddress(eMailAddress: String) = usersDao().queryPasswordWhereEMailAddress(eMailAddress)

suspend fun AndroidViewModel.usersInsert(user: User) = usersDao().insert(user)

suspend fun AndroidViewModel.usersUpdatePasswordWhereEMailAddress(
    password: String,
    eMailAddress: String
) = usersDao().updatePasswordWhereEMailAddress(password, eMailAddress)

// -- Shoes Dao -- //

fun AndroidViewModel.shoesQueryAll() = shoesDao().queryAll()

suspend fun AndroidViewModel.shoesInsert(shoe: Shoe) = shoesDao().insert(shoe)

suspend fun AndroidViewModel.shoesUpdate(shoe: Shoe) = shoesDao().update(shoe)

suspend fun AndroidViewModel.shoesDelete(shoeId: Int) = shoesDao().delete(shoeId)
