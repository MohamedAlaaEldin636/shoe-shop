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
import android.text.Editable
import android.util.Patterns
import android.view.View
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.maproductions.mohamedalaa.shoestore.R
import com.maproductions.mohamedalaa.shoestore.common_value_objects.User
import com.maproductions.mohamedalaa.shoestore.database.usersInsert
import com.maproductions.mohamedalaa.shoestore.database.usersQueryAll
import com.maproductions.mohamedalaa.shoestore.databinding.FragmentLoginBinding
import com.maproductions.mohamedalaa.shoestore.shared_pref.*
import com.maproductions.mohamedalaa.shoestore.view.LoginFragment
import com.maproductions.mohamedalaa.shoestore.view.LoginFragmentDirections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mohamedalaa.mautils.core_android.extensions.*

class LoginViewModel(application: Application, state: SavedStateHandle) : AndroidViewModel(application) {

    private companion object {
        const val STATE_KEY_E_MAIL_ADDRESS = "STATE_KEY_E_MAIL_ADDRESS"
        const val STATE_KEY_IS_VALID_E_MAIL_ADDRESS = "STATE_KEY_IS_VALID_E_MAIL_ADDRESS"
        const val STATE_KEY_ERROR_MSG_E_MAIL_ADDRESS = "STATE_KEY_ERROR_MSG_E_MAIL_ADDRESS"

        const val STATE_KEY_PASSWORD = "STATE_KEY_PASSWORD"
        const val STATE_KEY_IS_VALID_PASSWORD = "STATE_KEY_IS_VALID_PASSWORD"
        const val STATE_KEY_ERROR_MSG_PASSWORD = "STATE_KEY_ERROR_MSG_PASSWORD"

        const val STATE_KEY_KEEP_ME_LOGGED_IN = "STATE_KEY_KEEP_ME_LOGGED_IN"
    }

    /**
     * - Note we get a [LiveData] from [SavedStateHandle] then when it is changed whether from
     * the effect of two way data binding or dynamically via [MutableLiveData.setValue] then
     * [SavedStateHandle] is auto updated without the need of [SavedStateHandle.set] isa.
     */
    val eMailAddress = state.getLiveData(STATE_KEY_E_MAIL_ADDRESS, "")
    val isValidEMailAddress = state.getLiveData(STATE_KEY_IS_VALID_E_MAIL_ADDRESS, true)
    val errorMsgEMailAddress = state.getLiveData(STATE_KEY_ERROR_MSG_E_MAIL_ADDRESS, "")

    val password = state.getLiveData(STATE_KEY_PASSWORD, "")
    val isValidPassword = state.getLiveData(STATE_KEY_IS_VALID_PASSWORD, true)
    val errorMsgPassword = state.getLiveData(STATE_KEY_ERROR_MSG_PASSWORD, "")

    val keepMeLoggedIn = state.getLiveData(STATE_KEY_KEEP_ME_LOGGED_IN, true)

    /**
     * - Holds a [LiveData] value of all [User]s in the table to validate either login/newAccount clicks isa.
     * - Note since there are no observers with [LifecycleOwner] then value of first update will
     * only be available, If need other updates then use [LiveData.observe], but here we don't care
     * except about 1 result ssince after it with either login/newAccount this fragment will be popped
     * so even on a logout case a new instance will create a new fetch from database so we still updated.
     */
    private val allUsers = usersQueryAll()

    fun onLogInClick(view: View) {
        val fragment = view.findBindingOrNull<FragmentLoginBinding>()
            ?.lifecycleOwner as? LoginFragment ?: return

        // Clear all errors isa.
        clearAllErrorMsg()

        viewModelScope.launch {
            beforeAndAfterDismissInLoadingDialog(
                fragment.childFragmentManager,
                before = {
                    val isValid = isValidInput(fragment, true)
                    if (isValid) {
                        // Show or Don't show login screen again isa.
                        application.sharedPref_HiddenSettings_SetShowLogInScreen(keepMeLoggedIn.value != true)

                        // Set currently logged in user eMail isa.
                        if (keepMeLoggedIn.value == true) {
                            application.sharedPref_HiddenSettings_SetCurrentlyLoggedInUserEMail(eMailAddress.value)
                        }
                    }

                    isValid
                },
                after = {
                    // true if logIn/newAccount is correct, so proceed to next screen isa.
                    if (it != true) return@beforeAndAfterDismissInLoadingDialog

                    // Go to next screen isa.
                    goToNextDestination(view.findNavController())
                }
            )
        }
    }

