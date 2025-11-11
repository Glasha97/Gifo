package com.gifo.features.gif.details

import com.gifo.common.core.BaseVm
import com.gifo.features.gif.models.GifUi
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam

@KoinViewModel
internal class GifDetailsViewModel(@InjectedParam gifUi: GifUi) : BaseVm<GifDetailsScreenState>(GifDetailsScreenState(gifUi))