package com.currency.converter.app.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.ListenableWorker
import com.currency.converter.app.api.Resource
import com.currency.converter.app.models.CurrencyRate


interface IRepository {

    fun getRates(ratesLiveData: MutableLiveData<com.currency.converter.app.api.Resource<List<CurrencyRate>>>)
    fun getRatesInBackground(): ListenableWorker.Result
    fun getHistoricalRates(numberOfDays: Int) : LiveData<com.currency.converter.app.api.Resource<MutableList<MutableMap<String, List<CurrencyRate>>>>>
}