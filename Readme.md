# Unable to create converter for class 오류

retrofit으로 만든 Service에서 API를 호출 시 다음 에러가 발생
```
Process: com.sry.retrofittest, PID: 19207
java.lang.IllegalArgumentException: Unable to create converter for class java.util.List<com.sry.retrofittest.Repo>
   for method GitHubService.stringConvertTest
at retrofit2.Utils.methodError(Utils.java:56)
at retrofit2.HttpServiceMethod.createResponseConverter(HttpServiceMethod.java:139)
at retrofit2.HttpServiceMethod.parseAnnotations(HttpServiceMethod.java:97)
at retrofit2.ServiceMethod.parseAnnotations(ServiceMethod.java:39)
at retrofit2.Retrofit.loadServiceMethod(Retrofit.java:235)
at retrofit2.Retrofit$1.invoke(Retrofit.java:177)
at java.lang.reflect.Proxy.invoke(Proxy.java:1006)
at $Proxy2.stringConvertTest(Unknown Source)
```

retrofit 생성 시 addConverterFactory를 해주지 않아서 발생한 오류였다.
다음 같이 추가하면 된다.
```
val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create()) // 필터 없으면 ResponseBody만 가능
    .baseUrl("https://api.github.com/").build()
```
GsonConverterFactory가 없다면 다음 의존성을 추가.
```
implementation ("com.squareup.retrofit2:converter-gson:2.11.0")
```

오류가 나는 부분을 디버깅 해보았다.
```
Converter<ResponseBody, ResponseT> responseConverter =
        createResponseConverter(retrofit, method, responseType);
```
retrofit을 api 호출 시 responseConverter라는 것을 만든다.

```
    @GET("users/{user}/repos")
        suspend fun dataConvertTest(@Path("user") user: String?): List<Repo>
``` 
함수에 return 값인 List<Repo>를 추출하여 변환이 가능한지 체크하는 곳 같다.

responseConverter에 break point를 걸고 테스트해 보았다.

ResponseBody를 응답으로 했을 때
```
result = {BuiltInConverters$BufferingResponseBodyConverter@31346} 
 No fields to display
```

GsonConverterFactory를 추가하고 List<Repo>를 응답으로 했을 때
```
result = {GsonResponseBodyConverter@31527} 
 adapter = {CollectionTypeAdapterFactory$Adapter@31674} 
 gson = {Gson@31675} "{serializeNulls:false,factories:[Factory[typeHierarchy=com.google.gson.JsonElement,adapter=com.google.gson.internal.bind.TypeAdapters$28@eb9b3c3], com.google.gson.internal.bind.ObjectTypeAdapter$1@5a92440, com.google.gson.internal.Excluder@b901f79, Factory[type=java.lang.String,adapter=com.google.gson.internal.bind.TypeAdapters$15@68a74be], Factory[type=java.lang.Integer+int,adapter=com.google.gson.internal.bind.TypeAdapters$7@179c91f], Factory[type=java.lang.Boolean+boolean,adapter=com.google.gson.internal.bind.TypeAdapters$3@91c446c], Factory[type=java.lang.Byte+byte,adapter=com.google.gson.internal.bind.TypeAdapters$5@56a3a35], Factory[type=java.lang.Short+short,adapter=com.google.gson.internal.bind.TypeAdapters$6@5f51eca], Factory[type=java.lang.Long+long,adapter=com.google.gson.internal.bind.TypeAdapters$11@2fbd83b], Factory[type=java.lang.Double+double,adapter=com.google.gson.Gson$1@9bf3b58], Factory[type=java.lang.Float+float,adapter=com.google.gson.Gson$2@89144b1], com.google.g"
```

## 뭐가 문제였는가?
Retrofit 사이트의 가장 위에 있는 예제 코드만 바로 복붙해서 테스트를 했었다.
조금만 더 내려가서 아래를 보면 다음 설명이 있었다.  

```
Converters
By default, Retrofit can only deserialize HTTP bodies into OkHttp's ResponseBody type and it can only accept its RequestBody type for @Body.

Converters can be added to support other types. Sibling modules adapt popular serialization libraries for your convenience.
```
Retrofit은 http의 통신에서 받은 response body를 Okhttp의 ResponseBody타입 으로만 변환 가능. 하지만 Converter를 추가하면 원하는 타입으로 변환이 가능.

결론
Unable to create converter for class .. 오류가 난다면 http의 body를 service에서 설정한 값으로 변환하지 못하는 것 이므로 알맞은 Converter나 응답 값을 설정 하기.  