package com.lwjfork.retrofit.livedata.adapter

import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Created by lwj on 2020/5/11
 * lwjfork@gmail.com
 */
class LiveDataCallAdapterFactory<R : ResultModel<Any>, T : SimpleLiveData<Any, R>>(
        private val resultClassTyp: Class<R>,
        private val liveDataClassTyp: Class<T>
) : CallAdapter.Factory() {
    override fun get(
            returnType: Type,
            annotations: Array<Annotation>,
            retrofit: Retrofit
    ): CallAdapter<*, *>? {

        if (getRawType(returnType) != liveDataClassTyp) {
            return null
        }
        if (returnType !is ParameterizedType) {
            throw IllegalArgumentException("resource must be parameterized")
        }
        val liveDataType = getParameterUpperBound(0, returnType)
        val resultModelType = getRawType(liveDataType)
        val bodyType = getBodyType(resultClassTyp, resultModelType)
        return LiveDataCallAdapter<Any, R, T>(liveDataClassTyp, bodyType)
    }

    private fun getBodyType(resultClassTyp: Type, resultModelType: Type): ParameterizedType {
        return ParameterizedTypeImpl(resultClassTyp, arrayOf(resultModelType))
    }
}