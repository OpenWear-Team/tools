/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.fileproperties.permissions

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import java8.nio.file.Path
import wiki.wear.openweartools.R
import wiki.wear.openweartools.materialfiles.file.FileItem
import wiki.wear.openweartools.materialfiles.filejob.FileJobService
import wiki.wear.openweartools.materialfiles.provider.common.PosixFileAttributes
import wiki.wear.openweartools.materialfiles.provider.common.PosixGroup
import wiki.wear.openweartools.materialfiles.provider.common.toByteString
import wiki.wear.openweartools.materialfiles.util.SelectionLiveData
import wiki.wear.openweartools.materialfiles.util.putArgs
import wiki.wear.openweartools.materialfiles.util.show
import wiki.wear.openweartools.materialfiles.util.viewModels

class SetGroupDialogFragment : SetPrincipalDialogFragment() {
    override val viewModel: SetPrincipalViewModel by viewModels { { SetGroupViewModel() } }

    @StringRes
    override val titleRes: Int = R.string.file_properties_permissions_set_group_title

    override fun createAdapter(selectionLiveData: SelectionLiveData<Int>): PrincipalListAdapter =
        GroupListAdapter(selectionLiveData)

    override val PosixFileAttributes.principal
        get() = group()!!

    override fun setPrincipal(path: Path, principal: PrincipalItem, recursive: Boolean) {
        val group = PosixGroup(principal.id, principal.name?.toByteString())
        FileJobService.setGroup(path, group, recursive, requireContext())
    }

    companion object {
        fun show(file: FileItem, fragment: Fragment) {
            SetGroupDialogFragment().putArgs(Args(file)).show(fragment)
        }
    }
}
