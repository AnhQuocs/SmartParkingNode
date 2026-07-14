package com.example.smarttrafficradar.features.payment.di

import com.example.smarttrafficradar.features.payment.data.remote.PaymentApi
import com.example.smarttrafficradar.features.payment.data.repository.PaymentRepositoryImpl
import com.example.smarttrafficradar.features.payment.domain.repository.PaymentRepository
import com.google.firebase.firestore.FirebaseFirestore
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

    private const val BASE_URL = "https://0ae6-183-80-51-51.ngrok-free.app/"
//    private const val BASE_URL = "http://192.168.0.103:8080"

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
    fun providePaymentRepository(
        api: PaymentApi,
        firestore: FirebaseFirestore
    ): PaymentRepository {
        return PaymentRepositoryImpl(api, firestore)
    }
}