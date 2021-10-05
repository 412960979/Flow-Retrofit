package com.demo.net.network

import android.content.Context
import com.base.net.defaultBuildRetrofit
import com.base.net.provideService
import com.demo.net.BuildConfig
import com.demo.net.service.BaseService
import com.demo.net.service.TestService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideBaseService(retrofit: Retrofit): BaseService = provideService(retrofit)

    @Provides
    fun provideTestService(retrofit: Retrofit): TestService = provideService(retrofit)

    @Singleton
    @Provides
    fun provideRetrofit(@ApplicationContext context: Context): Retrofit{
        return defaultBuildRetrofit(
            context = context,
            baseUrl = BuildConfig.BASE_URL,
            debug = BuildConfig.DEBUG
        )
    }
}