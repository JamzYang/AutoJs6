package org.autojs.autojs.ui

import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Test

class HttpTest {

    @Test
    fun test() {
        val client = OkHttpClient()
        val request: Request = Request.Builder()
            .url(Constants.BASE_URL + "/files/version/main")
            .get()
            .build()

        val response = client.newCall(request).execute()
        println()
    }
}