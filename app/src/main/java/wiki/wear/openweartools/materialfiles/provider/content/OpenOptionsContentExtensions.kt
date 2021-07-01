/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.provider.content

import java8.nio.file.StandardOpenOption
import wiki.wear.openweartools.materialfiles.provider.common.OpenOptions

internal fun OpenOptions.toContentMode(): String =
    StringBuilder()
        .apply {
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
            if (createNew) {
                throw UnsupportedOperationException(StandardOpenOption.CREATE_NEW.toString())
            } else if (create) {
                // Ignored.
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
        }
        .toString()
