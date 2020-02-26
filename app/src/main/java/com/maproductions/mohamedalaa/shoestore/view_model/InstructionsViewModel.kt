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

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.findNavController
import com.maproductions.mohamedalaa.shoestore.R
import com.maproductions.mohamedalaa.shoestore.shared_pref.sharedPref_HiddenSettings_SetShowInstructionsScreen
import mohamedalaa.mautils.core_android.extensions.application

class InstructionsViewModel(application: Application) : AndroidViewModel(application) {

    fun onGoAheadClick(view: View) {
        application.sharedPref_HiddenSettings_SetShowInstructionsScreen(false)

        view.findNavController().navigate(R.id.action_global_shoe_list_dest)
    }

}
