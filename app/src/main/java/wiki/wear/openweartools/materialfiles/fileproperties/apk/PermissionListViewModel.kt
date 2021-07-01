/*
 * Copyright (c) 2021 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.fileproperties.apk

import androidx.lifecycle.ViewModel

class PermissionListViewModel(permissionNames: Array<String>) : ViewModel() {
    val permissionListLiveData = PermissionListLiveData(permissionNames)
}
