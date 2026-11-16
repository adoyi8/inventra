package com.sundroid.kobokobo.di



import com.sundroid.kobokobo.dashboard.data.database.ProductDatabaseFactory
import com.sundroid.kobokobo.user.data.database.UserDatabaseFactory
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

actual val platformModule: Module
    get() = module {
        single<HttpClientEngine> {
            OkHttp.create {
                // this: OkHttpConfig
                config {
                    // this: OkHttpClient.Builder
                    connectTimeout(60, TimeUnit.SECONDS)
                    readTimeout(60, TimeUnit.SECONDS)
                    writeTimeout(60, TimeUnit.SECONDS)
                }
            }
        }
        single { ProductDatabaseFactory(androidApplication()) }
        single { UserDatabaseFactory(androidApplication()) }
    }