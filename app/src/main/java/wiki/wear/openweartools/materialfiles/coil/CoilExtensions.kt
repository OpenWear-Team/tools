/*
 * Copyright (c) 2021 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.coil

import coil.annotation.ExperimentalCoilApi
import coil.request.ImageRequest
import coil.transition.CrossfadeTransition

@OptIn(ExperimentalCoilApi::class)
fun ImageRequest.Builder.fadeIn(durationMillis: Int): ImageRequest.Builder =
    apply {
        placeholder(android.R.color.transparent)
        transition(CrossfadeTransition(durationMillis, true))
    }
