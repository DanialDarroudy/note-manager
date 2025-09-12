package com.example.simplenote.core.dependencyinjection

import android.content.Context
import androidx.room.Room
import com.example.simplenote.core.database.business.AppDatabase
import com.example.simplenote.core.network.abstraction.INetworkService
import com.example.simplenote.core.network.business.AuthInterceptor
import com.example.simplenote.core.network.business.NetworkService
import com.example.simplenote.core.network.constant.ConstantProvider
import com.example.simplenote.modules.authentication.common.api.IAuthenticationApi
import com.example.simplenote.modules.authentication.token.createtoken.abstraction.ITokenCreator
import com.example.simplenote.modules.authentication.token.createtoken.business.TokenCreator
import com.example.simplenote.modules.authentication.user.createuser.abstraction.IUserCreator
import com.example.simplenote.modules.authentication.user.createuser.business.UserCreator
import com.example.simplenote.modules.authentication.token.renewtoken.abstraction.ITokenRenewer
import com.example.simplenote.modules.authentication.token.renewtoken.business.TokenRenewer
import com.example.simplenote.modules.authentication.user.getuser.abstraction.IUserGetter
import com.example.simplenote.modules.authentication.user.getuser.business.UserGetter
import com.example.simplenote.modules.note.common.api.ISimpleNoteApi
import com.example.simplenote.modules.note.common.synchronization.master.abstraction.IDatabaseSynchronizer
import com.example.simplenote.modules.note.common.database.abstraction.INoteDataAccessObject
import com.example.simplenote.modules.note.common.synchronization.master.business.DatabaseSynchronizer
import com.example.simplenote.modules.note.common.synchronization.pull.abstraction.IPuller
import com.example.simplenote.modules.note.common.synchronization.pull.business.Puller
import com.example.simplenote.modules.note.common.synchronization.push.create.abstraction.IUnsyncedNotesCreator
import com.example.simplenote.modules.note.common.synchronization.push.create.business.UnsyncedNotesCreator
import com.example.simplenote.modules.note.common.synchronization.push.delete.abstraction.IUnsyncedNotesDeleter
import com.example.simplenote.modules.note.common.synchronization.push.delete.business.UnsyncedNotesDeleter
import com.example.simplenote.modules.note.common.synchronization.push.master.abstraction.IPusher
import com.example.simplenote.modules.note.common.synchronization.push.master.business.Pusher
import com.example.simplenote.modules.note.common.synchronization.push.update.abstraction.IUnsyncedNotesUpdater
import com.example.simplenote.modules.note.common.synchronization.push.update.business.UnsyncedNotesUpdater
import com.example.simplenote.modules.note.create.abstraction.INoteCreator
import com.example.simplenote.modules.note.create.business.NoteCreator
import com.example.simplenote.modules.note.createbulk.abstraction.IBulkNoteCreator
import com.example.simplenote.modules.note.createbulk.business.BulkNoteCreator
import com.example.simplenote.modules.note.destroy.abstraction.INoteDestroyer
import com.example.simplenote.modules.note.destroy.business.NoteDestroyer
import com.example.simplenote.modules.note.getlist.abstraction.INoteListGetter
import com.example.simplenote.modules.note.getlist.business.NoteListGetter
import com.example.simplenote.modules.note.retrieve.abstraction.INoteRetriever
import com.example.simplenote.modules.note.retrieve.business.NoteRetriever
import com.example.simplenote.modules.note.update.abstraction.INoteUpdater
import com.example.simplenote.modules.note.update.business.NoteUpdater
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.getValue

object DependencyProvider {
    private lateinit var applicationContext: Context

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

    private val appDatabase: AppDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "notes_database"
        ).build()
    }

    val noteDataAccessObject: INoteDataAccessObject by lazy {
        appDatabase.noteDataAccessObject()
    }

    val simpleNoteApi: ISimpleNoteApi by lazy {
        retrofit.create(ISimpleNoteApi::class.java)
    }
    val authenticationApi: IAuthenticationApi by lazy {
        retrofit.create(IAuthenticationApi::class.java)
    }
    private val retrofit: Retrofit by lazy {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(AuthInterceptor { ConstantProvider.accessToken })
            .build()

        Retrofit.Builder()
            .baseUrl(ConstantProvider.baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    private val gson: Gson by lazy {
        GsonBuilder()
            .setPrettyPrinting()
            .create()
    }

    val noteCreator: INoteCreator by lazy {
        NoteCreator()
    }
    val bulkNoteCreator: IBulkNoteCreator by lazy {
        BulkNoteCreator()
    }

    val noteDestroyer: INoteDestroyer by lazy {
        NoteDestroyer()
    }

    val noteListGetter: INoteListGetter by lazy {
        NoteListGetter()
    }

    val noteRetriever: INoteRetriever by lazy {
        NoteRetriever()
    }

    val noteUpdater: INoteUpdater by lazy {
        NoteUpdater()
    }

    val networkService: INetworkService by lazy {
        NetworkService()
    }

    val userCreator: IUserCreator by lazy {
        UserCreator()
    }
    val userGetter: IUserGetter by lazy {
        UserGetter()
    }
    val tokenCreator: ITokenCreator by lazy {
        TokenCreator()
    }
    val tokenRenewer: ITokenRenewer by lazy {
        TokenRenewer()
    }
    val databaseSynchronizer: IDatabaseSynchronizer by lazy {
        DatabaseSynchronizer()
    }
    val unsyncedNotesDeleter: IUnsyncedNotesDeleter by lazy {
        UnsyncedNotesDeleter()
    }
    val unsyncedNotesUpdater: IUnsyncedNotesUpdater by lazy {
        UnsyncedNotesUpdater()
    }
    val unsyncedNotesCreator: IUnsyncedNotesCreator by lazy {
        UnsyncedNotesCreator()
    }
    val pusher: IPusher by lazy {
        Pusher()
    }
    val puller: IPuller by lazy {
        Puller()
    }
}