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

package com.maproductions.mohamedalaa.shoestore.view_model

import android.view.View
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.maproductions.mohamedalaa.shoestore.R
import com.maproductions.mohamedalaa.shoestore.common_value_objects.DataState

class ShoeListViewModel(state: SavedStateHandle) : ViewModel() {

    private companion object {
        const val STATE_DATA_STATE = "STATE_DATA_STATE"
    }

    val dataState = state.getLiveData(STATE_DATA_STATE, DataState.LOADING)

    fun addNewShoe(view: View) {
        view.findNavController().navigate(R.id.action_shoe_list_dest_to_shoe_detail_dest)
    }

}
