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
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

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
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .callTimeout(50, TimeUnit.MINUTES)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideBackendApiService(retrofit: Retrofit): BackendApiService {
        return retrofit.create(BackendApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideImageUploader(
        @ApplicationContext context: Context,
        apiService: BackendApiService
    ): ImageUploader {
        return ImageUploader(context, apiService)
    }

    @Provides
    @Singleton
    fun provideImageClassifier(@ApplicationContext context: Context): ImageClassifier {
        return ImageClassifier(context)
    }
}