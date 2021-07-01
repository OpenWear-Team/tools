/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.about

import android.os.Bundle
import android.view.View
import androidx.fragment.app.add
import androidx.fragment.app.commit
import wiki.wear.openweartools.materialfiles.app.AppActivity

class AboutActivity : AppActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Calls ensureSubDecor().
        findViewById<View>(android.R.id.content)
        if (savedInstanceState == null) {
            supportFragmentManager.commit { add<AboutFragment>(android.R.id.content) }
        }
    }
}
