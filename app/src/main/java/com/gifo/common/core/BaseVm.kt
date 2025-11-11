package com.gifo.common.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger

abstract class BaseVm<S>(initialState: S) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> get() = _state

    private val _snackbarEvent = Channel<String>()
    internal val errorEvent: Flow<String> = _snackbarEvent.receiveAsFlow()

    private val _progressVisibility = MutableStateFlow(false)
    val progressVisibility: StateFlow<Boolean> = _progressVisibility
    private var activeJobCounter = AtomicInteger(0)

    private val handler = CoroutineExceptionHandler { _, exception -> sendError(exception) }

    fun update(function: (S) -> S) {
        _state.update(function)
    }

    fun startJob(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        withLoader: Boolean = true,
        block: suspend CoroutineScope.() -> Unit,
    ): Job {

        if (withLoader) {
            activeJobCounter.incrementAndGet()
            _progressVisibility.update { true }
        }

        val job = viewModelScope.launch(dispatcher + handler, block = block)

        job.invokeOnCompletion {
            if (withLoader) {
                val loaderCounter = activeJobCounter.decrementAndGet()
                if (loaderCounter <= 0) {
                    _progressVisibility.update { false }
                    activeJobCounter.updateAndGet { 0 }
                }
            }
        }

        return job
    }

    fun sendSnackbarMessage(message: String) {
        _snackbarEvent.trySend(message)
    }

    protected fun sendError(exception: Throwable) {
        _snackbarEvent.trySend(exception.message ?: "Something went wrong")
    }
}