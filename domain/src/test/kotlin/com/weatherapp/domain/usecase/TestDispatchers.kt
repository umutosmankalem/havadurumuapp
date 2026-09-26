package com.weatherapp.domain.usecase

import com.weatherapp.core.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher

internal fun testDispatchers(dispatcher: CoroutineDispatcher = StandardTestDispatcher()): DispatcherProvider {
    return object : DispatcherProvider {
        override val io = dispatcher
        override val default = dispatcher
        override val main = dispatcher
    }
}
