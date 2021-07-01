/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.filelist

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import kotlinx.parcelize.Parcelize
import wiki.wear.openweartools.R
import wiki.wear.openweartools.databinding.CreateArchiveDialogBinding
import wiki.wear.openweartools.databinding.FileNameDialogNameIncludeBinding
import wiki.wear.openweartools.materialfiles.util.ParcelableArgs
import wiki.wear.openweartools.materialfiles.util.args
import wiki.wear.openweartools.materialfiles.util.putArgs
import wiki.wear.openweartools.materialfiles.util.setTextWithSelection
import wiki.wear.openweartools.materialfiles.util.show
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.compressors.CompressorStreamFactory

class CreateArchiveDialogFragment : FileNameDialogFragment() {
    private val args by args<Args>()

    override val binding: Binding
        get() = super.binding as Binding

    override val listener: Listener
        get() = super.listener as Listener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        if (savedInstanceState == null) {
            val files = args.files
            var name: String? = null
            if (files.size == 1) {
                name = files.single().path.fileName.toString()
            } else {
                val parent = files.mapTo(mutableSetOf()) { it.path.parent }.singleOrNull()
                if (parent != null && parent.nameCount > 0) {
                    name = parent.fileName.toString()
                }
            }
            name?.let { binding.nameEdit.setTextWithSelection(it) }
        }
        return dialog
    }

    @StringRes
    override val titleRes: Int = R.string.file_create_archive_title

    override fun onInflateBinding(inflater: LayoutInflater): FileNameDialogFragment.Binding =
        Binding.inflate(inflater)

    override val name: String
        get() {
            val extension = when (val typeId = binding.typeGroup.checkedRadioButtonId) {
                R.id.zipRadio -> "zip"
                R.id.tarXzRadio -> "tar.xz"
                R.id.sevenZRadio -> "7z"
                else -> throw AssertionError(typeId)
            }
            return "${super.name}.$extension"
        }

    override fun onOk(name: String) {
        val archiveType: String
        val compressorType: String?
        when (val typeId = binding.typeGroup.checkedRadioButtonId) {
            R.id.zipRadio -> {
                archiveType = ArchiveStreamFactory.ZIP
                compressorType = null
            }
            R.id.tarXzRadio -> {
                archiveType = ArchiveStreamFactory.TAR
                compressorType = CompressorStreamFactory.XZ
            }
            R.id.sevenZRadio -> {
                archiveType = ArchiveStreamFactory.SEVEN_Z
                compressorType = null
            }
            else -> throw AssertionError(typeId)
        }
        listener.archive(args.files, name, archiveType, compressorType)
    }

    companion object {
        fun show(files: FileItemSet, fragment: Fragment) {
            CreateArchiveDialogFragment().putArgs(Args(files)).show(fragment)
        }
    }

    @Parcelize
    class Args(val files: FileItemSet) : ParcelableArgs

    protected class Binding private constructor(
        root: View,
        nameLayout: TextInputLayout,
        nameEdit: EditText,
        val typeGroup: RadioGroup
    ) : FileNameDialogFragment.Binding(root, nameLayout, nameEdit) {
        companion object {
            fun inflate(inflater: LayoutInflater): Binding {
                val binding = CreateArchiveDialogBinding.inflate(inflater)
                val bindingRoot = binding.root
                val nameBinding = FileNameDialogNameIncludeBinding.bind(bindingRoot)
                return Binding(
                    bindingRoot, nameBinding.nameLayout, nameBinding.nameEdit, binding.typeGroup
                )
            }
        }
    }

    interface Listener : FileNameDialogFragment.Listener {
        fun archive(files: FileItemSet, name: String, archiveType: String, compressorType: String?)
    }
}
