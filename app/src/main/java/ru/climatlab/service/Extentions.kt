package ru.climatlab.service

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.exceptions.CompositeException
import io.reactivex.schedulers.Schedulers

/**
 * Created by tridetch on 07.04.2019. CliamtLabService
 */

class BreadcrumbException : Exception()

fun <T> Observable<T>.addSchedulers(): Observable<T> {
    return this.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
//            .dropBreadcrumb()
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T> Observable<T>.dropBreadcrumb(): Observable<T> {
    val breadcrumb = BreadcrumbException()
    return this.onErrorResumeNext { error: Throwable ->
        throw CompositeException(error, breadcrumb)
    }
}

fun <T> Single<T>.addSchedulers(): Single<T> {
    return this.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
//            .dropBreadcrumb()
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T> Single<T>.dropBreadcrumb(): Single<T> {
    val breadcrumb = BreadcrumbException()
    return this.onErrorResumeNext { error: Throwable ->
        throw CompositeException(error, breadcrumb)
    }
}

fun <T> Flowable<T>.addSchedulers(): Flowable<T> {
    return this.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
//            .dropBreadcrumb()
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T> Flowable<T>.dropBreadcrumb(): Flowable<T> {
    val breadcrumb = BreadcrumbException()
    return this.onErrorResumeNext { error: Throwable ->
        throw CompositeException(error, breadcrumb)
    }
}

fun Completable.addSchedulers(): Completable {
    return this.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
//            .dropBreadcrumb()
}

@Suppress("NOTHING_TO_INLINE")
inline fun Completable.dropBreadcrumb(): Completable {
    val breadcrumb = BreadcrumbException()
    return this.onErrorResumeNext { error: Throwable ->
        throw CompositeException(error, breadcrumb)
    }
}