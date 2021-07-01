/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.provider.common

import java.io.Closeable

interface CloseableIterator<T> : Iterator<T>, Closeable
