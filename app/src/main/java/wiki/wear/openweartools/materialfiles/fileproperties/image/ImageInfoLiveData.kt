/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.fileproperties.image

import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Size
import androidx.exifinterface.media.ExifInterface
import com.caverock.androidsvg.SVG
import java8.nio.file.Path
import wiki.wear.openweartools.materialfiles.file.MimeType
import wiki.wear.openweartools.materialfiles.fileproperties.PathObserverLiveData
import wiki.wear.openweartools.materialfiles.provider.common.getLastModifiedTime
import wiki.wear.openweartools.materialfiles.provider.common.newInputStream
import wiki.wear.openweartools.materialfiles.util.Failure
import wiki.wear.openweartools.materialfiles.util.Loading
import wiki.wear.openweartools.materialfiles.util.Stateful
import wiki.wear.openweartools.materialfiles.util.Success
import wiki.wear.openweartools.materialfiles.util.valueCompat
import okio.buffer
import okio.source
import kotlin.math.roundToInt

class ImageInfoLiveData(
    path: Path,
    private val mimeType: MimeType
) : PathObserverLiveData<Stateful<ImageInfo>>(path) {
    init {
        loadValue()
        observe()
    }

    override fun loadValue() {
        value = Loading(value?.value)
        AsyncTask.THREAD_POOL_EXECUTOR.execute {
            val value = try {
                val imageInfo = when (mimeType) {
                    MimeType.IMAGE_SVG_XML -> {
                        val svg = path.newInputStream()
                            // It seems we need Okio for SVG parser to work for materialfiles with entities.
                            // Something weird is going on with buffering and mark/reset.
                            //.buffer()
                            //.use { SVG.getFromInputStream(it) }
                            .source()
                            .buffer()
                            .use { SVG.getFromInputStream(it.inputStream()) }
                        val width = svg.documentWidth
                        val height = svg.documentHeight
                        val dimensions = if (width != -1f && height != -1f) {
                            Size(width.roundToInt(), height.roundToInt())
                        } else {
                            val viewBox = svg.documentViewBox
                            if (viewBox != null) {
                                Size(viewBox.width().roundToInt(), viewBox.height().roundToInt())
                            } else {
                                null
                            }
                        }
                        ImageInfo(dimensions, null)
                    }
                    else -> {
                        val bitmapOptions = BitmapFactory.Options()
                            .apply { inJustDecodeBounds = true }
                        path.newInputStream()
                            .buffered()
                            .use { BitmapFactory.decodeStream(it, null, bitmapOptions) }
                        val width = bitmapOptions.outWidth
                        val height = bitmapOptions.outHeight
                        val dimensions = if (width != -1 && height != -1) {
                            Size(width, height)
                        } else {
                            null
                        }
                        val exifInfo = try {
                            val lastModifiedTime = path.getLastModifiedTime().toInstant()
                            path.newInputStream().buffered().use {
                                val exifInterface = ExifInterface(it)
                                val dateTimeOriginal =
                                    exifInterface.inferDateTimeOriginal(lastModifiedTime)
                                val gpsCoordinates = exifInterface.latLong?.let { it[0] to it[1] }
                                val gpsAltitude = exifInterface.gpsAltitude
                                val make =
                                    exifInterface.getAttributeNotBlank(ExifInterface.TAG_MAKE)
                                val model =
                                    exifInterface.getAttributeNotBlank(ExifInterface.TAG_MODEL)
                                val fNumber = exifInterface.getAttributeDoubleOrNull(
                                    ExifInterface.TAG_F_NUMBER
                                )
                                val shutterSpeedValue = exifInterface.getAttributeDoubleOrNull(
                                    ExifInterface.TAG_SHUTTER_SPEED_VALUE
                                )
                                val focalLength = exifInterface.getAttributeDoubleOrNull(
                                    ExifInterface.TAG_FOCAL_LENGTH
                                )
                                val photographicSensitivity = exifInterface.getAttributeIntOrNull(
                                    ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY
                                )
                                val software =
                                    exifInterface.getAttributeNotBlank(ExifInterface.TAG_SOFTWARE)
                                val description = exifInterface.getAttributeNotBlank(
                                    ExifInterface.TAG_IMAGE_DESCRIPTION
                                ) ?: exifInterface.getAttributeNotBlank(
                                    ExifInterface.TAG_USER_COMMENT
                                )
                                val artist =
                                    exifInterface.getAttributeNotBlank(ExifInterface.TAG_ARTIST)
                                val copyright =
                                    exifInterface.getAttributeNotBlank(ExifInterface.TAG_COPYRIGHT)
                                ExifInfo(
                                    dateTimeOriginal, gpsCoordinates, gpsAltitude, make,
                                    model, fNumber, shutterSpeedValue, focalLength,
                                    photographicSensitivity, software, description, artist,
                                    copyright
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }
                        ImageInfo(dimensions, exifInfo)
                    }
                }
                Success(imageInfo)
            } catch (e: Exception) {
                Failure(valueCompat.value, e)
            }
            postValue(value)
        }
    }
}
