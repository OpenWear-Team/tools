/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.fileproperties.permissions

import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import coil.clear
import coil.loadAny
import wiki.wear.openweartools.R
import wiki.wear.openweartools.databinding.PrincipalItemBinding
import wiki.wear.openweartools.materialfiles.coil.ignoreError
import wiki.wear.openweartools.materialfiles.compat.getDrawableCompat
import wiki.wear.openweartools.materialfiles.ui.SimpleAdapter
import wiki.wear.openweartools.materialfiles.util.SelectionLiveData
import wiki.wear.openweartools.materialfiles.util.layoutInflater

abstract class PrincipalListAdapter(
    private val selectionLiveData: SelectionLiveData<Int>
) : SimpleAdapter<PrincipalItem, PrincipalListAdapter.ViewHolder>() {
    override val hasStableIds: Boolean
        get() = true

    override fun getItemId(position: Int): Long = getItem(position).id.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(PrincipalItemBinding.inflate(parent.context.layoutInflater, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        throw UnsupportedOperationException()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: List<Any>) {
        val principal = getItem(position)
        val binding = holder.binding
        binding.itemLayout.isChecked = selectionLiveData.value == principal.id
        if (payloads.isNotEmpty()) {
            return
        }
        binding.itemLayout.setOnClickListener {
            selectionLiveData.setValue(principal.id)
        }
        val icon = binding.iconImage.context.getDrawableCompat(principalIconRes)
        val applicationInfo = principal.applicationInfos.firstOrNull()
        if (applicationInfo != null) {
            binding.iconImage.loadAny(applicationInfo) {
                placeholder(icon)
                ignoreError()
            }
        } else {
            binding.iconImage.clear()
            binding.iconImage.setImageDrawable(icon)
        }
        binding.principalText.text = if (principal.name != null) {
            binding.principalText.context.getString(
                R.string.file_properties_permissions_principal_format, principal.name, principal.id
            )
        } else {
            principal.id.toString()
        }
        binding.labelText.text = principal.applicationLabels.firstOrNull()
            ?: binding.labelText.resources.getString(
                R.string.file_properties_permissions_set_principal_system
            )
    }

    @get:DrawableRes
    protected abstract val principalIconRes: Int

    fun findPositionByPrincipalId(id: Int): Int = findPositionById(id.toLong())

    class ViewHolder(val binding: PrincipalItemBinding) : RecyclerView.ViewHolder(binding.root)
}