    fun onCreateNewAccountClick(view: View) {
        val fragment = view.findBindingOrNull<FragmentLoginBinding>()
            ?.lifecycleOwner as? LoginFragment ?: return

        // Clear all errors isa.
        clearAllErrorMsg()

        viewModelScope.launch {
            beforeAndAfterDismissInLoadingDialog(
                fragment.childFragmentManager,
                before = {
                    val isValid = isValidInput(fragment, false)
                    if (isValid) {
                        // Creation of a new account -> so insert to database isa.
                        val insertedId = withContext(Dispatchers.IO) {
                            usersInsert(
                                User(0, eMailAddress.value.orEmpty(), password.value.orEmpty())
                            )
                        }
                        if (insertedId <= 0L) {
                            // should never happen but just in case it does isa.
                            application.toast(getString(R.string.unexpected_error_please_try_again))

                            return@beforeAndAfterDismissInLoadingDialog false
                        }

                        // Show or Don't show login screen again isa.
                        application.sharedPref_HiddenSettings_SetShowLogInScreen(keepMeLoggedIn.value != true)

                        // Set currently logged in user eMail isa.
                        if (keepMeLoggedIn.value == true) {
                            application.sharedPref_HiddenSettings_SetCurrentlyLoggedInUserEMail(eMailAddress.value)
                        }
                    }

                    isValid
                },
                after = {
                    // true if logIn/newAccount is correct, so proceed to next screen isa.
                    if (it != true) return@beforeAndAfterDismissInLoadingDialog

                    // Go to next screen isa.
                    goToNextDestination(view.findNavController())
                }
            )
        }
    }

    fun afterEMailAddressTextChange(@Suppress("UNUSED_PARAMETER") editable: Editable?) {
        isValidEMailAddress.value = true
    }

    fun afterPasswordTextChange(@Suppress("UNUSED_PARAMETER") editable: Editable?) {
        isValidPassword.value = true
    }

    // ---- Private fun

    private fun goToNextDestination(navController: NavController) {
        when {
            application.sharedPref_HiddenSettings_GetShowWelcomeScreen() -> {
                navController.navigate(
                    LoginFragmentDirections.actionLoginDestToWelcomeDest(eMailAddress.value.orEmpty())
                )
            }
            application.sharedPref_HiddenSettings_GetShowInstructionsScreen() -> {
                navController.navigate(R.id.action_global_instructions_dest)
            }
            else -> {
                navController.navigate(R.id.action_global_shoe_list_dest)
            }
        }
    }

    private fun clearAllErrorMsg() {
        afterEMailAddressTextChange(null)
        afterPasswordTextChange(null)
    }

    /**
     * - Checks for validation of input data (e-mail & password), then updates [errorMsgEMailAddress],
     * [errorMsgPassword], [isValidEMailAddress] & [isValidPassword].
     *
     * @return `true` if input data is correct, false otherwise isa.
     */
    private suspend fun isValidInput(lifecycleOwner: LifecycleOwner, isLogInOperationNotNewAccount: Boolean): Boolean {
        val users = allUsers.getNotNullOrSuspendUntilNotNullValue(lifecycleOwner)

        // Check if really represent an email address
        if (! Patterns.EMAIL_ADDRESS.matcher(eMailAddress.value.orEmpty()).matches()) {
            errorMsgEMailAddress.value = getString(R.string.invalid_e_mail_address)
            isValidEMailAddress.value = false

            return false
        }

        // Check password length range
        if (password.value.orEmpty().length !in 8..20) {
            errorMsgPassword.value = getString(R.string.length_range_must_be_8_20)
            isValidPassword.value = false

            return false
        }

        // Logical checks -> if login then a user has to exist, if new account then has to be new email isa.
        if (isLogInOperationNotNewAccount) {
            // Log In -> a user with same e-mail & password has to exist isa.
            val sameEMailAddressIndex = users.indexOfFirst { it.eMailAddress == eMailAddress.value }

            return if (sameEMailAddressIndex >= 0) {
                // EMail exists -> Check password
                val isValid = users.getOrNull(sameEMailAddressIndex)?.password == password.value
                if (! isValid) {
                    errorMsgPassword.value = getString(R.string.incorrect_password)
                    isValidPassword.value = false
                }

                isValid
            }else {
                // EMail doesn't exist
                errorMsgEMailAddress.value = getString(R.string.no_user_found_with_this_e_mail)
                isValidEMailAddress.value = false

                false
            }
        }else {
            // New Account -> a user with same e-mail MUST NOT exist isa.
            val isValid = users.all { it.eMailAddress != eMailAddress.value }
            if (! isValid) {
                errorMsgEMailAddress.value = getString(R.string.user_already_exists_with_this_e_mail)
                isValidEMailAddress.value = false
            }

            return isValid
        }
    }

}
