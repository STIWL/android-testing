package com.luisansal.jetpack.domain.di

import android.content.Context
import com.luisansal.jetpack.data.di.MARKERS_REF
import com.luisansal.jetpack.data.di.USERS_REF
import com.luisansal.jetpack.domain.usecases.*
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val usecasesModule = module {
    factory { FirebaseAnalyticsUseCase(get(), get(), get(), get(), get()) }
    factory { UserUseCase(get(), get(),get(named(USERS_REF))) }
    factory { VisitUseCase(get(),get()) }
    factory { PopulateUseCase(get()) }
    factory { LoginUseCase(get()) }
    factory { LoginFirebaseUseCase(get()) }
    factory { ChatUseCase(get()) }
    factory { MapsUseCase(get(),get(),get(named(MARKERS_REF))) }
}


internal val domainModule by lazy {
    listOf(usecasesModule)
}