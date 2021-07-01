/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.provider.linux.syscall

import wiki.wear.openweartools.materialfiles.provider.common.ByteString

class StructPasswd(
    val pw_name: ByteString?,
    val pw_uid: Int,
    val pw_gid: Int,
    val pw_gecos: ByteString?,
    val pw_dir: ByteString?,
    val pw_shell: ByteString?
)
