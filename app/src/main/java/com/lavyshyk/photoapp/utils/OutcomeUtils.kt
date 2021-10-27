 package com.lavyshyk.photoapp.utils

import com.lavyshyk.photoapp.model.Outcome



import androidx.lifecycle.MutableLiveData

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable


/**
 * Extension function tu push the loading status to the observing outcome.
 */

fun <T> MutableLiveData<Outcome<T>>.loading(isLoading: Boolean) {
    this.value = Outcome.loading(isLoading)
}

/**
 * Extension function tu push a success event with data to the observing outcome.
 */

fun <T> MutableLiveData<Outcome<T>>.success(data: T) {
    with(this) {
        loading(false)
        value = Outcome.success(data)
    }
}

/**
 * Extension function to push a next event with data to the observing outcome.
 */

fun <T> MutableLiveData<Outcome<T>>.next(data: T) {
    with(this) {
        loading(false)
        value = Outcome.next(data)
    }
}

/**
 * Extension function to push failed event with an exception to the observing outcome.
 */

fun <T> MutableLiveData<Outcome<T>>.failed(t: Throwable) {
    with(this) {
        loading(false)
        value = Outcome.failure(t)
    }
}

