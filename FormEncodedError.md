## Form-encoded method must contain at least one @Field. 오류

API 호출을 설정한 코드이다.
```
interface FormUrlEncodedTestService {
    @FormUrlEncoded
    @POST("users/sarang628/repos")
    suspend fun fetchRepo(): List<Repo>
}
```

@FormUrlEncoded을 설정했고, fetchRepo() 호출하는 파라미터가 비어있는 상태이다.
이것 때문에 오류가 발생한다.

## 해결 방법
```
@FormUrlEncoded
@POST("users/sarang628/repos")
suspend fun fetchRepo(@Field("body") body: String = ""): List<Repo> // 빈 값이라도 필드를 하나 만들어준다.
```
빈 값을 보내는 거면 @FormUrlEncoded를 지우는 방법과 억지로라도 필드 하나를 빈 값으로 넣어서 보내는 것이다.

name-value를 인코딩해서 body로 보내는 FormUrlEncoded 방식을 빈 값으로 보낼 이유가 없어
retrofit 라이브러리에서 사전에 검사를 하는 것 같다.


### @FormUrlEncoded 에 대한 설명 
```
Denotes that the request body will use form URL encoding. Fields should be declared as parameters
and annotated with {@link Field @Field}.
<p>Requests made with this annotation will have {@code application/x-www-form-urlencoded} MIME
type. Field names and values will be UTF-8 encoded before being URI-encoded in accordance to <a
href="https://datatracker.ietf.org/doc/html/rfc3986">RFC-3986</a>.
```
- request body가 URL encoding form 을 사용한다. 
- @Field를 파라미터에 붙여야 한다.
- 이 요쳥은 application/x-www-form-urlencoded MIME type을 갖게 된다.
- 필드명과 value는 UTF-8로 인코딩 된다. 

### MIME type 은 무엇?
https://en.wikipedia.org/wiki/MIME

```
Multipurpose Internet Mail Extensions (MIME) is a standard that extends the format of email messages 
to support text in character sets other than ASCII, as well as attachments of audio, video, images, 
and application programs. Message bodies may consist of multiple parts, and header information may be 
specified in non-ASCII character sets. Email messages with MIME formatting are typically transmitted 
with standard protocols, such as the Simple Mail Transfer Protocol (SMTP), the Post Office Protocol 
(POP), and the Internet Message Access Protocol (IMAP).
```

Multiporpose Internet Main Extension의 약자.
이메일의 메세지 포멧을 확장하는 표준.
아스키가 아닌 다른 케릭터 셋을 지원할 뿐만 아니라, 오디오, 비디오, 이미지, 응용 프로그램을 첨부 할 수 있다.

Http 통신에도 MIME 타입 지원을 알 수 있음.(다양한 케릭터셋, 오디오, 비디오, 이미지 등 첨부 가능.)

### x-www-form-urlencoded 은 무엇?
https://en.wikipedia.org/wiki/Percent-encoding#The_application/x-www-form-urlencoded_type

name와 value를 설정하면 전송할 때 body에 아래와 같이 변환(UTF-8)하여 전송하는 방식이다.
```
param1=data1&param2=data2&param3=data3
```

## 그러면 Form-encoded method must contain at least one 이 오류가 발생하는 이유?
retrofit에서 사전에 체크하는 것일 뿐이다. 그냥 전송해도 상관없는데
빈 값을 보낼꺼면 body를 name-value로 인코딩 하는 FormUrlEncoded 이 방식을 사용할 이유가 없어서 그런 것 같다.
```
if (isFormEncoded && !gotField) {
    throw methodError(method, "Form-encoded method must contain at least one @Field.");
  }
```

