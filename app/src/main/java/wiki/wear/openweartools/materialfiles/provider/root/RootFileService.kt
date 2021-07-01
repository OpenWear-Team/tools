/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.provider.root

import android.os.Process
import wiki.wear.openweartools.materialfiles.provider.remote.RemoteFileService
import wiki.wear.openweartools.materialfiles.provider.remote.RemoteInterface

val isRunningAsRoot = Process.myUid() == 0

object RootFileService : RemoteFileService(
    RemoteInterface {
        if (SuiFileServiceLauncher.isSuiAvailable) {
            SuiFileServiceLauncher.launchService()
        } else {
            LibRootJavaFileServiceLauncher.launchService()
        }
    }
)
