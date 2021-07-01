/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.provider.document

import android.provider.DocumentsContract
import java8.nio.file.ProviderMismatchException
import java8.nio.file.attribute.BasicFileAttributes
import wiki.wear.openweartools.materialfiles.util.hasBits

val BasicFileAttributes.documentSupportsThumbnail: Boolean
    get() {
        this as? DocumentFileAttributes ?: throw ProviderMismatchException(toString())
        return flags().hasBits(DocumentsContract.Document.FLAG_SUPPORTS_THUMBNAIL)
    }
