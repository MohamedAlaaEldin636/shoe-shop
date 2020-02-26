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
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.core.text.buildSpannedString
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.maproductions.mohamedalaa.shoestore.R
import mohamedalaa.mautils.core_android.custom_classes.character_style.LinkSpan
import mohamedalaa.mautils.core_android.extensions.application
import mohamedalaa.mautils.core_android.extensions.getDrawableFromRes
import mohamedalaa.mautils.core_android.extensions.getString
import mohamedalaa.mautils.core_android.extensions.set

/**
 * - **NOTE** Names/Links doesn't get translation so all names/links can be found here as hardcoded strings.
 */
class AboutViewModel(application: Application) : AndroidViewModel(application) {

    private val flaticonLink = "www.flaticon.com"

    val appMadeByMohamedAlaaCharSequence: CharSequence = buildSpannedString {
        append(getString(R.string.app_made_by))

        val mohamedAlaa = "Mohamed Alaa"
        append(" $mohamedAlaa")

        this[mohamedAlaa] = LinkSpan("https://play.google.com/store/apps/developer?id=Mohamed+Aladdin", true)
        this[mohamedAlaa] = ForegroundColorSpan(Color.BLUE)
        this[mohamedAlaa] = UnderlineSpan()
        this[mohamedAlaa] = StyleSpan(Typeface.ITALIC)
    }

    val appLauncherIconDrawable: Drawable = application.getDrawableFromRes(R.mipmap.ic_launcher_shoe_shop_rect)
    val appLauncherTextCharSequence: CharSequence = getIconMadeByText(
        "Payungkead",
        "https://www.flaticon.com/authors/payungkead"
    )

    val shoeIconDrawable: Drawable = application.getDrawableFromRes(R.drawable.ic_running)
    val shoeTextCharSequence: CharSequence = getIconMadeByText(
        "photo3idea_studio",
        "https://www.flaticon.com/authors/photo3idea-studio"
    )

    val otherIconsTextCharSequence: CharSequence = buildSpannedString {
        append(getString(R.string.other_icons_made_by))

        val materialDesignIcons = getString(R.string.material_design_icons)
        append(" $materialDesignIcons")

        this[materialDesignIcons] = LinkSpan("https://material.io/resources/icons")
        this[materialDesignIcons] = ForegroundColorSpan(Color.BLUE)
        this[materialDesignIcons] = UnderlineSpan()
        this[materialDesignIcons] = StyleSpan(Typeface.ITALIC)
    }

    // ---- Private fun

    private fun getIconMadeByText(designer: String, link: String): CharSequence = buildSpannedString {
        append(getString(R.string.icon_made_by_var_from, designer))
        append(" $flaticonLink")

        this[designer] = LinkSpan(link)
        this[designer] = ForegroundColorSpan(Color.BLUE)
        this[designer] = UnderlineSpan()
        this[designer] = StyleSpan(Typeface.ITALIC)

        this[flaticonLink] = LinkSpan("https://$flaticonLink")
        this[flaticonLink] = ForegroundColorSpan(Color.BLUE)
        this[flaticonLink] = UnderlineSpan()
        this[flaticonLink] = StyleSpan(Typeface.ITALIC)
    }

    fun onOpenSourceLicensesClick() {
        // NOTE this isn't a violation of the single activity structure as I use it,
        // but the open source licenses library from google has no fragments only activity
        // and the licenses has to be presented when we use it isa.
        application.startActivity(
            Intent(application, OssLicensesMenuActivity::class.java)
        )
    }

}
