/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.ftpserver

import android.net.wifi.WifiManager
import android.os.PowerManager
import wiki.wear.openweartools.materialfiles.app.powerManager
import wiki.wear.openweartools.materialfiles.app.wifiManager

class FtpServerWakeLock {
    private val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_TAG)
        .apply { setReferenceCounted(false) }
    private val wifiLock =
        wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, LOCK_TAG)
            .apply { setReferenceCounted(false) }

    fun acquire() {
        wakeLock.acquire()
        wifiLock.acquire()
    }

    fun release() {
        wifiLock.release()
        wakeLock.release()
    }

    companion object {
        private val LOCK_TAG = FtpServerWakeLock::class.java.simpleName
    }
}
