/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.coil

import android.os.Build
import coil.Coil
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import wiki.wear.openweartools.materialfiles.app.application

fun initializeCoil() {
    Coil.setImageLoader(
        ImageLoader.Builder(application)
            .componentRegistry {
                add(ApplicationInfoFetcher(application))
                add(PathAttributesFetcher(application))
                add(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        ImageDecoderDecoder(application)
                    } else {
                        GifDecoder()
                    }
                )
                add(SvgDecoder(application, false))
            }
            .build()
    )
}
