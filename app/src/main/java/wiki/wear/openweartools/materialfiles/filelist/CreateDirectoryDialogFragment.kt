/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.filelist

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import wiki.wear.openweartools.R
import wiki.wear.openweartools.materialfiles.util.show

class CreateDirectoryDialogFragment : FileNameDialogFragment() {
    override val listener: Listener
        get() = requireParentFragment() as Listener

    @StringRes
    override val titleRes: Int = R.string.file_create_directory_title

    override fun onOk(name: String) {
        listener.createDirectory(name)
    }

    companion object {
        fun show(fragment: Fragment) {
            CreateDirectoryDialogFragment().show(fragment)
        }
    }

    interface Listener : FileNameDialogFragment.Listener {
        fun createDirectory(name: String)
    }
}
