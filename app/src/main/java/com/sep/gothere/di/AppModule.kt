package com.sep.gothere.di

import android.content.Context
import com.sep.gothere.api.GoThereApi
import com.sep.gothere.data.UserPreferencesRepository
import com.sep.gothere.data.UserRepository
import com.sep.gothere.managers.datastore.DataStoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(GoThereApi.BASE_URL_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideGoThereApi(retrofit: Retrofit): GoThereApi =
        retrofit.create(GoThereApi::class.java)

    @Provides
    @Singleton
    fun provideUserRepository(
        goThereApi: GoThereApi
    ): UserRepository = UserRepository(goThereApi)

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(
        dataStoreManager: DataStoreManager
    ): UserPreferencesRepository = UserPreferencesRepository(dataStoreManager)

    @Provides
    @Singleton
    fun provideDataStoreManager(
        @ApplicationContext appContext: Context
    ): DataStoreManager = DataStoreManager(appContext)
}