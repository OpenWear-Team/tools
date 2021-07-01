/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.about

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import wiki.wear.openweartools.databinding.AboutFragmentBinding
import wiki.wear.openweartools.materialfiles.ui.LicensesDialogFragment
import wiki.wear.openweartools.materialfiles.util.createViewIntent
import wiki.wear.openweartools.materialfiles.util.startActivitySafe

class AboutFragment : Fragment() {
    private lateinit var binding: AboutFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        AboutFragmentBinding.inflate(inflater, container, false)
            .also { binding = it }
            .root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.gitHubLayout.setOnClickListener { startActivitySafe(GITHUB_URI.createViewIntent()) }
        binding.licensesLayout.setOnClickListener { LicensesDialogFragment.show(this) }
        binding.authorNameLayout.setOnClickListener {
            startActivitySafe(AUTHOR_RESUME_URI.createViewIntent())
        }
        binding.authorGitHubLayout.setOnClickListener {
            startActivitySafe(AUTHOR_GITHUB_URI.createViewIntent())
        }
        binding.authorGooglePlusLayout.setOnClickListener {
            startActivitySafe(AUTHOR_GOOGLE_PLUS_URI.createViewIntent())
        }
        binding.authorTwitterLayout.setOnClickListener {
            startActivitySafe(AUTHOR_TWITTER_URI.createViewIntent())
        }
    }

    companion object {
        private val GITHUB_URI = Uri.parse("https://github.com/zhanghai/MaterialFiles")
        private val PRIVACY_POLICY_URI =
            Uri.parse("https://github.com/zhanghai/MaterialFiles/blob/master/PRIVACY.md")
        private val AUTHOR_RESUME_URI = Uri.parse("https://resume.zhanghai.me/")
        private val AUTHOR_GITHUB_URI = Uri.parse("https://github.com/zhanghai")
        private val AUTHOR_GOOGLE_PLUS_URI =
            Uri.parse("https://plus.google.com/100015937320889992498")
        private val AUTHOR_TWITTER_URI = Uri.parse("https://twitter.com/zhanghai95")
    }
}
