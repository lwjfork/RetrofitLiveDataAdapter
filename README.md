# RetrofitLiveDataAdapter
针对 Retrofit 封装的 LiveDataCallAdapterFactory

## 依赖引入

```
 implementation 'com.github.lwjfork:retrofit-livedata-adapter:1.0.0'
```


## 使用
在库中已经定义了 请求的返回基类以及 LiveData ，在使用时对泛型进行约束

###  1. 定义 数据返回基本类型
返回类型实现 **ResultModel** 接口，并定义返回的有效数据类型

```
public class Result<T> implements ResultModel<T> {

    public int code;
    public String msg;
    public T data;
   
}
```

### 2. 定义 LiveData

```
class ResultLiveData<V> : SimpleLiveData<V, Result<V>>() {

    init {
        successCode {
            // 初始化 成功 code
            it.code == ResultCode.SUCCESS
        }
    }
   
    // 生成请求成功数据
    override fun generateSuccessResp(call: Call<Result<V>>, response: Response<Result<V>>): Result<V> {
        if (response.body() == null || response.code() == 204) {
            val result: Result<V> = Result<V>()
            result.code = 204
            result.msg = "系统异常，数据为空"
            return result
        }
        return response.body()!!
    }

    // 生成请求错误数据
    override fun generateErrorResp(call: Call<Result<V>>, throwable: Throwable): Result<V> {
        val result: Result<V> = Result<V>()
        if (throwable is SocketTimeoutException) {
            result.msg = "请求超时,请检查网络连接"
        } else if (throwable is JsonSyntaxException) {
            result.msg = "数据错误，请重试"
        } else if (throwable is HttpException) {
            if (throwable.code() >= 500) {
                if (BuildConfig.DEBUG) {
                    result.msg = "无法访问服务器"
                } else {
                    result.msg = "网络请求错误,请稍后再试"
                }
            }
        } else if (throwable is BaseException) { // 自定义异常
            result.msg = throwable.message
            result.code = throwable.code
        } else {
            result.msg = "网络请求错误,请稍后再试"
            result.code = 9999
        }
        return result
    }


}
```

### 3. 初始化 Retrofit

```
 Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(new OkHttpClient())
                  // 添加 CallAdapterFactory
                .addCallAdapterFactory(new LiveDataCallAdapterFactory<>(Result.class, ResultLiveData.class))
                .....
                .build();
                
            

```

### 4. 定义接口并使用

```
interface APIService{

  fun getData(@Url String url):ResultLiveData<Data>
  
}



val apiService:APIService = retrofit.create(APIService.class)
 
 
 
val liveData:ResultLiveData<Data> = apiService.getData("your url")


// 定义请求期间的各个回调
 liveData.beforeReq {
                // 请求开始
            }.successCode {
                it.code == 1000 // 判断是否成功
            }.successConsumer {
                // 请求成功回调
            }.errorConsumer {
                // 请求失败回调
            }.afterReq {
                // 请求结束
            }



 // 发起请求
 
// 触发请求 非生命周期感知
 liveData.subscribeForever()
 // 触发请求 生命周期感知
 // liveData.subscribe(owner)

```























