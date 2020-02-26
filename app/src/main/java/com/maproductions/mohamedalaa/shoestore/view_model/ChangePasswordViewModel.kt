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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.maproductions.mohamedalaa.shoestore.R
import com.maproductions.mohamedalaa.shoestore.database.usersQueryPasswordWhereEMailAddress
import com.maproductions.mohamedalaa.shoestore.database.usersUpdatePasswordWhereEMailAddress
import com.maproductions.mohamedalaa.shoestore.databinding.DialogFragmentChangePasswordBinding
import com.maproductions.mohamedalaa.shoestore.shared_pref.sharedPref_HiddenSettings_GetCurrentlyLoggedInUserEMail
import com.maproductions.mohamedalaa.shoestore.view.ChangePasswordDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mohamedalaa.mautils.core_android.extensions.*

class ChangePasswordViewModel(
    application: Application,
    state: SavedStateHandle
) : AndroidViewModel(application) {

    companion object {
        private const val STATE_CURRENT_PASSWORD = "STATE_CURRENT_PASSWORD"
        const val STATE_IS_VALID_CURRENT_PASSWORD = "STATE_IS_VALID_CURRENT_PASSWORD"

        private const val STATE_NEW_PASSWORD = "STATE_NEW_PASSWORD"
        const val STATE_IS_VALID_NEW_PASSWORD = "STATE_IS_VALID_NEW_PASSWORD"

        private const val STATE_CONFIRM_PASSWORD = "STATE_CONFIRM_PASSWORD"
        const val STATE_IS_VALID_CONFIRM_PASSWORD = "STATE_IS_VALID_CONFIRM_PASSWORD"
    }

    val currentPassword = state.getLiveData(STATE_CURRENT_PASSWORD, "")
    val isValidCurrentPassword = state.getLiveData(STATE_IS_VALID_CURRENT_PASSWORD, true)

    val newPassword = state.getLiveData(STATE_NEW_PASSWORD, "")
    val isValidNewPassword = state.getLiveData(STATE_IS_VALID_NEW_PASSWORD, true)

    val confirmPassword = state.getLiveData(STATE_CONFIRM_PASSWORD, "")
    val isValidConfirmPassword = state.getLiveData(STATE_IS_VALID_CONFIRM_PASSWORD, true)

    val toolbarSubtitle: LiveData<String> = MutableLiveData(application.sharedPref_HiddenSettings_GetCurrentlyLoggedInUserEMail())

    fun setValid(key: String) {
        when (key) {
            STATE_IS_VALID_CURRENT_PASSWORD -> isValidCurrentPassword.value = true
            STATE_IS_VALID_NEW_PASSWORD -> isValidNewPassword.value = true
            STATE_IS_VALID_CONFIRM_PASSWORD -> isValidConfirmPassword.value = true
        }
    }

    fun onOkClick(view: View) {
        val dialogFragment = view.findBindingOrNull<DialogFragmentChangePasswordBinding>()
            ?.lifecycleOwner as? ChangePasswordDialogFragment ?: return

        // Check if any field is empty isa.
        var atLeastOneIsEmpty = false
        if (currentPassword.value.orEmpty().length !in 8..20) {
            isValidCurrentPassword.value = false
            atLeastOneIsEmpty = true
        }
        if (newPassword.value.orEmpty().length !in 8..20) {
            isValidNewPassword.value = false
            atLeastOneIsEmpty = true
        }
        if (confirmPassword.value.orEmpty().length !in 8..20) {
            isValidConfirmPassword.value = false
            atLeastOneIsEmpty = true
        }
        if (atLeastOneIsEmpty) {
            return
        }

        beforeAndAfterDismissInLoadingDialog(
            dialogFragment.childFragmentManager,
            before = {
                val actualCurrentPassword = withContext(Dispatchers.IO) {
                    usersQueryPasswordWhereEMailAddress(toolbarSubtitle.value.orEmpty())
                }

                // Check same current password
                if (actualCurrentPassword != currentPassword.value) {
                    application.longToast(getString(R.string.incorrect_current_password))

                    return@beforeAndAfterDismissInLoadingDialog false
                }

                // Check new password is the same as the confirm password
                if (newPassword.value != confirmPassword.value) {
                    application.longToast(getString(R.string.new_amp_confirm_passwords_must_be_the_same))

                    return@beforeAndAfterDismissInLoadingDialog false
                }

                // Check actual new change
                if (newPassword.value == actualCurrentPassword) {
                    application.longToast(getString(R.string.no_change_between_current_amp_new_passwords))

                    return@beforeAndAfterDismissInLoadingDialog false
                }

                // Update new password then exit
                val updatedRows = withContext(Dispatchers.IO) {
                    usersUpdatePasswordWhereEMailAddress(
                        newPassword.value.orEmpty(), toolbarSubtitle.value.orEmpty()
                    )
                }
                if (updatedRows > 0) {
                    application.toast(getString(R.string.password_updated_successfully))

                    true
                }else {
                    application.toast(getString(R.string.unexpected_error_please_try_again))

                    false
                }
            },
            after = {
                if (it == true) {
                    dialogFragment.dismiss()
                }
            }
        )
    }

    fun onCancelClick(view: View) {
        val dialogFragment = view.findBindingOrNull<DialogFragmentChangePasswordBinding>()
            ?.lifecycleOwner as? ChangePasswordDialogFragment ?: return

        dialogFragment.dismiss()
    }

}
