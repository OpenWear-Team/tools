/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.util

fun Long.hasBits(bits: Long): Boolean = this and bits == bits

infix fun Long.andInv(other: Long): Long = this and other.inv()
