/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.provider.document

import android.os.Parcel
import android.os.Parcelable
import android.provider.DocumentsContract
import java8.nio.file.attribute.BasicFileAttributeView
import java8.nio.file.attribute.FileTime
import wiki.wear.openweartools.materialfiles.compat.use
import wiki.wear.openweartools.materialfiles.provider.content.resolver.ResolverException
import wiki.wear.openweartools.materialfiles.provider.content.resolver.getInt
import wiki.wear.openweartools.materialfiles.provider.content.resolver.getLong
import wiki.wear.openweartools.materialfiles.provider.content.resolver.getString
import wiki.wear.openweartools.materialfiles.provider.content.resolver.moveToFirstOrThrow
import wiki.wear.openweartools.materialfiles.provider.document.resolver.DocumentResolver
import wiki.wear.openweartools.materialfiles.util.readParcelable
import java.io.IOException

internal class DocumentFileAttributeView(
    private val path: DocumentPath
) : BasicFileAttributeView, Parcelable {
    override fun name(): String = NAME

    @Throws(IOException::class)
    override fun readAttributes(): DocumentFileAttributes {
        val uri = try {
            DocumentResolver.getDocumentUri(path)
        } catch (e: ResolverException) {
            throw e.toFileSystemException(path.toString())
        }
        var lastModifiedTimeMillis: Long
        var mimeType: String?
        var size: Long
        var flags: Int
        try {
            DocumentResolver.query(
                uri, arrayOf(
                    DocumentsContract.Document.COLUMN_LAST_MODIFIED,
                    DocumentsContract.Document.COLUMN_MIME_TYPE,
                    DocumentsContract.Document.COLUMN_SIZE,
                    DocumentsContract.Document.COLUMN_FLAGS
                ), null
            ).use { cursor ->
                cursor.moveToFirstOrThrow()
                lastModifiedTimeMillis = cursor.getLong(
                    DocumentsContract.Document.COLUMN_LAST_MODIFIED
                )
                mimeType = cursor.getString(DocumentsContract.Document.COLUMN_MIME_TYPE)
                size = cursor.getLong(DocumentsContract.Document.COLUMN_SIZE)
                flags = cursor.getInt(DocumentsContract.Document.COLUMN_FLAGS)
            }
        } catch (e: ResolverException) {
            throw e.toFileSystemException(path.toString())
        }
        return DocumentFileAttributes.from(lastModifiedTimeMillis, mimeType, size, flags, uri)
    }

    override fun setTimes(
        lastModifiedTime: FileTime?,
        lastAccessTime: FileTime?,
        createTime: FileTime?
    ) {
        throw UnsupportedOperationException()
    }

    private constructor(source: Parcel) : this(source.readParcelable<DocumentPath>()!!)

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeParcelable(path, flags)
    }

    companion object {
        private val NAME = DocumentFileSystemProvider.scheme

        val SUPPORTED_NAMES = setOf("basic", NAME)

        @JvmField
        val CREATOR = object : Parcelable.Creator<DocumentFileAttributeView> {
            override fun createFromParcel(source: Parcel): DocumentFileAttributeView =
                DocumentFileAttributeView(source)

            override fun newArray(size: Int): Array<DocumentFileAttributeView?> = arrayOfNulls(size)
        }
    }
}
