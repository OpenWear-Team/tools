/*
 * Copyright (c) 2021 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.util

import kotlinx.coroutines.flow.MutableStateFlow

@Suppress("FunctionName", "UNCHECKED_CAST")
fun <T : Any> LateInitMutableStateFlow(): MutableStateFlow<T> =
    MutableStateFlow<T?>(null) as MutableStateFlow<T>
