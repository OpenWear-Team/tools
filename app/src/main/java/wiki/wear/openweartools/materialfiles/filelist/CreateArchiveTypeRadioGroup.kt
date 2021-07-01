/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.filelist

import android.content.Context
import android.util.AttributeSet
import android.widget.RadioGroup
import wiki.wear.openweartools.materialfiles.settings.Settings
import wiki.wear.openweartools.materialfiles.util.valueCompat

class CreateArchiveTypeRadioGroup : RadioGroup {
    private var onCheckedChangeListener: OnCheckedChangeListener? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    init {
        check(Settings.CREATE_ARCHIVE_TYPE.valueCompat)
        super.setOnCheckedChangeListener { group, checkedId ->
            Settings.CREATE_ARCHIVE_TYPE.putValue(checkedId)
            onCheckedChangeListener?.onCheckedChanged(group, checkedId)
        }
    }

    override fun setOnCheckedChangeListener(listener: OnCheckedChangeListener?) {
        onCheckedChangeListener = listener
    }
}
