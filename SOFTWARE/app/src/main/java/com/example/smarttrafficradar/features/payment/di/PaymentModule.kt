package com.example.smarttrafficradar.features.payment.di

import com.example.smarttrafficradar.features.payment.data.remote.PaymentApi
import com.example.smarttrafficradar.features.payment.data.repository.PaymentRepositoryImpl
import com.example.smarttrafficradar.features.payment.domain.repository.PaymentRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PaymentModule {

    private const val BASE_URL = "http://192.168.1.27:8080/"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providePaymentApi(retrofit: Retrofit): PaymentApi {
        return retrofit.create(PaymentApi::class.java)
    }

    @Provides
    @Singleton
    fun providePaymentRepository(api: PaymentApi): PaymentRepository {
        return PaymentRepositoryImpl(api)
    }
}