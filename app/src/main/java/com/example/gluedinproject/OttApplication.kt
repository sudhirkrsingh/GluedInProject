package com.example.gluedinproject
import android.app.Application
import com.example.gluedinproject.data.remote.MockContentRemoteDataSource
import com.example.gluedinproject.data.repository.*
import com.example.gluedinproject.domain.usecase.*
class OttApplication : Application() {
    private val repository by lazy { DefaultContentRepository(MockContentRemoteDataSource(assets)) }
    val getHome by lazy { GetHomeContentUseCase(repository) }
    val getContent by lazy { GetContentUseCase(repository) }
    val recordEvent by lazy { RecordPlayerEventUseCase(LoggingEventRepository()) }
}
