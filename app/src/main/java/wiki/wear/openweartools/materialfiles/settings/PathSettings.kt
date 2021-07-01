/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.settings

import java8.nio.file.Path
import wiki.wear.openweartools.R
import wiki.wear.openweartools.materialfiles.filelist.FileSortOptions

object PathSettings {
    private const val NAME_SUFFIX = "path"

    @Suppress("UNCHECKED_CAST")
    fun getFileListSortOptions(path: Path): SettingLiveData<FileSortOptions?> =
        ParcelValueSettingLiveData(
            NAME_SUFFIX, R.string.pref_key_file_list_sort_options, path.toString(), null
        )
}
