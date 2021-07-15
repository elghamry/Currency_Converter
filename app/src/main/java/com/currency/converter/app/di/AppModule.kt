/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.currency.converter.app.di

import com.currency.converter.app.CCApplication
import com.currency.converter.app.api.RatesService
import com.currency.converter.app.repository.IRepository
import com.currency.converter.app.repository.RepositoryImpl

import dagger.Module
import dagger.Provides
import io.realm.Realm
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(includes = [com.currency.converter.app.di.ViewModelModule::class])
class AppModule {

    @Singleton
    @Provides
    fun provideRatesService(): com.currency.converter.app.api.RatesService {
        return Retrofit.Builder()
            .baseUrl("http://data.fixer.io/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(com.currency.converter.app.api.RatesService::class.java)
    }

    @Singleton
    @Provides
    fun provideDb(app: CCApplication): Realm {
        Realm.init(app.applicationContext)
        return Realm.getDefaultInstance()
    }

    @Singleton
    @Provides
    fun provideRepository(realmDb: Realm, ratesService: RatesService): IRepository {
        return RepositoryImpl(realmDb, ratesService)
    }
}
