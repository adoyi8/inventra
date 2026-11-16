package com.sundroid.kobokobo.di

import com.sundroid.kobokobo.dashboard.data.database.ProductDatabaseFactory
import com.sundroid.kobokobo.user.data.database.UserDatabaseFactory
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single<HttpClientEngine> { OkHttp.create() }
        single { ProductDatabaseFactory()}
        single { UserDatabaseFactory() }
    }