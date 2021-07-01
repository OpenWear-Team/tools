/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.util

fun Any.hash(vararg values: Any?): Int = values.contentDeepHashCode()
