/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.provider.smb.client

interface Authenticator {
    fun getAuthentication(authority: Authority): Authentication?
}
