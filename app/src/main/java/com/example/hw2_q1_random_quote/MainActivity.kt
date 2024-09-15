package com.example.hw2_q1_random_quote

import retrofit2.HttpException
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hw2_q1_random_quote.ui.theme.Hw2_q1_random_quoteTheme
import com.google.gson.annotations.SerializedName
import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Hw2_q1_random_quoteTheme {
                RandomQuote()
            }
        }
    }
}

private val RandomQuoteURL = "https://zenquotes.io/api/"

// code inspired by https://developer.android.com/codelabs/basic-android-kotlin-compose-getting-data-internet#8

interface QuoteApiService {
    @GET("random")
    suspend fun getQuote(): List<Quote>
}

object QuoteApi {
    val api: QuoteApiService by lazy {
        Retrofit.Builder()
            .baseUrl(RandomQuoteURL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
            .create(QuoteApiService::class.java)
    }
}

data class Quote(
//    @SerializedName("_id") val _id: String,
    @SerializedName("q") val quote: String,
    @SerializedName("a") val author: String,
    @SerializedName("i") val img: String,
    @SerializedName("c") val length: String,
    @SerializedName("h") val arrange: String
)

@Composable
fun RandomQuote() {
    val scope = rememberCoroutineScope()
    var randomQuote by remember { mutableStateOf("") }
    fun fetchData() {
        scope.launch(Dispatchers.IO) {
            randomQuote = try {
                QuoteApi.api.getQuote()[0].quote
            } catch (e: Exception) {
                "Fetch Failed"
            }
        }
    }
    Column(modifier= Modifier
        .fillMaxSize()
        .padding(60.dp)) {

        Text(
            text = randomQuote,
        )
        Button(
            onClick = {
                fetchData()
            }) {
            Text("Get a random quote")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Hw2_q1_random_quoteTheme {
        RandomQuote()
    }
}