package com.lavyshyk.photoapp.model

sealed class Outcome<T> {
    data class Progress<T>(var loading: Boolean): Outcome<T>()
    data class Success<T>(var data: T): Outcome<T>()
    data class Next<T>(var data: T): Outcome<T>()
    data class Failure<T>(var t: Throwable): Outcome<T>()

    companion object{
        fun <T> loading(isLoading: Boolean): Outcome<T> = Progress(isLoading)
        fun <T> success(data: T): Outcome<T> = Success(data)
        fun <T> failure(t: Throwable): Outcome<T> = Failure(t)
        fun <T> next(data: T): Outcome<T> = Next(data)
    }

}