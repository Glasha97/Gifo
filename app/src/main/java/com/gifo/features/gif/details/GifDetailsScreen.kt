package com.gifo.features.gif.details

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.gifo.common.compose.components.Button
import com.gifo.common.core.BaseComposeScreen
import com.gifo.common.preview.PreviewContainer
import com.gifo.common.resource.DrawableResource
import com.gifo.common.resource.StringResource
import com.gifo.common.utils.copyToClickboard
import com.gifo.features.gif.models.GifUi
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun GifDetailsScreen(gifUi: GifUi, onBack: () -> Unit) {
    val vm = koinViewModel<GifDetailsViewModel> { parametersOf(gifUi) }
    val state by vm.state.collectAsStateWithLifecycle()

    BaseComposeScreen(vm = vm) {
        val gifLabel = stringResource(StringResource.gif_url_copied)
        val configuration = LocalConfiguration.current
        when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                HorizontalContent(
                    state = state,
                    showSnackbarMessage = { vm.sendSnackbarMessage(gifLabel) },
                    onBack = onBack,
                )
            }

            else -> {
                PortraitContent(
                    state = state,
                    showSnackbarMessage = { vm.sendSnackbarMessage(gifLabel) },
                    onBack = onBack,
                )
            }
        }
    }
}

@Composable
private fun PortraitContent(
    state: GifDetailsScreenState,
    modifier: Modifier = Modifier,
    showSnackbarMessage: () -> Unit,
    onBack: () -> Unit,
) {
    val gif = state.gif ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Box {
            Icon(
                painter = painterResource(DrawableResource.ic_back),
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.CenterStart)
                    .clip(CircleShape)
                    .clickable(onClick = onBack)
                    .padding(2.dp),
                tint = MaterialTheme.colorScheme.inverseSurface,
            )

            Text(
                text = gif.title ?: stringResource(StringResource.gif_fallback_title),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.scrim,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(gif.gifUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 64.dp)
                .clip(RoundedCornerShape(12.dp))
                .aspectRatio(1f),
            contentScale = ContentScale.Crop,
        )

        Spacer(modifier = Modifier.height(24.dp))

        TextValueBlock(
            label = stringResource(StringResource.author_label),
            value = gif.username?.ifBlank { stringResource(StringResource.author_not_found) }
                ?: stringResource(StringResource.author_not_found),
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextValueBlock(
            label = stringResource(StringResource.created_at_label),
            value = gif.importDatetime,
        )

        Spacer(modifier = Modifier.weight(1f))

        val context = LocalContext.current

        Button(
            text = stringResource(StringResource.copy_url_to_clipboard),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                context.copyToClickboard(gif.url)
                showSnackbarMessage()
            },
        )
    }
}

@Composable
private fun HorizontalContent(
    state: GifDetailsScreenState,
    modifier: Modifier = Modifier,
    showSnackbarMessage: () -> Unit,
    onBack: () -> Unit,
) {
    val gif = state.gif ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box {
            Icon(
                painter = painterResource(DrawableResource.ic_back),
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.CenterStart)
                    .clip(CircleShape)
                    .clickable(onClick = onBack)
                    .padding(2.dp),
                tint = MaterialTheme.colorScheme.inverseSurface,
            )

            Text(
                text = gif.title ?: stringResource(StringResource.gif_fallback_title),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.scrim,
                textAlign = TextAlign.Center,
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(gif.gifUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop,
            )

            Spacer(modifier = Modifier.width(24.dp))

            Column {
                TextValueBlock(
                    label = stringResource(StringResource.author_label),
                    value = gif.username?.ifBlank { stringResource(StringResource.author_not_found) }
                        ?: stringResource(StringResource.author_not_found),
                )

                Spacer(modifier = Modifier.height(12.dp))

                TextValueBlock(
                    label = stringResource(StringResource.created_at_label),
                    value = gif.importDatetime,
                )

                Spacer(modifier = Modifier.height(24.dp))

                val context = LocalContext.current

                Button(
                    text = stringResource(StringResource.copy_url_to_clipboard),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        context.copyToClickboard(gif.url)
                        showSnackbarMessage()
                    },
                )
            }
        }
    }
}

@Composable
private fun TextValueBlock(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.scrim,
        )
    }
}

@Preview
@Composable
private fun GifDetailsScreenPreview() {
    PreviewContainer {
        PortraitContent(state = GifDetailsScreenState(), showSnackbarMessage = {}, onBack = {})
    }
}