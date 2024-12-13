package com.gazura.projectcapstone.api.retrofit

import com.gazura.projectcapstone.api.response.HasilResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiServices {

    @Multipart
    @POST("predict")
    suspend fun Predict(
        @Part file: MultipartBody.Part,
    ): HasilResponse
}