package com.currency.converter.app.repository

import android.text.format.DateFormat
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.ListenableWorker.Result
import com.currency.converter.app.BuildConfig
import com.currency.converter.app.api.RatesResult
import com.currency.converter.app.api.RatesService
import com.currency.converter.app.api.Resource
import com.currency.converter.app.models.CurrencyRate
import com.currency.converter.app.models.realm.RatesRealm

import io.reactivex.Observable
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmResults
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

const val DATE_FORMAT = "yyyy-MM-dd"

@Singleton
open class RepositoryImpl @Inject constructor(val realmDb: Realm, val ratesService: com.currency.converter.app.api.RatesService) : IRepository {


    override fun getRates(ratesLiveData: MutableLiveData<com.currency.converter.app.api.Resource<List<CurrencyRate>>>) {
        val ratesResult = realmDb.where(RatesRealm::class.java).findAll()
        ratesLiveData.value = com.currency.converter.app.api.Resource.loading(ratesResult.map { it.toRate() })
        ratesResult.addChangeListener(RealmChangeListener<RealmResults<RatesRealm>> {
            ratesLiveData.value = com.currency.converter.app.api.Resource.success(ratesResult.map {
                it.toRate()
            })
        })
        ratesService.fetchRates("latest", BuildConfig.API_KEY).enqueue(object : Callback<com.currency.converter.app.api.RatesResult> {
            override fun onFailure(call: Call<com.currency.converter.app.api.RatesResult>, t: Throwable) {
                ratesLiveData.value = com.currency.converter.app.api.Resource.error(t.message ?: "", null)
            }

            override fun onResponse(call: Call<com.currency.converter.app.api.RatesResult>, response: Response<com.currency.converter.app.api.RatesResult>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    if (response.body()?.rates?.isEmpty() == true) {
                        ratesLiveData.value = com.currency.converter.app.api.Resource.error("Empty data", null)
                    } else {
                        val responseBody = response.body() as com.currency.converter.app.api.RatesResult
                        realmDb.executeTransactionAsync { realm ->
                            realm.insertOrUpdate(responseBody.rates.map {
                                val ratesRealm = RatesRealm()
                                ratesRealm.apply {
                                    baseCurrencyCode = responseBody.base
                                    currencyCode = it.key
                                    rate = it.value
                                }
//                                ratesRealm.baseCurrencyCode = responseBody.base
//                                ratesRealm.currencyCode = it.key
//                                ratesRealm.rate = it.value
                                return@map ratesRealm
                            })
                        }
                    }
                }
            }

        })
    }

    override fun getHistoricalRates(numberOfDays: Int) : LiveData<com.currency.converter.app.api.Resource<MutableList<MutableMap<String, List<CurrencyRate>>>>> {
        val ratesLiveData: MutableLiveData<com.currency.converter.app.api.Resource<MutableList<MutableMap<String, List<CurrencyRate>>>>> = MutableLiveData()
        ratesLiveData.value = com.currency.converter.app.api.Resource.loading(null)
        val datesFromNow = mutableListOf<String>()
        val today = Calendar.getInstance()
        datesFromNow.add(DateFormat.format(DATE_FORMAT, today).toString())
        for (i in 1.. 4){
            today.add(Calendar.DAY_OF_MONTH, (numberOfDays / 5) * -1)
            datesFromNow.add(DateFormat.format(DATE_FORMAT, today).toString())
        }
        datesFromNow.reverse()
        Observable.fromIterable(datesFromNow)
            .flatMap {
                ratesService.fetchRxRates(it, BuildConfig.API_KEY) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                mutableMapOf(Pair(DateFormat.format("dd MMM",SimpleDateFormat(DATE_FORMAT,Locale.getDefault()).parse(it.date)).toString(),
                    it.rates.map { pair -> CurrencyRate(pair.key,pair.value,it.base,it.date) })) }
            .toList()
            .subscribe(object: SingleObserver<MutableList<MutableMap<String,List<CurrencyRate>>>> {
                override fun onSuccess(t: MutableList<MutableMap<String, List<CurrencyRate>>>) {
                    ratesLiveData.value = com.currency.converter.app.api.Resource.success(t)
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    ratesLiveData.value = com.currency.converter.app.api.Resource.error(e.message?: "An error occured.", null)
                }
            })

        return ratesLiveData
    }

    //TODO: Use method to fetch rates at interval in background with workmanager
    @WorkerThread
    override fun getRatesInBackground(): Result {
        val response = ratesService.fetchRates("latest",BuildConfig.API_KEY).execute()
        if (response.isSuccessful && response.body()?.success == true) {
            if (response.body()?.rates?.isEmpty() == true) {
                return Result.failure()
            }
            val responseBody = response.body() as com.currency.converter.app.api.RatesResult
            realmDb.beginTransaction()
            realmDb.insertOrUpdate(responseBody.rates.map {
                val ratesRealm = RatesRealm()
                ratesRealm.baseCurrencyCode = responseBody.base
                ratesRealm.currencyCode = it.key
                ratesRealm.rate = it.value
                return@map ratesRealm
            })
            realmDb.commitTransaction()
            return Result.success()
        }
        return Result.retry()
    }
}