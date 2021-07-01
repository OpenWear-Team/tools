/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.provider.root

import wiki.wear.openweartools.materialfiles.provider.common.PosixFileStore
import wiki.wear.openweartools.materialfiles.provider.remote.RemoteInterface
import wiki.wear.openweartools.materialfiles.provider.remote.RemotePosixFileStore

class RootPosixFileStore(fileStore: PosixFileStore) : RemotePosixFileStore(
    RemoteInterface { RootFileService.getRemotePosixFileStoreInterface(fileStore) }
)
