/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.provider.content

import android.os.Parcel
import android.os.Parcelable
import java8.nio.file.attribute.BasicFileAttributeView
import java8.nio.file.attribute.FileTime
import wiki.wear.openweartools.materialfiles.provider.content.resolver.Resolver
import wiki.wear.openweartools.materialfiles.provider.content.resolver.ResolverException
import wiki.wear.openweartools.materialfiles.util.readParcelable
import java.io.IOException

internal class ContentFileAttributeView(
    private val path: ContentPath
) : BasicFileAttributeView, Parcelable {
    override fun name(): String = NAME

    @Throws(IOException::class)
    override fun readAttributes(): ContentFileAttributes {
        val uri = path.uri!!
        val mimeType = try {
            Resolver.getMimeType(uri)
        } catch (e: ResolverException) {
            throw e.toFileSystemException(path.toString())
        }
        val size = try {
            Resolver.getSize(uri)
        } catch (e: ResolverException) {
            throw e.toFileSystemException(path.toString())
        }
        return ContentFileAttributes.from(mimeType, size, uri)
    }

    override fun setTimes(
        lastModifiedTime: FileTime?,
        lastAccessTime: FileTime?,
        createTime: FileTime?
    ) {
        throw UnsupportedOperationException()
    }

    private constructor(source: Parcel) : this(source.readParcelable<ContentPath>()!!)

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeParcelable(path as Parcelable, flags)
    }

    companion object {
        private val NAME = ContentFileSystemProvider.scheme

        val SUPPORTED_NAMES = setOf("basic", NAME)

        @JvmField
        val CREATOR = object : Parcelable.Creator<ContentFileAttributeView> {
            override fun createFromParcel(source: Parcel): ContentFileAttributeView =
                ContentFileAttributeView(source)

            override fun newArray(size: Int): Array<ContentFileAttributeView?> = arrayOfNulls(size)
        }
    }
}
