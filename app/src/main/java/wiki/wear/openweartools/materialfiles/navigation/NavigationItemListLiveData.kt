/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.navigation

import androidx.lifecycle.MediatorLiveData
import wiki.wear.openweartools.materialfiles.settings.Settings

object NavigationItemListLiveData : MediatorLiveData<List<NavigationItem?>>() {
    init {
        // Initialize value before we have any active observer.
        loadValue()
        addSource(Settings.STORAGES) { loadValue() }
        addSource(StandardDirectoriesLiveData) { loadValue() }
        addSource(Settings.BOOKMARK_DIRECTORIES) { loadValue() }
    }

    private fun loadValue() {
        value = navigationItems
    }
}
