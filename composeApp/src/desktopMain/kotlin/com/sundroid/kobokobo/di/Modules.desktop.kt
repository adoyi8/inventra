package com.sundroid.kobokobo.di

import com.sundroid.kobokobo.dashboard.data.database.ProductDatabaseFactory
import com.sundroid.kobokobo.user.data.database.UserDatabaseFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single { ProductDatabaseFactory()}
        single { UserDatabaseFactory() }
    }