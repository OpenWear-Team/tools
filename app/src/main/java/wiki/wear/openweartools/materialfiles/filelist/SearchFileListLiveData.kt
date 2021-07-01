/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.filelist

import android.os.AsyncTask
import java8.nio.file.Path
import wiki.wear.openweartools.materialfiles.file.FileItem
import wiki.wear.openweartools.materialfiles.file.loadFileItem
import wiki.wear.openweartools.materialfiles.provider.common.search
import wiki.wear.openweartools.materialfiles.util.CloseableLiveData
import wiki.wear.openweartools.materialfiles.util.Failure
import wiki.wear.openweartools.materialfiles.util.Loading
import wiki.wear.openweartools.materialfiles.util.Stateful
import wiki.wear.openweartools.materialfiles.util.Success
import wiki.wear.openweartools.materialfiles.util.valueCompat
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

class SearchFileListLiveData(
    private val path: Path,
    private val query: String
) : CloseableLiveData<Stateful<List<FileItem>>>() {
    private var future: Future<Unit>? = null

    init {
        loadValue()
    }

    fun loadValue() {
        future?.cancel(true)
        value = Loading(emptyList())
        future = (AsyncTask.THREAD_POOL_EXECUTOR as ExecutorService).submit<Unit> {
            val fileList = mutableListOf<FileItem>()
            try {
                path.search(query, INTERVAL_MILLIS) { paths: List<Path> ->
                    for (path in paths) {
                        val fileItem = try {
                            path.loadFileItem()
                        } catch (e: IOException) {
                            e.printStackTrace()
                            // TODO: Support file without information.
                            continue
                        }
                        fileList.add(fileItem)
                    }
                    postValue(Loading(fileList.toList()))
                }
                postValue(Success(fileList))
            } catch (e: Exception) {
                // TODO: Retrieval of previous value is racy.
                postValue(Failure(valueCompat.value, e))
            }
        }
    }

    override fun close() {
        future?.cancel(true)
    }

    companion object {
        private const val INTERVAL_MILLIS = 500L
    }
}
