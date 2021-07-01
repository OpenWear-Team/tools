/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.provider.common

import java.io.IOException

abstract class PosixFileStore : AbstractFileStore() {
    @Throws(IOException::class)
    abstract fun refresh()

    @Throws(IOException::class)
    abstract fun setReadOnly(readOnly: Boolean)
}
