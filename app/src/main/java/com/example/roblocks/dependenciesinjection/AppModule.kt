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
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideExampleApi(): exampleAPI{
        return Retrofit.Builder()
            .baseUrl("https://example.com")
            .build()
            .create(exampleAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideRepository(api: exampleAPI): RoblocksRepository{
        return RoblocksRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase{
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
    fun provideProjectAIDao(appDatabase: AppDatabase): ProjectAIDao{
        return appDatabase.projectAIDao()
    }

    @Provides
    @Singleton
    fun provideProjectIOTDao(appDatabase: AppDatabase): ProjectIOTDao {
        return appDatabase.projectIOTDao()
    }

    @Provides
    @Singleton
    fun provideProjectAIRepository(projectAIDao: ProjectAIDao): ProjectAIRepository{
        return ProjectAIRepositoryImpl(projectAIDao)
    }

    @Provides
    @Singleton
    fun provideProjectIOTRepository(projectIOTDao: ProjectIOTDao): ProjectIOTRepository {
        return ProjectIOTRepositoryImpl(projectIOTDao)
    }
}