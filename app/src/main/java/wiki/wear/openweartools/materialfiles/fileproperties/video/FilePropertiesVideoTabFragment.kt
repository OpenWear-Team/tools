/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.fileproperties.video

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import java8.nio.file.Path
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith
import wiki.wear.openweartools.R
import wiki.wear.openweartools.materialfiles.file.FileItem
import wiki.wear.openweartools.materialfiles.file.format
import wiki.wear.openweartools.materialfiles.file.formatLong
import wiki.wear.openweartools.materialfiles.file.isVideo
import wiki.wear.openweartools.materialfiles.filelist.name
import wiki.wear.openweartools.materialfiles.fileproperties.FilePropertiesTabFragment
import wiki.wear.openweartools.materialfiles.util.ParcelableArgs
import wiki.wear.openweartools.materialfiles.util.ParcelableParceler
import wiki.wear.openweartools.materialfiles.util.Stateful
import wiki.wear.openweartools.materialfiles.util.args
import wiki.wear.openweartools.materialfiles.util.awaitGetFromLocation
import wiki.wear.openweartools.materialfiles.util.createViewLocation
import wiki.wear.openweartools.materialfiles.util.isGeocoderPresent
import wiki.wear.openweartools.materialfiles.util.isMediaMetadataRetrieverCompatible
import wiki.wear.openweartools.materialfiles.util.startActivitySafe
import wiki.wear.openweartools.materialfiles.util.userFriendlyString
import wiki.wear.openweartools.materialfiles.util.viewModels

class FilePropertiesVideoTabFragment : FilePropertiesTabFragment() {
    private val args by args<Args>()

    private val viewModel by viewModels { { FilePropertiesVideoTabViewModel(args.path) } }

    private var addressJob: Job? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.videoInfoLiveData.observe(viewLifecycleOwner) { onVideoInfoChanged(it) }
    }

    override fun refresh() {
        viewModel.reload()
    }

    private fun onVideoInfoChanged(stateful: Stateful<VideoInfo>) {
        addressJob?.cancel()
        addressJob = null
        bindView(stateful) { videoInfo ->
            if (videoInfo.title != null) {
                addItemView(R.string.file_properties_media_title, videoInfo.title)
            }
            if (videoInfo.dimensions != null) {
                addItemView(
                    R.string.file_properties_media_dimensions, getString(
                        R.string.file_properties_media_dimensions_format,
                        videoInfo.dimensions.width, videoInfo.dimensions.height
                    )
                )
            }
            if (videoInfo.duration != null) {
                addItemView(R.string.file_properties_media_duration, videoInfo.duration.format())
            }
            if (videoInfo.date != null) {
                addItemView(R.string.file_properties_media_date_time, videoInfo.date.formatLong())
            }
            if (videoInfo.location != null) {
                addItemView(
                    R.string.file_properties_media_coordinates, getString(
                        R.string.file_properties_media_coordinates_format, videoInfo.location.first,
                        videoInfo.location.second
                    )
                ) {
                    startActivitySafe(
                        Intent::class.createViewLocation(
                            videoInfo.location.first, videoInfo.location.second, args.path.name
                        )
                    )
                }
                if (isGeocoderPresent) {
                    val textView = addItemView(
                        R.string.file_properties_media_address, getString(R.string.loading)
                    )
                    val geocoder = Geocoder(requireContext())
                    addressJob = viewLifecycleOwner.lifecycleScope.launch {
                        val address = try {
                            geocoder.awaitGetFromLocation(
                                videoInfo.location.first.toDouble(),
                                videoInfo.location.second.toDouble(), 1
                            ).first()
                        } catch (e: Exception) {
                            null
                        }
                        if (isActive) {
                            textView.text = address?.userFriendlyString
                                ?: getString(R.string.unknown)
                        }
                    }
                }
            }
            if (videoInfo.bitRate != null) {
                addItemView(
                    R.string.file_properties_media_bit_rate, getString(
                        R.string.file_properties_media_bit_rate_format, videoInfo.bitRate / 1000
                    )
                )
            }
        }
    }

    companion object {
        fun isAvailable(file: FileItem): Boolean =
            file.path.isMediaMetadataRetrieverCompatible && file.mimeType.isVideo
    }

    @Parcelize
    class Args(val path: @WriteWith<ParcelableParceler> Path) : ParcelableArgs
}
