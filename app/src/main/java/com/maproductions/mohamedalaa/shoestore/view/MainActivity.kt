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

package com.maproductions.mohamedalaa.shoestore.view

import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.maproductions.mohamedalaa.shoestore.R
import com.maproductions.mohamedalaa.shoestore.databinding.ActivityMainBinding
import com.maproductions.mohamedalaa.shoestore.shared_pref.sharedPref_HiddenSettings_GetCurrentlyLoggedInUserEMail
import com.maproductions.mohamedalaa.shoestore.shared_pref.sharedPref_HiddenSettings_GetShowInstructionsScreen
import com.maproductions.mohamedalaa.shoestore.shared_pref.sharedPref_HiddenSettings_GetShowLogInScreen
import com.maproductions.mohamedalaa.shoestore.shared_pref.sharedPref_HiddenSettings_GetShowWelcomeScreen
import com.maproductions.mohamedalaa.shoestore.view_model.MainViewModel
import mohamedalaa.mautils.core_android.extensions.tintColorFilter
import mohamedalaa.mautils.core_android.extensions.tintCompat

/**
 * ## VIP Note
 * 1. the starter code link is broken, And I told Udacity mentor support but they said that they
 * are looking in to it for now and a fix would take some time, so I built the ap from scratch,
 * So I just followed the rubric and added some additional features for a Stand out application isa.
 *
 * ### Additional Notes ( for reviewer )
 * - It would be even better for the project to be a multi-module project since by doing so this
 * will make debugging, separation of concerns way better isa, but I didn't do that due to that
 * the project is small.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<MainViewModel>()

    private val appBarConfiguration = AppBarConfiguration(
        setOf(
            R.id.login_dest,
            R.id.welcome_dest,
            R.id.instructions_dest,
            R.id.shoe_list_dest
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!::binding.isInitialized) {
            binding = DataBindingUtil.setContentView(this,
                R.layout.activity_main
            )
        }

        // Using below enables fragments to call onCreateOptionsMenu, Also note title & up actions
        // are auto handled with navigation component so disable them below isa.
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // Assign binding vars isa.
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // -- Setup xml -- //

        // Setup toolbar overflow color isa.
        binding.toolbar.apply {
            overflowIcon = overflowIcon?.mutate()?.apply {
                if (DrawableCompat.getColorFilter(this) == null) {
                    tintCompat(Color.WHITE)
                }else {
                    tintColorFilter(Color.WHITE, PorterDuff.Mode.DST_IN)
                }
            }
        }

        // Setup navigation component isa.
        setupMainNavGraph(savedInstanceState)
    }

    // ---- Private fun

    /**
     * - Hooks up [ActivityMainBinding.toolbar] with [NavController].
     * - Checks initial destination if should be changed isa.
     */
    private fun setupMainNavGraph(savedInstanceState: Bundle?) {
        val navController = findNavController(R.id.mainNavHostFragment)

        // Setup toolbar
        // ( onBoarding screens are considered top-level && can't go back since rubric mentioned
        // only detail screen to go back )
        binding.toolbar.setupWithNavController(
            navController,
            appBarConfiguration
        )
        // Subtitle is only needed in Shoe Detail Fragment isa.
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id != R.id.shoe_detail_dest) {
                viewModel.toolbarSubtitle.value = ""
            }
        }

        // Checks initial destination (only if initially opened the app) isa.
        if (savedInstanceState == null && ! sharedPref_HiddenSettings_GetShowLogInScreen()) {
            when {
                sharedPref_HiddenSettings_GetShowWelcomeScreen() -> {
                    navController.navigate(
                        R.id.action_login_dest_to_welcome_dest,
                        WelcomeFragmentArgs(
                            sharedPref_HiddenSettings_GetCurrentlyLoggedInUserEMail().orEmpty()
                        ).toBundle()
                    )
                }
                sharedPref_HiddenSettings_GetShowInstructionsScreen() -> {
                    // Might happen in case user entered instructions screen but suddenly closed the app isa.
                    navController.navigate(R.id.action_global_instructions_dest)
                }
                else -> {
                    navController.navigate(R.id.action_global_shoe_list_dest)
                }
            }
        }
    }

}
