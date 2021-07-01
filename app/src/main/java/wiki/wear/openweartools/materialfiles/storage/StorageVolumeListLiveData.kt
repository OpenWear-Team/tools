/*
 * Copyright (c) 2021 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.storage

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.storage.StorageVolume
import androidx.lifecycle.LiveData
import wiki.wear.openweartools.materialfiles.app.application
import wiki.wear.openweartools.materialfiles.app.storageManager
import wiki.wear.openweartools.materialfiles.compat.storageVolumesCompat

object StorageVolumeListLiveData : LiveData<List<StorageVolume>>() {
    init {
        loadValue()
        application.registerReceiver(
            object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    loadValue()
                }
            }, IntentFilter().apply {
                // @see android.os.storage.VolumeInfo#sEnvironmentToBroadcast
                addAction(Intent.ACTION_MEDIA_UNMOUNTED)
                addAction(Intent.ACTION_MEDIA_CHECKING)
                addAction(Intent.ACTION_MEDIA_MOUNTED)
                addAction(Intent.ACTION_MEDIA_EJECT)
                addAction(Intent.ACTION_MEDIA_UNMOUNTABLE)
                addAction(Intent.ACTION_MEDIA_REMOVED)
                addAction(Intent.ACTION_MEDIA_BAD_REMOVAL)
            })
    }

    private fun loadValue() {
        value = storageManager.storageVolumesCompat
    }
}
