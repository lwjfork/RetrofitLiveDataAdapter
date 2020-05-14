package com.lwjfork.retrofit.livedata.adapter

import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

/**
 * Created by lwj on 2020/5/11
 * lwjfork@gmail.com
 */
class LiveDataCallAdapter<K, V : ResultModel<K>, T : SimpleLiveData<K, V>>(
        private val liveDataClassTyp: Class<T>,
        private val bodyType: Type) : CallAdapter<V, T> {
    override fun adapt(call: Call<V>): T {
        return liveDataClassTyp.newInstance().apply {
            setCall(call)
        }
    }

    override fun responseType(): Type {
        return bodyType
    }


}