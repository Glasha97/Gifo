package com.gifo.common.compose.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.gifo.common.resource.StringResource
import kotlinx.coroutines.delay


@Composable
fun LoadingTextWithDots(
    modifier: Modifier = Modifier,
    text: String = stringResource(StringResource.please_wait),
    dotAnimationDelay: Long = 500L,
    maxDots: Int = 3
) {
    var dotCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(dotAnimationDelay)
            dotCount = (dotCount + 1) % (maxDots + 1)
        }
    }

    Text(
        text = text + ".".repeat(dotCount),
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
    )
}