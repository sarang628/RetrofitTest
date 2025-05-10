package com.sry.retrofittest.error

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.sry.retrofittest.Repo
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

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
fun FilterErrorTest(modifier: Modifier = Modifier) {
    val coroutine = rememberCoroutineScope()
    val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create()) // 필터 없으면 ResponseBody만 가능
        .baseUrl("https://api.github.com/").build()
    val service = retrofit.create(GitHubService::class.java)
    var text by remember { mutableStateOf("click! retrofit test") }

    TextButton({
        coroutine.launch {
            //try {
                val result = service.responseBodyConvertTest("sarang628")
//                val result = service.dataConvertTest("sarang628")
//                val result = service.stringConvertTest("sarang628")
//                text = "true. repoSize: ${result.size}"
                text = "true. ${result}"
//            } catch (e: Exception) {
//                e.printStackTrace()
//                text = "false. ${e.message} ${e.stackTrace}"
//            }
        }
    }) {
        Text(
            text = text,
            modifier = modifier
        )
    }
}