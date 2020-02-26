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
import android.text.method.*
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.maproductions.mohamedalaa.shoestore.R
import com.maproductions.mohamedalaa.shoestore.databinding.FragmentAboutBinding
import com.maproductions.mohamedalaa.shoestore.view_model.AboutViewModel

class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentAboutBinding>(
            inflater, R.layout.fragment_about, container, false
        )

        // Assign binding vars isa.
        binding.viewModel = viewModels<AboutViewModel>().value
        binding.lifecycleOwner = this

        // Setup xml
        setupXml(binding)

        return binding.root
    }

    // ---- Private fun

    /**
     * - Makes [ClickableSpan]s clickable in [TextView]s isa.
     */
    private fun setupXml(binding: FragmentAboutBinding) {
        binding.appMadeByTextView.movementMethod = LinkMovementMethod.getInstance()
        binding.appLauncherInclude.textView.movementMethod = LinkMovementMethod.getInstance()
        binding.shoeInclude.textView.movementMethod = LinkMovementMethod.getInstance()
        binding.otherIconsTextView.movementMethod = LinkMovementMethod.getInstance()
    }

}
