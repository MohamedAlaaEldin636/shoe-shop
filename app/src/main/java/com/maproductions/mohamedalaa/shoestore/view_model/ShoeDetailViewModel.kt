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
import androidx.lifecycle.*
import androidx.navigation.findNavController
import com.maproductions.mohamedalaa.shoestore.common_value_objects.Shoe
import com.maproductions.mohamedalaa.shoestore.databinding.FragmentShoeDetailBinding
import com.maproductions.mohamedalaa.shoestore.extensions.asBundle
import com.maproductions.mohamedalaa.shoestore.view.ShoeDetailFragment
import com.maproductions.mohamedalaa.shoestore.view.ShoeDetailFragmentArgs
import com.maproductions.mohamedalaa.shoestore.R
import com.maproductions.mohamedalaa.shoestore.database.shoesDelete
import com.maproductions.mohamedalaa.shoestore.database.shoesInsert
import com.maproductions.mohamedalaa.shoestore.database.shoesUpdate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mohamedalaa.mautils.core_android.extensions.*

class ShoeDetailViewModel(application: Application, state: SavedStateHandle) : AndroidViewModel(application) {

    companion object {
        private const val STATE_ID = "STATE_ID"

        private const val STATE_NAME = "STATE_NAME"
        const val STATE_ERROR_MSG_NAME = "STATE_ERROR_MSG_NAME"

        private const val STATE_COMPANY = "STATE_COMPANY"
        const val STATE_ERROR_MSG_COMPANY = "STATE_IS_VALID_COMPANY"

        private const val STATE_SIZE = "STATE_SIZE"
        const val STATE_ERROR_MSG_SIZE = "STATE_IS_VALID_SIZE"

        private const val STATE_DESCRIPTION = "STATE_DESCRIPTION"
        const val STATE_ERROR_MSG_DESCRIPTION = "STATE_IS_VALID_DESCRIPTION"
    }

    private val safeArgs = ShoeDetailFragmentArgs.fromBundle(state.asBundle())

    val id = state.getLiveData(STATE_ID, safeArgs.shoeId)

    val name = state.getLiveData(STATE_NAME, safeArgs.shoeName)
    val errorMsgName = state.getLiveData(STATE_ERROR_MSG_NAME, "")

    val company = state.getLiveData(STATE_COMPANY, safeArgs.shoeCompany)
    val errorMsgCompany = state.getLiveData(STATE_ERROR_MSG_COMPANY, "")

    val size = state.getLiveData(STATE_SIZE, safeArgs.shoeSize)
    val errorMsgSize = state.getLiveData(STATE_ERROR_MSG_SIZE, "")

    val description = state.getLiveData(STATE_DESCRIPTION, safeArgs.shoeDescription)
    val errorMsgDescription = state.getLiveData(STATE_ERROR_MSG_DESCRIPTION, "")

    /** When it's [LiveData.getValue] is not-null then we have all the current shoes in the database isa. */
    val allShoes = MutableLiveData<List<Shoe>>()

    fun setValid(key: String) {
        when (key) {
            STATE_ERROR_MSG_NAME -> errorMsgName.value = ""
            STATE_ERROR_MSG_COMPANY -> errorMsgCompany.value = ""
            STATE_ERROR_MSG_SIZE -> errorMsgSize.value = ""
            STATE_ERROR_MSG_DESCRIPTION -> errorMsgDescription.value = ""
        }
    }

    fun onSaveClick(view: View) {
        val fragment = view.findBindingOrNull<FragmentShoeDetailBinding>()
            ?.lifecycleOwner as? ShoeDetailFragment ?: return

        // Clear all errors isa.
        clearAllErrorMsg()

        // Check that all fields are not empty isa.
        var atLeastOneNotValidValue = false
        if (name.value.isNullOrEmpty()) {
            errorMsgName.value = getString(R.string.this_field_can_t_be_empty)
            atLeastOneNotValidValue = true
        }
        if (company.value.isNullOrEmpty()) {
            errorMsgCompany.value = getString(R.string.this_field_can_t_be_empty)
            atLeastOneNotValidValue = true
        }
        if (size.value.isNullOrEmpty()) {
            errorMsgSize.value = getString(R.string.this_field_can_t_be_empty)
            atLeastOneNotValidValue = true
        }
        if (description.value.isNullOrEmpty()) {
            errorMsgDescription.value = getString(R.string.this_field_can_t_be_empty)
            atLeastOneNotValidValue = true
        }
        if (atLeastOneNotValidValue) {
            logError(
                "${errorMsgCompany.value}, ${errorMsgSize.value}"
            )
            return
        }

        // Insert/Update a Shoe if applicable isa.
        beforeAndAfterDismissInLoadingDialog<Int?>(
            fragment.childFragmentManager,
            before = {
                // Check no other shoe has the same name, ( but if is update surely can have same own name )
                val shoesNames = allShoes.getNotNullOrSuspendUntilNotNullValue(fragment)
                    .map { it.name }
                if (name.value != safeArgs.shoeName && name.value in shoesNames) {
                    errorMsgName.value = getString(R.string.another_shoe_exists_with_this_name)

                    return@beforeAndAfterDismissInLoadingDialog null
                }

                // Insert/Update Shoe in database isa.
                val shoe = Shoe(
                    safeArgs.shoeId,
                    name.value.orEmpty(),
                    company.value.orEmpty(),
                    size.value?.toFloatOrNull() ?: 0.0f,
                    description.value.orEmpty()
                )
                withContext(Dispatchers.IO) {
                    if (safeArgs.shoeId == 0) {
                        // Insert
                        shoesInsert(shoe).toInt()
                    }else {
                        // Update
                        shoesUpdate(shoe)
                    }
                }
            },
            after = {
                if (it is Int && it > 0) {
                    if (safeArgs.shoeId == 0) {
                        getString(R.string.created_shoe_successfully)
                    }else {
                        getString(R.string.updated_shoe_successfully)
                    }

                    // Go back to Shoe List Fragment isa.
                    view.findNavController().popBackStack()
                }
            }
        )
    }

    fun onDeleteClick(view: View) {
        val fragment = view.findBindingOrNull<FragmentShoeDetailBinding>()
            ?.lifecycleOwner as? ShoeDetailFragment ?: return

        beforeAndAfterDismissInLoadingDialog(
            fragment.childFragmentManager,
            Dispatchers.IO,
            before = {
                // Delete from database isa.
                shoesDelete(safeArgs.shoeId)
            },
            after = {
                if (it != null && it > 0) {
                    // Go back to Shoe List Fragment isa.
                    view.findNavController().popBackStack()
                }else {
                    // should never happen but just in case it does isa.
                    application.toast(getString(R.string.unexpected_error_please_try_again))
                }
            }
        )
    }

    // ---- Private fun

    private fun clearAllErrorMsg() {
        errorMsgName.value = ""
        errorMsgCompany.value = ""
        errorMsgSize.value = ""
        errorMsgDescription.value = ""
    }

}
