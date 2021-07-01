/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.provider.linux.syscall

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import wiki.wear.openweartools.materialfiles.provider.common.ByteString

@Parcelize
class StructMntent(
    val mnt_fsname: ByteString,
    val mnt_dir: ByteString,
    val mnt_type: ByteString,
    val mnt_opts: ByteString,
    val mnt_freq: Int,
    val mnt_passno: Int
) : Parcelable
