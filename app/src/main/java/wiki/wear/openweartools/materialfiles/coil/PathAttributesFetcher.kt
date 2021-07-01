/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.coil

import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.ParcelFileDescriptor
import androidx.core.graphics.drawable.toDrawable
import coil.bitmap.BitmapPool
import coil.decode.DataSource
import coil.decode.Options
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.fetch.SourceResult
import coil.fetch.VideoFrameFetcher
import coil.size.PixelSize
import coil.size.Size
import java8.nio.file.Path
import java8.nio.file.attribute.BasicFileAttributes
import me.zhanghai.android.appiconloader.AppIconLoader
import wiki.wear.openweartools.R
import wiki.wear.openweartools.materialfiles.compat.use
import wiki.wear.openweartools.materialfiles.file.MimeType
import wiki.wear.openweartools.materialfiles.file.asMimeType
import wiki.wear.openweartools.materialfiles.file.isApk
import wiki.wear.openweartools.materialfiles.file.isImage
import wiki.wear.openweartools.materialfiles.file.isMedia
import wiki.wear.openweartools.materialfiles.file.isPdf
import wiki.wear.openweartools.materialfiles.file.isVideo
import wiki.wear.openweartools.materialfiles.file.lastModifiedInstant
import wiki.wear.openweartools.materialfiles.provider.common.AndroidFileTypeDetector
import wiki.wear.openweartools.materialfiles.provider.common.newInputStream
import wiki.wear.openweartools.materialfiles.provider.content.resolver.ResolverException
import wiki.wear.openweartools.materialfiles.provider.document.documentSupportsThumbnail
import wiki.wear.openweartools.materialfiles.provider.document.isDocumentPath
import wiki.wear.openweartools.materialfiles.provider.document.resolver.DocumentResolver
import wiki.wear.openweartools.materialfiles.provider.linux.isLinuxPath
import wiki.wear.openweartools.materialfiles.settings.Settings
import wiki.wear.openweartools.materialfiles.util.getDimensionPixelSize
import wiki.wear.openweartools.materialfiles.util.setDataSource
import wiki.wear.openweartools.materialfiles.util.valueCompat
import okio.buffer
import okio.source
import wiki.wear.openweartools.materialfiles.util.setDataSource as appSetDataSource

class PathAttributesFetcher(
    private val context: Context
) : Fetcher<Pair<Path, BasicFileAttributes>> {
    private val videoFrameFetcher = object : VideoFrameFetcher<Path>(context) {
        override fun key(data: Path): String? {
            throw AssertionError(data)
        }

        override fun MediaMetadataRetriever.setDataSource(data: Path) {
            appSetDataSource(data)
        }
    }

    private val pdfPageFetcher = object : PdfPageFetcher<Path>(context) {
        override fun key(data: Path): String? {
            throw AssertionError(data)
        }

        override fun openParcelFileDescriptor(data: Path): ParcelFileDescriptor =
            when {
                data.isLinuxPath ->
                    ParcelFileDescriptor.open(data.toFile(), ParcelFileDescriptor.MODE_READ_ONLY)
                data.isDocumentPath ->
                    DocumentResolver.openParcelFileDescriptor(data as DocumentResolver.Path, "r")
                else -> throw IllegalArgumentException(data.toString())
            }
    }

    private val appIconLoader = AppIconLoader(
        // This is used by FileListAdapter.
        context.getDimensionPixelSize(R.dimen.large_icon_size), false, context
    )

    override fun key(data: Pair<Path, BasicFileAttributes>): String {
        val (path, attributes) = data
        return "$path:${attributes.lastModifiedInstant.toEpochMilli()}"
    }

    override suspend fun fetch(
        pool: BitmapPool,
        data: Pair<Path, BasicFileAttributes>,
        size: Size,
        options: Options
    ): FetchResult {
        val (path, attributes) = data
        val isThumbnailSize = size.isThumbnailSize
        if (path.isDocumentPath && isThumbnailSize && attributes.documentSupportsThumbnail) {
            size as PixelSize
            val thumbnail = try {
                DocumentResolver.getThumbnail(
                    path as DocumentResolver.Path, size.width, size.height
                )
            } catch (e: ResolverException) {
                e.printStackTrace()
                null
            }
            if (thumbnail != null) {
                return DrawableResult(
                    thumbnail.toDrawable(context.resources), true, DataSource.DISK
                )
            }
        }
        val mimeType = AndroidFileTypeDetector.getMimeType(data.first, data.second).asMimeType()
        if (path.isLinuxPath || (path.isDocumentPath && (!isThumbnailSize
                || DocumentResolver.isLocal(path as DocumentResolver.Path)
                || Settings.READ_REMOTE_FILES_FOR_THUMBNAIL.valueCompat))) {
            if (mimeType.isMedia) {
                val embeddedPicture = try {
                    MediaMetadataRetriever().use { retriever ->
                        retriever.setDataSource(path)
                        retriever.embeddedPicture
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
                if (embeddedPicture != null) {
                    return SourceResult(
                        embeddedPicture.inputStream().source().buffer(), null, DataSource.DISK
                    )
                }
            }
            if (mimeType.isVideo) {
                try {
                    return videoFrameFetcher.fetch(pool, path, size, options)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            if (mimeType.isPdf) {
                try {
                    return pdfPageFetcher.fetch(pool, path, size, options)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        if (path.isLinuxPath && mimeType.isApk) {
            val apkPath = path.toFile().path
            val applicationInfo = context.packageManager.getPackageArchiveInfo(apkPath, 0)
                ?.applicationInfo
            if (applicationInfo != null) {
                applicationInfo.sourceDir = apkPath
                applicationInfo.publicSourceDir = apkPath
                val icon = appIconLoader.loadIcon(applicationInfo)
                // Not sampled because we only load with one fixed size.
                return DrawableResult(icon.toDrawable(context.resources), false, DataSource.DISK)
            }
        }
        if ((mimeType.isImage || mimeType == MimeType.GENERIC) && (!path.isDocumentPath
                || !isThumbnailSize || DocumentResolver.isLocal(path as DocumentResolver.Path)
                || Settings.READ_REMOTE_FILES_FOR_THUMBNAIL.valueCompat)) {
            val inputStream = path.newInputStream()
            return SourceResult(
                inputStream.source().buffer(),
                if (mimeType != MimeType.GENERIC) mimeType.value else null,
                DataSource.DISK
            )
        }
        error("Cannot fetch $path")
    }

    private val Size.isThumbnailSize: Boolean
        // @see android.provider.MediaStore.ThumbnailConstants.MINI_SIZE
        get() = this is PixelSize && width <= 512 && height <= 384
}
