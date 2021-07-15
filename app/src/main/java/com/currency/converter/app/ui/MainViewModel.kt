package com.currency.converter.app.ui

import androidx.lifecycle.*
import com.currency.converter.app.api.Resource
import com.currency.converter.app.models.CurrencyRate
import com.currency.converter.app.repository.IRepository

import javax.inject.Inject

class MainViewModel @Inject constructor(val repository: IRepository) : ViewModel() {

    private val _ratesLiveData: MutableLiveData<com.currency.converter.app.api.Resource<List<CurrencyRate>>> = MutableLiveData()
    val ratesLiveData: LiveData<com.currency.converter.app.api.Resource<List<CurrencyRate>>>
        get() = _ratesLiveData

    private val _historicalRatesLiveData: MediatorLiveData<Result> = MediatorLiveData()
    val historicalRatesLiveData: LiveData<Result>
        get() = _historicalRatesLiveData

    init {
        getHistoricalRates(30)
        getHistoricalRates(90)
    }

    fun getRates() {
        repository.getRates(_ratesLiveData)
    }

    fun getHistoricalRates(numberOfDays: Int) {
        val result = repository.getHistoricalRates(numberOfDays)
        _historicalRatesLiveData.addSource(result)
         {
//            _historicalRatesLiveData.removeSource(result)
            _historicalRatesLiveData.value = Result(numberOfDays, it)
        }
    }
}

data class Result(val numberOfDays: Int, val data: com.currency.converter.app.api.Resource<MutableList<MutableMap<String, List<CurrencyRate>>>>)