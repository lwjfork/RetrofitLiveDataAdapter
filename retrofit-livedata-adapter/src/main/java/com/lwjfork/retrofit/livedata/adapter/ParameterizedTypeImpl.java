package com.lwjfork.retrofit.livedata.adapter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by lwj on 2020/5/11
 * lwjfork@gmail.com
 */
public class ParameterizedTypeImpl implements ParameterizedType {
    private final Type raw;
    private final Type[] args;

    public ParameterizedTypeImpl(Type raw, Type[] args) {
        this.raw = raw;
        this.args = args != null ? args : new Type[0];
    }

    @Override
    public Type[] getActualTypeArguments() {
        return args;
    }

    @Override
    public Type getRawType() {
        return raw;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }
}
