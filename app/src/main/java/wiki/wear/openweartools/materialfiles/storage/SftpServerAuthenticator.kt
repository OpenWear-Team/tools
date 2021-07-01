/*
 * Copyright (c) 2021 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.storage

import wiki.wear.openweartools.materialfiles.provider.sftp.client.Authentication
import wiki.wear.openweartools.materialfiles.provider.sftp.client.Authenticator
import wiki.wear.openweartools.materialfiles.provider.sftp.client.Authority
import wiki.wear.openweartools.materialfiles.settings.Settings
import wiki.wear.openweartools.materialfiles.util.valueCompat

object SftpServerAuthenticator : Authenticator {
    private val transientServers = mutableSetOf<SftpServer>()

    override fun getAuthentication(authority: Authority): Authentication? {
        val server = synchronized(transientServers) {
            transientServers.find { it.authority == authority }
        } ?: Settings.STORAGES.valueCompat.find {
            it is SftpServer && it.authority == authority
        } as SftpServer?
        return server?.authentication
    }

    fun addTransientServer(server: SftpServer) {
        synchronized(transientServers) {
            transientServers += server
        }
    }

    fun removeTransientServer(server: SftpServer) {
        synchronized(transientServers) {
            transientServers -= server
        }
    }
}
