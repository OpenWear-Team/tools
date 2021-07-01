/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.file

import java8.nio.file.attribute.BasicFileAttributes
import org.threeten.bp.Instant

val BasicFileAttributes.fileSize: FileSize
    get() = size().asFileSize()

val BasicFileAttributes.lastModifiedInstant: Instant
    get() = lastModifiedTime().toInstant()
