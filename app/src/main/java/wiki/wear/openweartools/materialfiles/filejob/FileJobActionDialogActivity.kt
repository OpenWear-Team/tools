/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.filejob

import android.os.Bundle
import android.view.View
import androidx.fragment.app.commit
import wiki.wear.openweartools.materialfiles.app.AppActivity
import wiki.wear.openweartools.materialfiles.util.args
import wiki.wear.openweartools.materialfiles.util.putArgs

class FileJobActionDialogActivity : AppActivity() {
    private val args by args<FileJobActionDialogFragment.Args>()

    private lateinit var fragment: FileJobActionDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Calls ensureSubDecor().
        findViewById<View>(android.R.id.content)
        if (savedInstanceState == null) {
            fragment = FileJobActionDialogFragment().putArgs(args)
            supportFragmentManager.commit {
                add(fragment, FileJobActionDialogFragment::class.java.name)
            }
        } else {
            fragment = supportFragmentManager.findFragmentByTag(
                FileJobActionDialogFragment::class.java.name
            ) as FileJobActionDialogFragment
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (isFinishing) {
            fragment.onFinish()
        }
    }
}
