/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.app

import androidx.core.content.edit
import wiki.wear.openweartools.BuildConfig

private const val KEY_VERSION_CODE = "key_version_code"

private const val VERSION_CODE_BELOW_1_1_0 = 17
private const val VERSION_CODE_1_1_0 = 18
private const val VERSION_CODE_1_2_0 = 22
private const val VERSION_CODE_1_3_0 = 24
private const val VERSION_CODE_LATEST = BuildConfig.VERSION_CODE

private var lastVersionCode: Int
    get() {
        if (defaultSharedPreferences.all.isEmpty()) {
            // This is a new install.
            lastVersionCode = VERSION_CODE_LATEST
            return VERSION_CODE_LATEST
        }
        return defaultSharedPreferences.getInt(KEY_VERSION_CODE, VERSION_CODE_BELOW_1_1_0)
    }
    set(value) {
        defaultSharedPreferences.edit { putInt(KEY_VERSION_CODE, value) }
    }

fun upgradeApp() {
    upgradeAppFrom(lastVersionCode)
    lastVersionCode = VERSION_CODE_LATEST
}

private fun upgradeAppFrom(lastVersionCode: Int) {
    if (lastVersionCode < VERSION_CODE_1_1_0) {
        upgradeAppTo1_1_0(lastVersionCode)
    }
    if (lastVersionCode < VERSION_CODE_1_2_0) {
        upgradeAppTo1_2_0(lastVersionCode)
    }
    if (lastVersionCode < VERSION_CODE_1_3_0) {
        upgradeAppTo1_3_0(lastVersionCode)
    }
    // Continue with new `if`s on lastVersionCode instead of `else if`.
}
