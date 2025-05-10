package com.sry.retrofittest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sry.retrofittest.error.FilterError
import com.sry.retrofittest.ui.theme.RetrofitTestTheme
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * 인코딩 오류를 파악하기 위한 테스트 프로젝트
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RetrofitTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(Modifier.padding(innerPadding)){
                        FilterError() // addConverterFactory 테스트
                    }
                }
            }
        }
    }
}

data class Repo( val id: Int )

interface GitHubService {
    @GET("users/{user}/repos")
    suspend fun dataConvertTest(@Path("user") user: String?): List<Repo>

    @GET("users/{user}/repos")
    suspend fun responseBodyConvertTest(@Path("user") user: String?): ResponseBody

    @GET("users/{user}/repos")
    suspend fun stringConvertTest(@Path("user") user: String?): String

    @FormUrlEncoded
    @POST("users/sarang628/repos")
    suspend fun listRepos1(): List<Repo>

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val coroutine = rememberCoroutineScope()
    val retrofit = Retrofit.Builder()
        //.addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://api.github.com/").build()
    val service = retrofit.create(GitHubService::class.java)
    var text by remember { mutableStateOf("click! retrofit test") }

    TextButton({
        coroutine.launch {
            //try {
            //val result = service.listRepos("sarang628")
            val result = service.dataConvertTest("sarang628")
            text = "true. repoSize: ${result.size}"
            //} catch (e: Exception) {
            //    text = "false. ${e.message}"
            //}
        }
    }) {
        Text(
            text = text,
            modifier = modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RetrofitTestTheme {
        Greeting("Android")
    }
}