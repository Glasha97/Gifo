package com.gifo.common.core

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.gifo.common.compose.components.LoadingTextWithDots
import com.gifo.common.flow.ObserveAsEvents
import com.gifo.common.resource.DrawableResource
import kotlinx.coroutines.launch

@Composable
@NonRestartableComposable
fun <Vm> BaseComposeScreen(
    modifier: Modifier = Modifier,
    vm: BaseVm<Vm>,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(vm.errorEvent, snackBarHostState) { error ->
        scope.launch {
            snackBarHostState.currentSnackbarData?.dismiss()
            snackBarHostState.showSnackbar(error)
        }
    }

    Box(modifier.fillMaxSize()) {
        val isLoading by vm.progressVisibility.collectAsStateWithLifecycle()

        content()

        AnimatedVisibility(
            visible = isLoading,
            modifier = Modifier.align(Alignment.Center),
            enter = fadeIn() + expandIn(expandFrom = Alignment.Center),
            exit = shrinkOut(shrinkTowards = Alignment.Center) + fadeOut(),
        ) {
            Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(DrawableResource.loading_state)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(128.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .aspectRatio(1f),
                    contentScale = ContentScale.Crop,
                )

                Spacer(modifier = Modifier.size(8.dp))

                LoadingTextWithDots()
            }
        }

        SnackbarHost(
            modifier = Modifier.align(Alignment.BottomCenter),
            hostState = snackBarHostState,
            snackbar = {
                Snackbar(
                    snackbarData = it,
                    shape = RoundedCornerShape(4.dp),
                )
            }
        )
    }
}
