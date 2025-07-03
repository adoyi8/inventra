package com.ikemba.inventrar.di


import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.ikemba.inventrar.business.data.domain.BusinessRepository
import com.ikemba.inventrar.business.network.KtorRemoteBusinessDataSource
import com.ikemba.inventrar.business.network.RemoteBusinessDataSource
import com.ikemba.inventrar.cart.data.network.KtorRemoteCartDataSource
import com.ikemba.inventrar.cart.data.network.RemoteCartDataSource
import com.ikemba.inventrar.cart.data.repository.DefaultCartRepository
import com.ikemba.inventrar.cart.domain.CartRepository
import com.ikemba.inventrar.core.data.HttpClientFactory
import com.ikemba.inventrar.dashboard.data.database.ProductDatabase
import com.ikemba.inventrar.dashboard.data.database.ProductDatabaseFactory
import com.ikemba.inventrar.dashboard.data.network.KtorRemoteProductDataSource
import com.ikemba.inventrar.dashboard.data.network.RemoteProductDataSource
import com.ikemba.inventrar.dashboard.data.repository.DefaultProductRepository
import com.ikemba.inventrar.dashboard.domain.ProductRepository
import com.ikemba.inventrar.dashboard.presentation.DashboardViewModel
import com.ikemba.inventrar.heldOrder.data.domain.HeldOrderRepository
import com.ikemba.inventrar.heldOrder.data.network.KtorRemoteHeldOrderDataSource
import com.ikemba.inventrar.heldOrder.data.network.RemoteHeldOrderDataSource
import com.ikemba.inventrar.heldOrder.data.repository.DefaultHeldOrderRepository
import com.ikemba.inventrar.heldOrder.presentation.HeldOrderViewModel
import com.ikemba.inventrar.login.presentation.UserViewModel
import com.ikemba.inventrar.splashScreen.presentation.SplashScreenViewModel
import com.ikemba.inventrar.transactionHistory.data.network.KtorRemoteTransactionHistoryDataSource
import com.ikemba.inventrar.transactionHistory.data.network.RemoteTransactionHistoryDataSource
import com.ikemba.inventrar.transactionHistory.data.repository.DefaultTransactionHistoryRepository
import com.ikemba.inventrar.transactionHistory.domain.TransactionHistoryRepository
import com.ikemba.inventrar.transactionHistory.presentation.TransactionHistoryViewModel
import com.ikemba.inventrar.user.data.database.UserDatabase
import com.ikemba.inventrar.user.data.database.UserDatabaseFactory
import com.ikemba.inventrar.user.data.network.KtorRemoteUserDataSource
import com.ikemba.inventrar.user.data.network.RemoteUserDataSource
import com.ikemba.inventrar.user.data.repository.DefaultUserRepository
import com.ikemba.inventrar.user.domain.UserRepository
import com.ikemba.inventrar.changePassword.presentation.ChangePasswordViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

import com.ikemba.inventrar.business.data.repository.DefaultBusinessRepository

expect val platformModule: Module

val sharedModule = module {
    single { HttpClientFactory.create(get()) }

    singleOf(::DefaultUserRepository).bind<UserRepository>()
    singleOf(::KtorRemoteUserDataSource).bind<RemoteUserDataSource>()
    singleOf(::DefaultProductRepository).bind<ProductRepository>()
    singleOf(::KtorRemoteProductDataSource).bind<RemoteProductDataSource>()
    singleOf(::DefaultCartRepository).bind<CartRepository>()
    singleOf(::KtorRemoteCartDataSource).bind<RemoteCartDataSource>()
    singleOf(::DefaultTransactionHistoryRepository).bind<TransactionHistoryRepository>()
    singleOf(::KtorRemoteTransactionHistoryDataSource).bind<RemoteTransactionHistoryDataSource>()
    singleOf(::DefaultHeldOrderRepository).bind<HeldOrderRepository>()
    singleOf(::KtorRemoteHeldOrderDataSource).bind<RemoteHeldOrderDataSource>()
    singleOf(::DefaultBusinessRepository).bind<BusinessRepository>()
    singleOf(::KtorRemoteBusinessDataSource).bind<RemoteBusinessDataSource>()

    single {
        get<UserDatabaseFactory>().create()
            .setDriver(BundledSQLiteDriver())
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
    single {
        get<ProductDatabaseFactory>().create()
            .setDriver(BundledSQLiteDriver())
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
    single { get<ProductDatabase>().productDao }

    single { get<UserDatabase>().userDao }

    single { get<ProductDatabase>().cartDao }
    single { get<ProductDatabase>().postSalesRequestDao }

    viewModelOf(::UserViewModel)
    viewModelOf(::SplashScreenViewModel)
    viewModelOf(::DashboardViewModel)
    viewModelOf(::TransactionHistoryViewModel)
    viewModelOf(::HeldOrderViewModel)
    viewModelOf(::ChangePasswordViewModel)
}