/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.storage

import wiki.wear.openweartools.materialfiles.provider.smb.client.Authentication
import wiki.wear.openweartools.materialfiles.provider.smb.client.Authenticator
import wiki.wear.openweartools.materialfiles.provider.smb.client.Authority
import wiki.wear.openweartools.materialfiles.settings.Settings
import wiki.wear.openweartools.materialfiles.util.valueCompat

object SmbServerAuthenticator : Authenticator {
    private val transientServers = mutableSetOf<SmbServer>()

    override fun getAuthentication(authority: Authority): Authentication? {
        val server = synchronized(transientServers) {
            transientServers.find { it.authority == authority }
        } ?: Settings.STORAGES.valueCompat.find {
            it is SmbServer && it.authority == authority
        } as SmbServer?
        return server?.authentication
    }

    fun addTransientServer(server: SmbServer) {
        synchronized(transientServers) {
            transientServers += server
        }
    }

    fun removeTransientServer(server: SmbServer) {
        synchronized(transientServers) {
            transientServers -= server
        }
    }
}
