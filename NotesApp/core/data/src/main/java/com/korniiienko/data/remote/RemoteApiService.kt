package com.korniiienko.data.remote

import com.korniiienko.data.remote.model.GetNoteResponse
import com.korniiienko.data.remote.model.GetNotesResponse
import com.korniiienko.data.remote.model.PatchNotesRequest
import com.korniiienko.data.remote.model.SingleNoteRequest
import com.korniiienko.data.remote.model.SingleNoteResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RemoteApiService {

    @GET("list")
    suspend fun getNotes(
        @Header("X-Generate-Fails") generateFailsThreshold: Int? = null,
    ): GetNotesResponse

    @PATCH("list")
    suspend fun patchNotes(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body request: PatchNotesRequest,
        @Header("X-Generate-Fails") generateFailsThreshold: Int? = null,
    ): GetNotesResponse

    @GET("list/{id}")
    suspend fun getNote(
        @Path("id") noteUid: String,
        @Header("X-Generate-Fails") generateFailsThreshold: Int? = null,
    ): GetNoteResponse

    @POST("list")
    suspend fun addNote(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body request: SingleNoteRequest,
        @Header("X-Generate-Fails") generateFailsThreshold: Int? = null,
    ): SingleNoteResponse

    @PUT("list/{id}")
    suspend fun updateNote(
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") noteUid: String,
        @Body request: SingleNoteRequest,
        @Header("X-Generate-Fails") generateFailsThreshold: Int? = null,
    ): SingleNoteResponse

    @DELETE("list/{id}")
    suspend fun deleteNote(
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") noteUid: String,
        @Header("X-Generate-Fails") generateFailsThreshold: Int? = null,
    ): SingleNoteResponse
}