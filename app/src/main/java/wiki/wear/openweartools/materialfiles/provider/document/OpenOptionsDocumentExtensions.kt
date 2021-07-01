/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.provider.document

import java8.nio.file.StandardOpenOption
import wiki.wear.openweartools.materialfiles.provider.common.OpenOptions

internal fun OpenOptions.toDocumentMode(): String =
    StringBuilder().apply {
        if (read && write) {
            append("rw")
        } else if (write) {
            append('w')
        } else {
            append('r')
        }
        if (append) {
            append('a')
        }
        if (truncateExisting) {
            append('t')
        }
        if (create || createNew) {
            throw AssertionError(
                "${StandardOpenOption.CREATE} and ${StandardOpenOption.CREATE_NEW
                } should have been handled before calling DocumentOpenOptions.toMode()"
            )
        }
        if (deleteOnClose) {
            throw UnsupportedOperationException(StandardOpenOption.DELETE_ON_CLOSE.toString())
        }
        if (sync) {
            throw UnsupportedOperationException(StandardOpenOption.SYNC.toString())
        }
        if (dsync) {
            throw UnsupportedOperationException(StandardOpenOption.DSYNC.toString())
        }
    }.toString()
