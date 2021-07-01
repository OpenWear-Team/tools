/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.fileproperties.permissions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import wiki.wear.openweartools.materialfiles.provider.common.PosixFileModeBit
import wiki.wear.openweartools.materialfiles.util.toEnumSet
import wiki.wear.openweartools.materialfiles.util.valueCompat

class SetModeViewModel(mode: Set<PosixFileModeBit>) : ViewModel() {
    private val _modeLiveData: MutableLiveData<Set<PosixFileModeBit>> = MutableLiveData(mode)
    val modeLiveData: LiveData<Set<PosixFileModeBit>>
        get() = _modeLiveData
    val mode: Set<PosixFileModeBit>
        get() = _modeLiveData.valueCompat

    fun toggleModeBit(modeBit: PosixFileModeBit) {
        val mode = _modeLiveData.valueCompat.toEnumSet()
        if (modeBit in mode) {
            mode -= modeBit
        } else {
            mode += modeBit
        }
        _modeLiveData.value = mode
    }
}
