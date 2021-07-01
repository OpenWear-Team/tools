/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.app

import android.os.AsyncTask
import android.os.Build
import android.webkit.WebView
import com.facebook.stetho.Stetho
import com.jakewharton.threetenabp.AndroidThreeTen
import jcifs.context.SingletonContext
import wiki.wear.openweartools.BuildConfig
import wiki.wear.openweartools.materialfiles.coil.initializeCoil
import wiki.wear.openweartools.materialfiles.compat.RestrictedHiddenApiAccess
import wiki.wear.openweartools.materialfiles.filejob.fileJobNotificationTemplate
import wiki.wear.openweartools.materialfiles.ftpserver.ftpServerServiceNotificationTemplate
import wiki.wear.openweartools.materialfiles.provider.FileSystemProviders
import wiki.wear.openweartools.materialfiles.settings.Settings
import wiki.wear.openweartools.materialfiles.storage.SftpServerAuthenticator
import wiki.wear.openweartools.materialfiles.storage.SmbServerAuthenticator
import wiki.wear.openweartools.materialfiles.theme.custom.CustomThemeHelper
import wiki.wear.openweartools.materialfiles.theme.night.NightModeHelper
import java.util.Properties
import wiki.wear.openweartools.materialfiles.provider.sftp.client.Client as SftpClient
import wiki.wear.openweartools.materialfiles.provider.smb.client.Client as SmbClient

val appInitializers = listOf(
    ::initializeCrashlytics, ::allowRestrictedHiddenApiAccess, ::initializeThreeTen,
    ::initializeWebViewDebugging, ::initializeStetho, ::initializeCoil,
    ::initializeFileSystemProviders, ::upgradeApp, ::initializeSettings, ::initializeCustomTheme,
    ::initializeNightMode, ::createNotificationChannels
)

private fun initializeCrashlytics() {
}

private fun allowRestrictedHiddenApiAccess() {
    RestrictedHiddenApiAccess.allow()
}

private fun initializeThreeTen() {
    AndroidThreeTen.init(application)
}

private fun initializeWebViewDebugging() {
    if (BuildConfig.DEBUG) {
        WebView.setWebContentsDebuggingEnabled(true)
    }
}

private fun initializeStetho() {
    Stetho.initializeWithDefaults(application)
}

private fun initializeFileSystemProviders() {
    FileSystemProviders.install()
    FileSystemProviders.overflowWatchEvents = true
    // SingletonContext.init() calls NameServiceClientImpl.initCache() which connects to network.
    AsyncTask.THREAD_POOL_EXECUTOR.execute {
        SingletonContext.init(
            Properties().apply {
                setProperty("jcifs.netbios.cachePolicy", "0")
                setProperty("jcifs.smb.client.maxVersion", "SMB1")
            }
        )
    }
    SftpClient.authenticator = SftpServerAuthenticator
    SmbClient.authenticator = SmbServerAuthenticator
}

private fun initializeSettings() {
    // Force initialization of Settings so that it won't happen on a background thread.
    Settings.FILE_LIST_DEFAULT_DIRECTORY.value
}

private fun initializeCustomTheme() {
    CustomThemeHelper.initialize(application)
}

private fun initializeNightMode() {
    NightModeHelper.initialize(application)
}

private fun createNotificationChannels() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        notificationManager.createNotificationChannels(
            listOf(
                backgroundActivityStartNotificationTemplate.channelTemplate,
                fileJobNotificationTemplate.channelTemplate,
                ftpServerServiceNotificationTemplate.channelTemplate
            ).map { it.create(application) }
        )
    }
}
