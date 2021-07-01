/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.graphics.drawable.AnimatedStateListDrawableCompat
import wiki.wear.openweartools.R
import wiki.wear.openweartools.materialfiles.util.asColor
import wiki.wear.openweartools.materialfiles.util.getColorByAttr
import wiki.wear.openweartools.materialfiles.util.shortAnimTime
import wiki.wear.openweartools.materialfiles.util.withModulatedAlpha

object CheckableItemBackground {
    // We need an <animated-selector> (AnimatedStateListDrawable) with an item drawable referencing
    // a ColorStateList that adds an alpha to our primary color, which is a theme attribute. We
    // currently don't have any compat handling for ColorStateList inside drawable on pre-23,
    // although AppCompatResources do have compat handling for inflating ColorStateList directly.
    // Note that the <selector>s used in Material Components are color resources, so they are
    // inflated as ColorStateList instead of StateListDrawable and don't have this problem.
    @SuppressLint("RestrictedApi")
    fun create(context: Context): Drawable =
        AnimatedStateListDrawableCompat().apply {
            val shortAnimTime = context.shortAnimTime
            setEnterFadeDuration(shortAnimTime)
            setExitFadeDuration(shortAnimTime)
            val primaryColor = context.getColorByAttr(R.attr.colorPrimary)
            val checkedColor = primaryColor.asColor().withModulatedAlpha(0.12f).value
            addState(intArrayOf(android.R.attr.state_checked), ColorDrawable(checkedColor))
            addState(intArrayOf(), ColorDrawable(Color.TRANSPARENT))
        }
}
