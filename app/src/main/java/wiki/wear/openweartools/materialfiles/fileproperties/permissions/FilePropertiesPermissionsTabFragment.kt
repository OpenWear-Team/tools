/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.fileproperties.permissions

import android.os.Bundle
import wiki.wear.openweartools.R
import wiki.wear.openweartools.materialfiles.file.FileItem
import wiki.wear.openweartools.materialfiles.fileproperties.FilePropertiesFileViewModel
import wiki.wear.openweartools.materialfiles.fileproperties.FilePropertiesTabFragment
import wiki.wear.openweartools.materialfiles.provider.common.PosixFileAttributes
import wiki.wear.openweartools.materialfiles.provider.common.PosixPrincipal
import wiki.wear.openweartools.materialfiles.provider.common.isNullOrEmpty
import wiki.wear.openweartools.materialfiles.provider.common.toInt
import wiki.wear.openweartools.materialfiles.provider.common.toModeString
import wiki.wear.openweartools.materialfiles.util.Stateful
import wiki.wear.openweartools.materialfiles.util.viewModels

class FilePropertiesPermissionsTabFragment : FilePropertiesTabFragment() {
    private val viewModel by viewModels<FilePropertiesFileViewModel>({ requireParentFragment() })

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.fileLiveData.observe(viewLifecycleOwner) { onFileChanged(it) }
    }

    override fun refresh() {
        viewModel.reload()
    }

    private fun onFileChanged(stateful: Stateful<FileItem>) {
        bindView(stateful) { file ->
            val attributes = file.attributes as PosixFileAttributes
            val owner = attributes.owner()
            addItemView(R.string.file_properties_permissions_owner, getPrincipalText(owner)) {
                SetOwnerDialogFragment.show(file, this@FilePropertiesPermissionsTabFragment)
            }
            val group = attributes.group()
            addItemView(R.string.file_properties_permissions_group, getPrincipalText(group)) {
                SetGroupDialogFragment.show(file, this@FilePropertiesPermissionsTabFragment)
            }
            val mode = attributes.mode()
            addItemView(
                R.string.file_properties_permissions_mode, if (mode != null) {
                    getString(
                        R.string.file_properties_permissions_mode_format, mode.toModeString(),
                        mode.toInt()
                    )
                } else {
                    getString(R.string.unknown)
                }
            ) {
                if (!attributes.isSymbolicLink) {
                    SetModeDialogFragment.show(file, this@FilePropertiesPermissionsTabFragment)
                }
            }
            val seLinuxContext = attributes.seLinuxContext()
            addItemView(
                R.string.file_properties_permissions_selinux_context,
                if (!seLinuxContext.isNullOrEmpty()) {
                    seLinuxContext.toString()
                } else {
                    getString(R.string.empty_placeholder)
                }
            ) {
                SetSeLinuxContextDialogFragment.show(
                    file, this@FilePropertiesPermissionsTabFragment
                )
            }
        }
    }

    private fun getPrincipalText(principal: PosixPrincipal?) =
        if (principal != null) {
            if (principal.name != null) {
                getString(
                    R.string.file_properties_permissions_principal_format, principal.name,
                    principal.id
                )
            } else {
                principal.id.toString()
            }
        } else {
            getString(R.string.unknown)
        }

    companion object {
        fun isAvailable(file: FileItem): Boolean {
            val attributes = file.attributes
            return attributes is PosixFileAttributes && (attributes.owner() != null
                || attributes.group() != null || attributes.mode() != null
                || attributes.seLinuxContext() != null)
        }
    }
}
