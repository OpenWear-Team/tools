/*
 * Copyright (c) 2021 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.provider.sftp

import java8.nio.file.Path
import wiki.wear.openweartools.materialfiles.provider.sftp.client.Authority

fun Authority.createSftpRootPath(): Path =
    SftpFileSystemProvider.getOrNewFileSystem(this).rootDirectory
