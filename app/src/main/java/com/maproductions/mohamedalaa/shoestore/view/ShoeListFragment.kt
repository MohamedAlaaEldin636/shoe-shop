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

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.maproductions.mohamedalaa.shoestore.R
import com.maproductions.mohamedalaa.shoestore.common_value_objects.DataState
import com.maproductions.mohamedalaa.shoestore.common_value_objects.Shoe
import com.maproductions.mohamedalaa.shoestore.common_value_objects.User
import com.maproductions.mohamedalaa.shoestore.databinding.FragmentShoeListBinding
import com.maproductions.mohamedalaa.shoestore.databinding.ItemShoeBinding
import com.maproductions.mohamedalaa.shoestore.shared_pref.sharedPref_HiddenSettings_GetCurrentlyLoggedInUserEMail
import com.maproductions.mohamedalaa.shoestore.shared_pref.sharedPref_HiddenSettings_SetCurrentlyLoggedInUserEMail
import com.maproductions.mohamedalaa.shoestore.shared_pref.sharedPref_HiddenSettings_SetShowLogInScreen
import com.maproductions.mohamedalaa.shoestore.view_model.MainViewModel
import com.maproductions.mohamedalaa.shoestore.view_model.ShoeListViewModel
import mohamedalaa.mautils.core_android.extensions.findBindingOrNull
import mohamedalaa.mautils.core_android.extensions.showDialogFragment
import mohamedalaa.mautils.core_kotlin.extensions.performIfNotNull
import mohamedalaa.mautils.core_kotlin.extensions.toStringOrEmpty

/**
 * ## VIP Note
 * - Since it's not required from the rubric we share same [List]<[Shoe]> for all [User] accounts,
 * but in real life application each [User] should have his related [List]<[Shoe]>, and in this case
 * we will need to add 1 more property called `userId` in [Shoe] Entity to be able to fetch [Shoe]s
 * belonging to [sharedPref_HiddenSettings_GetCurrentlyLoggedInUserEMail] from the database isa.
 */
class ShoeListFragment : Fragment() {

    private lateinit var binding: FragmentShoeListBinding

    private val viewModel by viewModels<ShoeListViewModel>()

    private val activityViewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!::binding.isInitialized) {
            binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_shoe_list, container, false
            )
        }

        // Support adding menu items to activity's toolbar isa.
        setHasOptionsMenu(true)

        // Assign binding vars isa.
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // observe data in view model isa.
        observeViewModel()

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.shoe_list_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit_account_password -> {
                activity?.showDialogFragment<ChangePasswordDialogFragment>()
            }
            R.id.action_log_out -> context.performIfNotNull {
                sharedPref_HiddenSettings_SetCurrentlyLoggedInUserEMail(null)
                sharedPref_HiddenSettings_SetShowLogInScreen(true)

                findNavController().navigate(R.id.action_global_login_dest)
            }
            R.id.action_about -> {
                findNavController().navigate(R.id.action_shoe_list_dest_to_about_dest)
            }
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    // ---- Private fun

    private fun observeViewModel() {
        activityViewModel.allShoes.observe(this, Observer { list ->
            // Update UI
            if (list.isNullOrEmpty()) {
                // Null should never happen, since Room will call this by returning value only isa.
                viewModel.dataState.value = DataState.EMPTY
            }else {
                // Show loading till adding all views dynamically isa.
                viewModel.dataState.value = DataState.LOADING

                binding.listLinearLayout.apply {
                    // Clear all children isa.
                    removeAllViews()

                    // Load all new children isa.
                    for (shoe in list) {
                        val viewBinding = DataBindingUtil.inflate<ItemShoeBinding>(
                            layoutInflater, R.layout.item_shoe, this, false
                        )
                        viewBinding.shoe = shoe
                        viewBinding.root.setOnClickListener {
                            val itemShoe = it.findBindingOrNull<ItemShoeBinding>()?.shoe ?: return@setOnClickListener

                            binding.root.findNavController().navigate(
                                ShoeListFragmentDirections.actionShoeListDestToShoeDetailDest(
                                    itemShoe.id,
                                    itemShoe.name,
                                    itemShoe.company,
                                    itemShoe.sizeInCentimeters.toStringOrEmpty(),
                                    itemShoe.description
                                )
                            )
                        }

                        addView(viewBinding.root)
                    }
                }

                viewModel.dataState.value = DataState.NOT_EMPTY
            }
        })
    }

}
