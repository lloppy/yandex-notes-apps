package com.yuaatn.instagram_notes.data.remote

import com.yuaatn.instagram_notes.data.remote.model.ElementNoteRequest
import com.yuaatn.instagram_notes.data.remote.model.FetchNoteResponse
import com.yuaatn.instagram_notes.data.remote.model.FetchNotesResponse
import com.yuaatn.instagram_notes.data.remote.model.NoteResponse
import com.yuaatn.instagram_notes.data.remote.model.PatchNotesRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface NotesApi {

    @GET("list")
    suspend fun fetchNotes(
        @Header("X-Generate-Fails") generateFailsThreshold: Int? = null,
    ): FetchNotesResponse

    @GET("list/{id}")
    suspend fun fetchNoteByUid(
        @Path("id") noteUid: String,
        @Header("X-Generate-Fails") generateFailsThreshold: Int? = null,
    ): FetchNoteResponse

    @POST("list")
    suspend fun createNote(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body request: ElementNoteRequest,
        @Header("X-Generate-Fails") generateFailsThreshold: Int? = null,
    ): NoteResponse

    @PUT("list/{id}")
    suspend fun updateNote(
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") noteUid: String,
        @Body request: ElementNoteRequest,
        @Header("X-Generate-Fails") generateFailsThreshold: Int? = null,
    ): NoteResponse

    @DELETE("list/{id}")
    suspend fun removeNoteByUid(
        @Header("X-Last-Known-Revision") revision: Int,
        @Path("id") noteUid: String,
        @Header("X-Generate-Fails") generateFailsThreshold: Int? = null,
    ): NoteResponse


    @PATCH("list")
    suspend fun patchNotes(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body request: PatchNotesRequest,
        @Header("X-Generate-Fails") generateFailsThreshold: Int? = null,
    ): FetchNotesResponse

}