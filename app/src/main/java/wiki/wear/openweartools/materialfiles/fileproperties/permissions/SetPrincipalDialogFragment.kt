/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.fileproperties.permissions

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java8.nio.file.Path
import kotlinx.parcelize.Parcelize
import wiki.wear.openweartools.databinding.SetPrincipalDialogBinding
import wiki.wear.openweartools.materialfiles.file.FileItem
import wiki.wear.openweartools.materialfiles.provider.common.PosixFileAttributes
import wiki.wear.openweartools.materialfiles.provider.common.PosixPrincipal
import wiki.wear.openweartools.materialfiles.util.*

abstract class SetPrincipalDialogFragment : AppCompatDialogFragment() {
    private val args by args<Args>()

    protected abstract val viewModel: SetPrincipalViewModel

    private lateinit var binding: SetPrincipalDialogBinding

    private lateinit var adapter: PrincipalListAdapter

    private var pendingScrollToId: Int? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        MaterialAlertDialogBuilder(requireContext(), theme)
            .setTitle(titleRes)
            .apply {
                val selectionLiveData = viewModel.selectionLiveData
                if (selectionLiveData.value == null) {
                    val id = argsPrincipalId
                    selectionLiveData.value = id
                    pendingScrollToId = id
                }

                binding = SetPrincipalDialogBinding.inflate(context.layoutInflater)
                binding.filterEdit.doAfterTextChanged { viewModel.filter = it.toString() }
                binding.recyclerView.layoutManager = LinearLayoutManager(context)
                adapter = createAdapter(selectionLiveData)
                binding.recyclerView.adapter = adapter
                binding.recursiveCheck.isVisible = args.file.attributes.isDirectory
                setView(binding.root)

                viewModel.filteredPrincipalListLiveData.observe(this@SetPrincipalDialogFragment) {
                    onFilteredPrincipalListChanged(it)
                }
                selectionLiveData.observe(this@SetPrincipalDialogFragment, adapter)
            }
            .setPositiveButton(android.R.string.ok) { _, _ -> setPrincipal() }
            .setNegativeButton(android.R.string.cancel, null)
            .create()

    @get:StringRes
    protected abstract val titleRes: Int

    protected abstract fun createAdapter(
        selectionLiveData: SelectionLiveData<Int>
    ): PrincipalListAdapter

    private fun onFilteredPrincipalListChanged(stateful: Stateful<List<PrincipalItem>>) {
        when (stateful) {
            is Loading -> {
                binding.progress.fadeInUnsafe()
                binding.errorText.fadeOutUnsafe()
                binding.emptyView.fadeOutUnsafe()
                adapter.clear()
            }
            is Failure -> {
                binding.progress.fadeOutUnsafe()
                binding.errorText.fadeInUnsafe()
                binding.errorText.text = stateful.throwable.toString()
                binding.emptyView.fadeOutUnsafe()
                adapter.clear()
            }
            is Success -> {
                binding.progress.fadeOutUnsafe()
                binding.errorText.fadeOutUnsafe()
                binding.emptyView.fadeToVisibilityUnsafe(stateful.value.isEmpty())
                adapter.replace(stateful.value)
                pendingScrollToId?.let {
                    val position = adapter.findPositionByPrincipalId(it)
                    if (position != RecyclerView.NO_POSITION) {
                        binding.recyclerView.scrollToPosition(position)
                    }
                    this.pendingScrollToId = null
                }
            }
        }
    }

    private fun setPrincipal() {
        val id = viewModel.selectionLiveData.valueCompat
        val recursive = binding.recursiveCheck.isChecked
        if (!recursive) {
            if (id == argsPrincipalId) {
                return
            }
        }
        val principalListStateful = viewModel.principalListStateful
        if (principalListStateful !is Success) {
            return
        }
        val principal = principalListStateful.value.find { it.id == id } ?: return
        setPrincipal(args.file.path, principal, recursive)
    }

    private val argsPrincipalId: Int
        get() {
            val attributes = args.file.attributes as PosixFileAttributes
            return attributes.principal.id
        }

    protected abstract val PosixFileAttributes.principal: PosixPrincipal

    protected abstract fun setPrincipal(path: Path, principal: PrincipalItem, recursive: Boolean)

    @Parcelize
    class Args(val file: FileItem) : ParcelableArgs
}
