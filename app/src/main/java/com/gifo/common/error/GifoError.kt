package com.gifo.common.error


internal sealed class GifoError(
    override val message: String,
    override val cause: Throwable? = null,
) : Exception(message, cause) {
    data class Network(override val message: String, val code: Int) : GifoError(message)
    data object Unknown : GifoError("Unknown error") {
        private fun readResolve(): Any = Unknown
    }
}
