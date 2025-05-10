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

<b>Http 통신에도 MIME 타입 지원을 알 수 있음.(다양한 케릭터셋, 오디오, 비디오, 이미지 등 첨부 가능.)</b></br>
<b>Http 헤더의 content-type 에서 MIME 타입을 설정한다.</b>
```
1. Multipart 관련 MIME 타입
Content-Type: multipart/related – 여러 개의 연관된 데이터(예: HTML 문서와 이미지)를 하나의 메시지로 묶을 때 사용 Content-Type: application/x-fixedrecord – 고정 길이 레코드 형식 데이터를 전송할 때 사용

2. XML 기반 MIME 타입
Content-Type: text/xml – 텍스트 기반 XML 문서
Content-Type: application/xml – 애플리케이션에서 사용되는 일반적인 XML
Content-Type: application/xml-external-parsed-entity – 외부 파싱 XML 엔터티
Content-Type: application/xml-dtd – DTD(문서 유형 정의) 파일
Content-Type: application/mathml+xml – MathML 수학 표현 문서
Content-Type: application/xslt+xml – XSLT(XML 스타일시트 변환) 문서

3. 애플리케이션 관련 MIME 타입
Content-Type: application/EDI-X12 – 전자 문서 교환 형식 (RFC 1767)
Content-Type: application/EDIFACT – 국제 전자문서 표준 (RFC 1767)
Content-Type: application/javascript – 자바스크립트 코드 (RFC 4329)
Content-Type: application/octet-stream – 바이너리 데이터 (기본값, 실행파일이나 다운로드용)
Content-Type: application/ogg – Ogg 미디어 포맷 (RFC 3534)
Content-Type: application/x-shockwave-flash – Adobe Flash 파일
Content-Type: application/json – JSON 포맷 데이터 (RFC 4627)
Content-Type: application/x-www-form-urlencoded – 웹 폼 데이터 전송 방식

4. 오디오 관련 MIME 타입
Content-Type: audio/mpeg – MPEG 오디오 포맷 (예: MP3)
Content-Type: audio/x-ms-wma – Windows Media 오디오
Content-Type: audio/vnd.rn-realaudio – RealAudio 형식
```

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

