/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.filelist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.commit
import wiki.wear.openweartools.materialfiles.app.AppActivity
import wiki.wear.openweartools.materialfiles.util.args
import wiki.wear.openweartools.materialfiles.util.putArgs

class OpenFileAsDialogActivity : AppActivity() {
    private val args by args<OpenFileAsDialogFragment.Args>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Calls ensureSubDecor().
        findViewById<View>(android.R.id.content)
        if (savedInstanceState == null) {
            val fragment = OpenFileAsDialogFragment().putArgs(args)
            supportFragmentManager.commit {
                add(fragment, OpenFileAsDialogFragment::class.java.name)
            }
        }
    }
}
