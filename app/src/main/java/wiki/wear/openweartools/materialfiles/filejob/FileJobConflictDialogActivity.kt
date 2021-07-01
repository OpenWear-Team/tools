/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.filejob

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.commit
import wiki.wear.openweartools.materialfiles.app.AppActivity
import wiki.wear.openweartools.materialfiles.file.FileItem
import wiki.wear.openweartools.materialfiles.util.args
import wiki.wear.openweartools.materialfiles.util.putArgs

class FileJobConflictDialogActivity : AppActivity() {
    private val args by args<FileJobConflictDialogFragment.Args>()

    private lateinit var fragment: FileJobConflictDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Calls ensureSubDecor().
        findViewById<View>(android.R.id.content)
        if (savedInstanceState == null) {
            fragment = FileJobConflictDialogFragment().putArgs(args)
            supportFragmentManager.commit {
                add(fragment, FileJobConflictDialogFragment::class.java.name)
            }
        } else {
            fragment = supportFragmentManager.findFragmentByTag(
                FileJobConflictDialogFragment::class.java.name
            ) as FileJobConflictDialogFragment
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (isFinishing) {
            fragment.onFinish()
        }
    }

    companion object {
        fun getTitle(sourceFile: FileItem, targetFile: FileItem, context: Context): String =
            FileJobConflictDialogFragment.getTitle(sourceFile, targetFile, context)

        fun getMessage(
            sourceFile: FileItem,
            targetFile: FileItem,
            type: CopyMoveType,
            context: Context
        ): String = FileJobConflictDialogFragment.getMessage(sourceFile, targetFile, type, context)
    }
}
