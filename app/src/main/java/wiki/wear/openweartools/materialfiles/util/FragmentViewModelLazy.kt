/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package wiki.wear.openweartools.materialfiles.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

inline fun <reified VM : ViewModel> Fragment.viewModels(
    noinline ownerProducer: () -> ViewModelStoreOwner = { this },
    noinline factoryProducer: (() -> () -> VM)? = null
) = viewModels<VM>(
    ownerProducer,
    factoryProducer?.let {
        {
            val factory = it()
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel?> create(modelClass: Class<T>) = factory() as T
            }
        }
    }
)

inline fun <reified VM : ViewModel> Fragment.activityViewModels(
    noinline factoryProducer: (() -> () -> VM)? = null
) = viewModels(::requireActivity, factoryProducer)
