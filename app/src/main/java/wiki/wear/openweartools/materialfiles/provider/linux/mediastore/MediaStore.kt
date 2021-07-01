/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.provider.linux.mediastore

import android.media.MediaScannerConnection
import java8.nio.channels.FileChannel
import wiki.wear.openweartools.materialfiles.app.application
import wiki.wear.openweartools.materialfiles.provider.common.DelegateFileChannel
import wiki.wear.openweartools.materialfiles.provider.root.isRunningAsRoot
import java.io.File
import java.io.IOException

/*
 * @see com.android.internal.content.FileSystemProvider
 * @see com.android.providers.media.scan.ModernMediaScanner.java
 */
object MediaStore {
    fun scan(file: File) {
        if (isRunningAsRoot) {
            return
        }
        MediaScannerConnection.scanFile(application, arrayOf(file.path), null, null)
    }

    fun createScanOnCloseFileChannel(fileChannel: FileChannel, file: File): FileChannel =
        if (isRunningAsRoot) {
            fileChannel
        } else {
            object : DelegateFileChannel(fileChannel) {
                @Throws(IOException::class)
                override fun implCloseChannel() {
                    super.implCloseChannel()

                    scan(file)
                }
            }
        }
}
