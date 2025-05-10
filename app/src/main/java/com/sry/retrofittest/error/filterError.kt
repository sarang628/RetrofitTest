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
import com.sry.retrofittest.GitHubService
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun FilterError(modifier: Modifier = Modifier) {
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