package com.example.roblocks.dependenciesinjection

import android.content.Context
import androidx.room.Room
import com.example.roblocks.data.AppDatabase
import com.example.roblocks.data.DAO.ProjectAIDao
import com.example.roblocks.data.DAO.ProjectIOTDao
import com.example.roblocks.data.remote.exampleAPI
import com.example.roblocks.data.repository.ProjectAIRepositoryImpl
import com.example.roblocks.data.repository.ProjectIOTRepositoryImpl
import com.example.roblocks.domain.repository.RoblocksRepository
import com.example.roblocks.data.repository.RoblocksRepositoryImpl
import com.example.roblocks.domain.repository.ProjectAIRepository
import com.example.roblocks.domain.repository.ProjectIOTRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton
import com.example.roblocks.ai.ImageClassifier
import com.example.roblocks.BuildConfig
import com.example.roblocks.ai.ImageUploader
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory
import com.example.roblocks.data.remote.BackendApiService
import com.example.roblocks.data.remote.BackendApiServiceClassifier
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private val ROBLOCKS_URL = "https://roblocks.pythonanywhere.com"
    private val CLASSIFIER_URL = "http://192.168.1.4:5000/"

    @Provides
    @Singleton
    fun provideExampleApi(): exampleAPI {
        return Retrofit.Builder()
            .baseUrl("https://example.com")
            .build()
            .create(exampleAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideRepository(api: exampleAPI): RoblocksRepository {
        return RoblocksRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "app-database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideProjectAIDao(appDatabase: AppDatabase): ProjectAIDao {
        return appDatabase.projectAIDao()
    }

    @Provides
    @Singleton
    fun provideProjectIOTDao(appDatabase: AppDatabase): ProjectIOTDao {
        return appDatabase.projectIOTDao()
    }

    @Provides
    @Singleton
    fun provideProjectAIRepository(projectAIDao: ProjectAIDao): ProjectAIRepository {
        return ProjectAIRepositoryImpl(projectAIDao)
    }

    @Provides
    @Singleton
    fun provideProjectIOTRepository(projectIOTDao: ProjectIOTDao): ProjectIOTRepository {
        return ProjectIOTRepositoryImpl(projectIOTDao)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(1000, TimeUnit.SECONDS)
            .readTimeout(1000, TimeUnit.SECONDS)
            .writeTimeout(1000, TimeUnit.SECONDS)
            .callTimeout(50, TimeUnit.MINUTES)
            .build()
    }

    @Provides
    @Singleton
    @RetrofitRoblocks
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ROBLOCKS_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @RetrofitClassifier
    fun provideClassfierRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(CLASSIFIER_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideBackendApiService(
        @RetrofitRoblocks retrofit: Retrofit
    ): BackendApiService {
        return retrofit.create(BackendApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideBackendImageClassifier(
        @RetrofitClassifier retrofit: Retrofit
    ): BackendApiServiceClassifier {
        return retrofit.create(BackendApiServiceClassifier::class.java)
    }

    @Provides
    @Singleton
    fun provideImageUploader(
        @ApplicationContext context: Context,
        apiService: BackendApiServiceClassifier
    ): ImageUploader {
        return ImageUploader(context, apiService)
    }

    @Provides
    @Singleton
    fun provideImageClassifier(@ApplicationContext context: Context): ImageClassifier {
        return ImageClassifier(context)
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitClassifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitRoblocks
