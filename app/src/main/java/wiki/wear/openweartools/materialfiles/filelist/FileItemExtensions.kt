/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.filelist

import android.content.Context
import java8.nio.file.Path
import java8.nio.file.attribute.BasicFileAttributes
import java8.nio.file.attribute.FileTime
import wiki.wear.openweartools.materialfiles.file.FileItem
import wiki.wear.openweartools.materialfiles.file.MimeType
import wiki.wear.openweartools.materialfiles.file.getBrokenSymbolicLinkName
import wiki.wear.openweartools.materialfiles.file.getName
import wiki.wear.openweartools.materialfiles.file.isMedia
import wiki.wear.openweartools.materialfiles.file.supportsThumbnail
import wiki.wear.openweartools.materialfiles.provider.archive.createArchiveRootPath
import wiki.wear.openweartools.materialfiles.provider.document.documentSupportsThumbnail
import wiki.wear.openweartools.materialfiles.provider.document.isDocumentPath
import wiki.wear.openweartools.materialfiles.provider.document.resolver.DocumentResolver
import wiki.wear.openweartools.materialfiles.provider.linux.isLinuxPath
import wiki.wear.openweartools.materialfiles.settings.Settings
import wiki.wear.openweartools.materialfiles.util.asFileName
import wiki.wear.openweartools.materialfiles.util.valueCompat
import java.text.CollationKey

val FileItem.name: String
    get() = path.name

val FileItem.baseName: String
    get() = if (attributes.isDirectory) name else name.asFileName().baseName

val FileItem.extension: String
    get() = if (attributes.isDirectory) "" else name.asFileName().extensions

fun FileItem.getMimeTypeName(context: Context): String {
        if (attributesNoFollowLinks.isSymbolicLink && isSymbolicLinkBroken) {
            return MimeType.getBrokenSymbolicLinkName(context)
        }
        return mimeType.getName(extension, context)
    }

val FileItem.isArchiveFile: Boolean
    get() = path.isArchiveFile(mimeType)

val FileItem.isListable: Boolean
    get() = attributes.isDirectory || isArchiveFile

val FileItem.listablePath: Path
    get() = if (isArchiveFile) path.createArchiveRootPath() else path

val FileItem.supportsThumbnail: Boolean
    get() =
        when {
            path.isLinuxPath -> mimeType.supportsThumbnail
            path.isDocumentPath -> {
                when {
                    attributes.documentSupportsThumbnail -> true
                    mimeType.isMedia ->
                        DocumentResolver.isLocal(path as DocumentResolver.Path)
                            || Settings.READ_REMOTE_FILES_FOR_THUMBNAIL.valueCompat
                    else -> false
                }
            }
            // TODO: Allow other providers as well - but might be resource consuming.
            else -> false
        }

fun FileItem.createDummyArchiveRoot(): FileItem =
    FileItem(
        path.createArchiveRootPath(), DummyCollationKey(), DummyArchiveRootBasicFileAttributes(),
        null, null, false, MimeType.DIRECTORY
    )

// Dummy collation key only to be added to the selection set, which may be used to determine file
// type when confirming deletion.
private class DummyCollationKey : CollationKey("") {
    override fun compareTo(other: CollationKey?): Int {
        throw UnsupportedOperationException()
    }

    override fun toByteArray(): ByteArray {
        throw UnsupportedOperationException()
    }
}

// Dummy attributes only to be added to the selection set, which may be used to determine file
// type when confirming deletion.
private class DummyArchiveRootBasicFileAttributes : BasicFileAttributes {
    override fun lastModifiedTime(): FileTime {
        throw UnsupportedOperationException()
    }

    override fun lastAccessTime(): FileTime {
        throw UnsupportedOperationException()
    }

    override fun creationTime(): FileTime {
        throw UnsupportedOperationException()
    }

    override fun isRegularFile(): Boolean = false

    override fun isDirectory(): Boolean = true

    override fun isSymbolicLink(): Boolean = false

    override fun isOther(): Boolean = false

    override fun size(): Long {
        throw UnsupportedOperationException()
    }

    override fun fileKey(): Any {
        throw UnsupportedOperationException()
    }
}
