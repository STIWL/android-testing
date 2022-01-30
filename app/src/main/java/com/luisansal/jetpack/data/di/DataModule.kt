package com.luisansal.jetpack.data.di

import android.content.Context
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.luisansal.jetpack.core.data.preferences.AuthSharedPreferences
import com.luisansal.jetpack.core.utils.getEncryptedSharedPreferences
import com.luisansal.jetpack.core.utils.listByElementsOf
import com.luisansal.jetpack.data.database.BaseRoomDatabase
import com.luisansal.jetpack.data.datastore.*
import com.luisansal.jetpack.data.network.RetrofitConfig
import com.luisansal.jetpack.data.preferences.ConfigSharedPreferences
import com.luisansal.jetpack.data.preferences.SyncSharedPreferences
import com.luisansal.jetpack.data.preferences.UserSharedPreferences
import com.luisansal.jetpack.data.repository.*
import com.luisansal.jetpack.domain.logs.LogRepository
import com.luisansal.jetpack.domain.network.ApiService
import com.luisansal.jetpack.domain.network.DirectionsApiService
import com.luisansal.jetpack.domain.network.PlacesApiService
import com.luisansal.jetpack.domain.repository.FirebaseAnalyticsRepository
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val preferencesModule = module {
    single { UserSharedPreferences(get<Context>().userPreferences) }
    single { AuthSharedPreferences(get<Context>().authPreferences) }
    single { SyncSharedPreferences(get<Context>().syncPreferences) }
    single { ConfigSharedPreferences(get<Context>().configPreferences) }
}

private val Context.userPreferences
    get() = getEncryptedSharedPreferences(UserSharedPreferences.PREFERENCES_NAME)

private val Context.authPreferences
    get() = getEncryptedSharedPreferences(AuthSharedPreferences.PREFERENCES_NAME)

private val Context.syncPreferences
    get() = getEncryptedSharedPreferences(SyncSharedPreferences.PREFERENCES_NAME)

private val Context.configPreferences
    get() = getEncryptedSharedPreferences(ConfigSharedPreferences.PREFERENCES_NAME)

const val USERS_REF = "users"
const val MARKERS_REF = "markers"
val databaseModule = module {
    single { BaseRoomDatabase.getDatabase(get()) }
    single { Firebase.database }
    single(named(USERS_REF)) { get<FirebaseDatabase>().getReference(USERS_REF) }
    single(named(MARKERS_REF)) { get<FirebaseDatabase>().getReference(MARKERS_REF) }
}

val networkModule = module {
    single { RetrofitConfig(ApiService.BASE_URL, get()).creteService(ApiService::class.java) }
    single { RetrofitConfig(PlacesApiService.GMAPS_URL, get()).creteService(PlacesApiService::class.java) }
    single { RetrofitConfig(DirectionsApiService.GMAPS_URL, get()).creteService(DirectionsApiService::class.java) }
}

internal val dataStoreModule = module {
    factory { FirebaseAnalyticsCloudDataStore(get()) }
    factory { EscribirArchivoLocalDataStore(get()) }
    factory { AuthCloudStore(get(), get(), get()) }
    factory { ChatCloudStore(get()) }
    factory { MapsCloudStore(get(), get(), get(), get()) }
}

internal val repositoryModule = module {
    factory { UserRepository(get()) }
    factory { VisitRepository(get()) }
    factory { SesionDataRepository(get(), get()) }
    factory<FirebaseAnalyticsRepository> { FirebaseAnalyticsDataRepository(get()) }
    factory<LogRepository> { LogLocalDataRepository(get()) }
    factory { PopulateRepository(get()) }
    factory { MapsRepository(get()) }
    factory { com.luisansal.jetpack.data.repository.datastore.FirebaseAnalyticsCloudDataStore(get()) }
    factory { com.luisansal.jetpack.data.repository.logs.EscribirArchivoLocalDataStore(get()) }
}

internal val dataModule by lazy {
    listByElementsOf<Module>(preferencesModule, repositoryModule, dataStoreModule, databaseModule, networkModule)
}