/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.util

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

fun DialogFragment.show(fragment: Fragment) {
    show(fragment.childFragmentManager, null)
}
