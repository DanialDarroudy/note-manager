package com.example.simplenote.modules.note.common.api


import com.example.simplenote.modules.note.common.model.Note
import com.example.simplenote.modules.note.common.model.Notes
import com.example.simplenote.modules.note.create.dto.request.CreateNoteRequestDto
import com.example.simplenote.modules.note.createbulk.dto.request.CreateBulkNoteRequestDto
import com.example.simplenote.modules.note.createbulk.dto.response.CreateBulkNoteResponseDto
import com.example.simplenote.modules.note.update.dto.request.UpdateNoteRequestDto
import retrofit2.Response
import retrofit2.http.*


interface ISimpleNoteApi {
    @POST("/api/notes/")
    suspend fun createNote(@Body createNoteRequestDto: CreateNoteRequestDto): Response<Note>

    @POST("/api/notes/bulk")
    suspend fun createBulkNote(@Body createNoteRequestDtos: List<CreateNoteRequestDto>): Response<List<Note>>

    @PUT("/api/notes/{id}/")
    suspend fun updateNote(@Path("id") id: Long, @Body updateNoteRequestDto: UpdateNoteRequestDto): Response<Note>

    @GET("/api/notes/{id}/")
    suspend fun retrieveNote(@Path("id") id: Long): Response<Note>

    @DELETE("/api/notes/{id}/")
    suspend fun destroyNote(@Path("id") id: Long): Response<Unit>

    @GET("/api/notes")
    suspend fun getNoteList(@Query("page") page: Int?, @Query("page_size") pageSize: Int?): Response<Notes>
}