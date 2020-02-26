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

package com.maproductions.mohamedalaa.shoestore.shared_pref

import mohamedalaa.mautils.shared_pref_annotation.MAKClass
import mohamedalaa.mautils.shared_pref_annotation.MAParameterizedKClass
import mohamedalaa.mautils.shared_pref_annotation.MASharedPrefPair

@MASharedPrefPair(
    name = "showLogInScreen",
    type = MAParameterizedKClass(
        nonNullKClasses = [Boolean::class]
    ),
    defaultValue = "true"
)
@MASharedPrefPair(
    name = "showWelcomeScreen",
    type = MAParameterizedKClass(
        nonNullKClasses = [Boolean::class]
    ),
    defaultValue = "true"
)
@MASharedPrefPair(
    name = "showInstructionsScreen",
    type = MAParameterizedKClass(
        nonNullKClasses = [Boolean::class]
    ),
    defaultValue = "true"
)
@MASharedPrefPair(
    name = "currentlyLoggedInUserEMail",
    type = MAParameterizedKClass(
        maKClass = [
            MAKClass(String::class, true)
        ]
    ),
    defaultValue = "null"
)
@Suppress("ClassName")
/**
 * - Naming convention isn't satisfied here by design not as a mistake isa.
 */
class _HiddenSettings
