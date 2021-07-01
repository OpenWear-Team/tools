/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.provider.remote

import wiki.wear.openweartools.materialfiles.provider.common.ParcelableFileTime
import wiki.wear.openweartools.materialfiles.provider.common.ParcelablePosixFileMode
import wiki.wear.openweartools.materialfiles.provider.common.PosixFileAttributeView
import wiki.wear.openweartools.materialfiles.provider.common.PosixGroup
import wiki.wear.openweartools.materialfiles.provider.common.PosixUser

class RemotePosixFileAttributeViewInterface(
    private val attributeView: PosixFileAttributeView
) : IRemotePosixFileAttributeView.Stub() {
    override fun readAttributes(exception: ParcelableException): ParcelableObject? =
        tryRun(exception) { attributeView.readAttributes().toParcelable() }

    override fun setTimes(
        lastModifiedTime: ParcelableFileTime?,
        lastAccessTime: ParcelableFileTime?,
        createTime: ParcelableFileTime?,
        exception: ParcelableException
    ) {
        tryRun(exception) {
            attributeView.setTimes(
                lastModifiedTime?.value, lastAccessTime?.value, createTime?.value
            )
        }
    }

    override fun setOwner(owner: PosixUser, exception: ParcelableException) {
        tryRun(exception) { attributeView.setOwner(owner) }
    }

    override fun setGroup(group: PosixGroup, exception: ParcelableException) {
        tryRun(exception) { attributeView.setGroup(group) }
    }

    override fun setMode(mode: ParcelablePosixFileMode, exception: ParcelableException) {
        tryRun(exception) { attributeView.setMode(mode.value) }
    }

    override fun setSeLinuxContext(context: ParcelableObject, exception: ParcelableException) {
        tryRun(exception) { attributeView.setSeLinuxContext(context.value()) }
    }

    override fun restoreSeLinuxContext(exception: ParcelableException) {
        tryRun(exception) { attributeView.restoreSeLinuxContext() }
    }
}
