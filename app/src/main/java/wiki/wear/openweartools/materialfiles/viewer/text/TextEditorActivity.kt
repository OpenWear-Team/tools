/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.viewer.text

import android.os.Bundle
import android.view.View
import androidx.fragment.app.commit
import wiki.wear.openweartools.materialfiles.app.AppActivity
import wiki.wear.openweartools.materialfiles.util.putArgs

class TextEditorActivity : AppActivity() {
    private lateinit var fragment: TextEditorFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Calls ensureSubDecor().
        findViewById<View>(android.R.id.content)
        if (savedInstanceState == null) {
            fragment = TextEditorFragment().putArgs(TextEditorFragment.Args(intent))
            supportFragmentManager.commit { add(android.R.id.content, fragment) }
        } else {
            fragment = supportFragmentManager.findFragmentById(android.R.id.content)
                as TextEditorFragment
        }
    }

    override fun onBackPressed() {
        if (fragment.onFinish()) {
            return
        }
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        if (fragment.onFinish()) {
            return true
        }
        return super.onSupportNavigateUp()
    }
}
