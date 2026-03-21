package com.project.fridgemate.data.remote.api

import com.project.fridgemate.data.remote.dto.ApiOkResponse
import com.project.fridgemate.data.remote.dto.ScanDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ScanApi {

    @Multipart
    @POST("fridges/me/scans")
    suspend fun uploadScan(
        @Part image: MultipartBody.Part
    ): Response<ApiOkResponse<ScanDto>>
}
