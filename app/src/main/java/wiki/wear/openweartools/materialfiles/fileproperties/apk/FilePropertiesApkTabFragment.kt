/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.fileproperties.apk

import android.os.Build
import android.os.Bundle
import java8.nio.file.Path
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith
import wiki.wear.openweartools.R
import wiki.wear.openweartools.materialfiles.compat.longVersionCodeCompat
import wiki.wear.openweartools.materialfiles.file.FileItem
import wiki.wear.openweartools.materialfiles.file.isApk
import wiki.wear.openweartools.materialfiles.fileproperties.FilePropertiesTabFragment
import wiki.wear.openweartools.materialfiles.provider.linux.isLinuxPath
import wiki.wear.openweartools.materialfiles.util.ParcelableArgs
import wiki.wear.openweartools.materialfiles.util.ParcelableParceler
import wiki.wear.openweartools.materialfiles.util.Stateful
import wiki.wear.openweartools.materialfiles.util.args
import wiki.wear.openweartools.materialfiles.util.getQuantityString
import wiki.wear.openweartools.materialfiles.util.getStringArray
import wiki.wear.openweartools.materialfiles.util.viewModels

class FilePropertiesApkTabFragment : FilePropertiesTabFragment() {
    private val args by args<Args>()

    private val viewModel by viewModels { { FilePropertiesApkTabViewModel(args.path) } }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.apkInfoLiveData.observe(viewLifecycleOwner) { onApkInfoChanged(it) }
    }

    override fun refresh() {
        viewModel.reload()
    }

    private fun onApkInfoChanged(stateful: Stateful<ApkInfo>) {
        bindView(stateful) { apkInfo ->
            addItemView(R.string.file_properties_apk_label, apkInfo.label)
            val packageInfo = apkInfo.packageInfo
            addItemView(R.string.file_properties_apk_package_name, packageInfo.packageName)
            addItemView(
                R.string.file_properties_apk_version, getString(
                    R.string.file_properties_apk_version_format, packageInfo.versionName,
                    packageInfo.longVersionCodeCompat
                )
            )
            val applicationInfo = packageInfo.applicationInfo
            // PackageParser didn't return minSdkVersion before N, so it's hard to implement a
            // compat version.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                addItemView(
                    R.string.file_properties_apk_min_sdk_version,
                    getSdkVersionText(applicationInfo.minSdkVersion)
                )
            }
            addItemView(
                R.string.file_properties_apk_target_sdk_version,
                getSdkVersionText(applicationInfo.targetSdkVersion)
            )
            val requestedPermissionsSize = packageInfo.requestedPermissions?.size ?: 0
            addItemView(
                R.string.file_properties_apk_requested_permissions,
                if (requestedPermissionsSize == 0) {
                    getString(R.string.file_properties_apk_requested_permissions_zero)
                } else {
                    getQuantityString(
                        R.plurals.file_properties_apk_requested_permissions_positive_format,
                        requestedPermissionsSize, requestedPermissionsSize
                    )
                }, if (requestedPermissionsSize == 0) {
                    null
                } else {
                    {
                        PermissionListDialogFragment.show(
                            packageInfo.requestedPermissions, this@FilePropertiesApkTabFragment
                        )
                    }
                }
            )
            addItemView(
                R.string.file_properties_apk_signature_digests,
                if (apkInfo.signingCertificateDigests.isNotEmpty()) {
                    apkInfo.signingCertificateDigests.joinToString("\n")
                } else {
                    getString(R.string.file_properties_apk_signature_digests_empty)
                }
            )
            if (apkInfo.pastSigningCertificateDigests.isNotEmpty()) {
                addItemView(
                    R.string.file_properties_apk_past_signature_digests,
                    apkInfo.pastSigningCertificateDigests.joinToString("\n")
                )
            }
        }
    }

    private fun getSdkVersionText(sdkVersion: Int): String {
        val names = getStringArray(R.array.file_properites_apk_sdk_version_names)
        val codeNames = getStringArray(R.array.file_properites_apk_sdk_version_codenames)
        return getString(
            R.string.file_properites_apk_sdk_version_format,
            names[sdkVersion.coerceIn(names.indices)],
            codeNames[sdkVersion.coerceIn(codeNames.indices)], sdkVersion
        )
    }

    companion object {
        fun isAvailable(file: FileItem): Boolean = file.mimeType.isApk && file.path.isLinuxPath
    }

    @Parcelize
    class Args(val path: @WriteWith<ParcelableParceler> Path) : ParcelableArgs
}
