/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.provider.smb

import java8.nio.file.Path
import wiki.wear.openweartools.materialfiles.provider.smb.client.Authority

fun Authority.createSmbRootPath(): Path =
    SmbFileSystemProvider.getOrNewFileSystem(this).rootDirectory
