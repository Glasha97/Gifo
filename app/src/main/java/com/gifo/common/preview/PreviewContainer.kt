package com.gifo.common.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import com.gifo.design.theme.GifoTheme
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

@Composable
fun PreviewContainer(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    if (LocalInspectionMode.current && GlobalContext.getOrNull() == null) {
        startKoin {
            modules(emptyList())
        }
    }

    GifoTheme {
        Column(modifier = modifier.background(Color.White)) {
            content()
        }
    }
}