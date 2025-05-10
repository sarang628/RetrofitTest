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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface FormUrlEncodedTestService {
    @FormUrlEncoded
    @POST("users/sarang628/repos")
    suspend fun fetchRepo(@Field("body") body: String = ""): List<Repo>
}

@Composable
fun FormUrlEncodedTest(modifier: Modifier = Modifier) {
    val coroutine = rememberCoroutineScope()
    val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://api.github.com/").build()
    val service = retrofit.create(FormUrlEncodedTestService::class.java)
    var text by remember { mutableStateOf("click! retrofit test") }

    TextButton({
        coroutine.launch {
            val result = service.fetchRepo()
            text = "true. repoSize: ${result.size}"
        }
    }) {
        Text(
            text = text,
            modifier = modifier
        )
    }
}
