package com.lwjfork.retrofit.livedata.adapter

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

/**
 * Created by lwj on 2020/5/11
 * lwjfork@gmail.com
 */
abstract class SimpleLiveData<V, T : ResultModel<V>> : NetReqLiveData<V, T>() {

    private var successCodeBlock: ((T) -> Boolean)? = null
    private var beforeReqBlock: (() -> Unit)? = null
    private var afterReqBlock: (() -> Unit)? = null
    private var successConsumerBlock: ((T) -> Unit)? = null
    private var errorConsumerBlock: ((T) -> Unit)? = null

    open fun onDataChanged(responseBean: T) {
        if (successCodeBlock != null && successCodeBlock!!.invoke(responseBean)) {
            successConsumerBlock?.invoke(responseBean)
        } else {
            errorConsumerBlock?.invoke(responseBean)
        }
        afterReqBlock?.invoke()
    }


    open override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        beforeReqBlock?.invoke()
        super.observe(owner, observer)
    }

    open override fun observeForever(observer: Observer<in T>) {
        beforeReqBlock?.invoke()
        super.observeForever(observer)
    }

    open fun successCode(
            successCodeBlock: ((T) -> Boolean)): SimpleLiveData<V, T> {
        this.successCodeBlock = successCodeBlock
        return this
    }

    open fun beforeReq(
            beforeReqBlock: (() -> Unit)): SimpleLiveData<V, T> {
        this.beforeReqBlock = beforeReqBlock
        return this
    }


    open fun afterReq(afterReqBlock: (() -> Unit)): SimpleLiveData<V, T> {
        this.afterReqBlock = afterReqBlock
        return this
    }

    open fun successConsumer(successConsumerBlock: ((T) -> Unit)
    ): SimpleLiveData<V, T> {
        this.successConsumerBlock = successConsumerBlock
        return this
    }

    open fun errorConsumer(errorConsumerBlock: ((T) -> Unit)
    ): SimpleLiveData<V, T> {
        this.errorConsumerBlock = errorConsumerBlock
        return this
    }

    open fun subscribeForever(): Observer<T> {
        val observer: Observer<T> = Observer<T> { t ->
            onDataChanged(t)
        }
        observeForever(observer)
        return observer
    }

    open fun subscribe(owner: LifecycleOwner): Observer<T> {
        val observer: Observer<T> = Observer<T> { t -> onDataChanged(t) }
        observe(owner, observer)
        return observer
    }


}


