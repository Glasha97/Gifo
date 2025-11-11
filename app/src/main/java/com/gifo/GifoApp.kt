package com.gifo

import android.app.Application
import com.gifo.common.koin.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.ksp.generated.module

class GifoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            if (BuildConfig.DEBUG) {
                androidLogger()
            }

            androidContext(this@GifoApp)
            modules(AppModule().module)
        }
    }
}
