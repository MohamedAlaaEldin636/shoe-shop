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

package com.maproductions.mohamedalaa.shoestore.data_binding

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

object BATextInputLayout {

    @JvmStatic
    @BindingAdapter("android:textInputLayout_errorText")
    fun errorText(textInputLayout: TextInputLayout, errorText: String?) {
        textInputLayout.error = errorText
    }

    /**
     * - uses [TextInputLayout.setErrorEnabled] && [TextInputLayout.setHelperTextEnabled] then
     * resets the corresponding `true` value text to it's current text to display the error/helper text.
     * - sets [TextInputLayout.setErrorEnabled] to `true` only if [value] is `true` isa.
     */
    @JvmStatic
    @BindingAdapter("android:textInputLayout_errorEnabledOtherwiseHelperEnabled")
    fun errorEnabledOtherwiseHelperEnabled(textInputLayout: TextInputLayout, value: Boolean?) {
        if (value == true) {
            textInputLayout.error = textInputLayout.error
        }else {
            textInputLayout.helperText = textInputLayout.helperText
        }
    }

}
