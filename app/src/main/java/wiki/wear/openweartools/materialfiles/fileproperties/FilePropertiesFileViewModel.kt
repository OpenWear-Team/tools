/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.fileproperties

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import wiki.wear.openweartools.materialfiles.file.FileItem
import wiki.wear.openweartools.materialfiles.util.Stateful

class FilePropertiesFileViewModel(file: FileItem) : ViewModel() {
    private val _fileLiveData = FileLiveData(file)
    val fileLiveData: LiveData<Stateful<FileItem>>
        get() = _fileLiveData

    fun reload() {
        _fileLiveData.loadValue()
    }

    override fun onCleared() {
        _fileLiveData.close()
    }
}
