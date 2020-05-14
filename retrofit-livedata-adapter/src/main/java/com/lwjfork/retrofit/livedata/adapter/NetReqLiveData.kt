package com.lwjfork.retrofit.livedata.adapter

import androidx.annotation.WorkerThread
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by lwj on 2020/5/11
 * lwjfork@gmail.com
 */
abstract class NetReqLiveData<V, T : ResultModel<V>> : MutableLiveData<T>() {

    private val started = AtomicBoolean(false)

    private var call: Call<T>? = null

    internal fun setCall(call: Call<T>) {
        this.call = call
    }

    /**
     * 生成错误数据
     */
    @WorkerThread
    abstract fun generateErrorResp(call: Call<T>, throwable: Throwable): T

    /**
     * 生成成功数据
     */
    @WorkerThread
    abstract fun generateSuccessResp(call: Call<T>, response: Response<T>): T

    private fun realCall() {
        if (started.compareAndSet(false, true)) {//确保执行一次
            call?.enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    if (!call.isCanceled) {
                        postValue(generateErrorResp(call, t))
                    }
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    if (!call.isCanceled) {
                        postValue(generateSuccessResp(call, response))
                    }
                }
            })
        }
    }

    override fun onActive() {
        super.onActive()
        realCall()
    }

    override fun onInactive() {
        super.onInactive()
        if (!hasObservers()) { //
            call?.cancel()
        }
    }
}


