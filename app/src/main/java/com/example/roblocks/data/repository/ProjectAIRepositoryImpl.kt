package com.example.roblocks.data.repository

import android.content.Context
import com.example.roblocks.data.DAO.ProjectAIDao
import com.example.roblocks.data.entities.ProjectAIEntity
import com.example.roblocks.domain.repository.ProjectAIRepository
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton

class ProjectAIRepositoryImpl @Inject constructor(
    @ApplicationContext private val projectAIDao: ProjectAIDao
) : ProjectAIRepository {

    override fun getProjectNewest(): ProjectAIEntity{
        return projectAIDao.getProjectNewest()
    }

    override fun getAllProjects(): Flow<List<ProjectAIEntity>> {
        return projectAIDao.getAllProjectsNewest()
    }

    override suspend fun getProjectById(id: String): ProjectAIEntity? {
        return projectAIDao.getProjectById(id)
    }

    override suspend fun saveProject(
        name: String,
        tipe: String,
        blocklyXml: String,
        arduinoCode: String
    ): ProjectAIEntity {
        val uuid = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()
        val aiProjectFileName = "PROJECT_${uuid}.ai"
        val project = ProjectAIEntity(
            id = uuid,
            name = name,
            tipe = tipe,
            workspace_xml = blocklyXml,
            created_at = timestamp,
            updated_at = timestamp,
            file_source_proyek_AI = aiProjectFileName,
            id_siswa = "testSaja"
        )

        projectAIDao.insertProject(project)
        return project
    }

    override suspend fun exportProjectAsZip(
        context: Context,
        project: ProjectAIEntity,
        modelFile: File,
        datasetDir: File
    ): File = withContext(Dispatchers.IO){
        val projectDir = File(context.cacheDir, "projectAI_${project.id}")
        val zipFile = File(context.filesDir, "${project.name}.zip")

        projectDir.deleteRecursively()
        projectDir.mkdirs()

        val json = Gson().toJson(project)
        File(projectDir, "project.json").writeText(json)

        modelFile.copyTo(File(projectDir, "model.tflite"))

        val datasetTarget = File(projectDir, "dataset")
        datasetTarget.mkdirs()
        datasetDir.listFiles()?.forEach { file ->
            file.copyTo(File(datasetTarget, file.name))
        }

        zipFile(projectDir, zipFile)

        projectDir.deleteRecursively()

        return@withContext zipFile
    }

    fun zipFile(sourceDir: File, zipFile: File){
        ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use{
                zos ->
            sourceDir.walkTopDown().filter { it.isFile }.forEach { file ->
                val relativePath = file.relativeTo(sourceDir).path
                zos.putNextEntry(ZipEntry(relativePath))
                file.inputStream().copyTo(zos)
                zos.closeEntry()
            }
        }
    }

    override suspend fun insertProject(project: ProjectAIEntity){
        projectAIDao.insertProject(project)
    }

    override suspend fun updateProject(project: ProjectAIEntity) {
        val updatedProject = project.copy(
            updated_at = System.currentTimeMillis()
        )
        projectAIDao.updateProject(updatedProject)
    }

    override suspend fun deleteProject(project: ProjectAIEntity) {
        projectAIDao.deleteProject(project)
    }

    override suspend fun deleteProjectByID(id: String){
        projectAIDao.deleteProjectById(id)
    }
}
