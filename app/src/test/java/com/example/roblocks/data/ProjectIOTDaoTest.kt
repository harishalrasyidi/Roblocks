package com.example.roblocks.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class ProjectIOTDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var projectIOTDao: ProjectIOTDao
    
    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        projectIOTDao = database.projectIOTDao()
    }
    
    @After
    fun closeDb() {
        database.close()
    }
    
    @Test
    fun insertAndGetProject() = runBlocking {
        // Create a sample project
        val projectId = UUID.randomUUID().toString()
        val project = ProjectIOTEntity(
            id = projectId,
            id_siswa = "student1",
            name = "Test Project",
            description = "A test project",
            file_block_code = "PROJECT_${projectId}.cdk",
            file_source_code = "PROJECT_${projectId}.ino",
            created_at = System.currentTimeMillis(),
            updated_at = System.currentTimeMillis()
        )
        
        // Insert the project
        projectIOTDao.insertProject(project)
        
        // Retrieve the project and verify it matches
        val retrievedProject = projectIOTDao.getProjectById(projectId)
        assertEquals(project.id, retrievedProject?.id)
        assertEquals(project.name, retrievedProject?.name)
        assertEquals(project.description, retrievedProject?.description)
    }
    
    @Test
    fun getAllProjects() = runBlocking {
        // Create multiple projects
        val project1 = ProjectIOTEntity(
            id = UUID.randomUUID().toString(),
            id_siswa = "student1",
            name = "Project 1",
            description = "Project 1 description",
            file_block_code = "block1.cdk",
            file_source_code = "code1.ino",
            created_at = System.currentTimeMillis(),
            updated_at = System.currentTimeMillis()
        )
        
        val project2 = ProjectIOTEntity(
            id = UUID.randomUUID().toString(),
            id_siswa = "student1",
            name = "Project 2",
            description = "Project 2 description",
            file_block_code = "block2.cdk",
            file_source_code = "code2.ino",
            created_at = System.currentTimeMillis(),
            updated_at = System.currentTimeMillis() + 1000
        )
        
        // Insert the projects
        projectIOTDao.insertProject(project1)
        projectIOTDao.insertProject(project2)
        
        // Verify all projects are retrieved
        val allProjects = projectIOTDao.getAllProjects().first()
        assertEquals(2, allProjects.size)
        
        // Verify they are ordered by updated_at DESC
        assertEquals(project2.id, allProjects[0].id) // More recent update
        assertEquals(project1.id, allProjects[1].id) // Less recent update
    }
    
    @Test
    fun getProjectsByStudentId() = runBlocking {
        // Create projects for different students
        val project1 = ProjectIOTEntity(
            id = UUID.randomUUID().toString(),
            id_siswa = "student1",
            name = "Student 1 Project",
            description = "Student 1 project description",
            file_block_code = "block1.cdk",
            file_source_code = "code1.ino",
            created_at = System.currentTimeMillis(),
            updated_at = System.currentTimeMillis()
        )
        
        val project2 = ProjectIOTEntity(
            id = UUID.randomUUID().toString(),
            id_siswa = "student2",
            name = "Student 2 Project",
            description = "Student 2 project description",
            file_block_code = "block2.cdk",
            file_source_code = "code2.ino",
            created_at = System.currentTimeMillis(),
            updated_at = System.currentTimeMillis()
        )
        
        // Insert the projects
        projectIOTDao.insertProject(project1)
        projectIOTDao.insertProject(project2)
        
        // Verify filtering by student ID works
        val student1Projects = projectIOTDao.getProjectsByStudentId("student1").first()
        assertEquals(1, student1Projects.size)
        assertEquals("student1", student1Projects[0].id_siswa)
        assertEquals(project1.id, student1Projects[0].id)
        
        val student2Projects = projectIOTDao.getProjectsByStudentId("student2").first()
        assertEquals(1, student2Projects.size)
        assertEquals("student2", student2Projects[0].id_siswa)
        assertEquals(project2.id, student2Projects[0].id)
    }
    
    @Test
    fun updateProject() = runBlocking {
        // Create and insert a project
        val projectId = UUID.randomUUID().toString()
        val project = ProjectIOTEntity(
            id = projectId,
            id_siswa = "student1",
            name = "Original Name",
            description = "Original description",
            file_block_code = "PROJECT_${projectId}.cdk",
            file_source_code = "PROJECT_${projectId}.ino",
            created_at = System.currentTimeMillis(),
            updated_at = System.currentTimeMillis()
        )
        
        projectIOTDao.insertProject(project)
        
        // Update the project
        val updatedProject = project.copy(
            name = "Updated Name",
            description = "Updated description",
            updated_at = System.currentTimeMillis() + 5000
        )
        
        projectIOTDao.updateProject(updatedProject)
        
        // Retrieve and verify the update
        val retrievedProject = projectIOTDao.getProjectById(projectId)
        assertEquals("Updated Name", retrievedProject?.name)
        assertEquals("Updated description", retrievedProject?.description)
    }
    
    @Test
    fun deleteProject() = runBlocking {
        // Create and insert a project
        val projectId = UUID.randomUUID().toString()
        val project = ProjectIOTEntity(
            id = projectId,
            id_siswa = "student1",
            name = "Project to Delete",
            description = "This project will be deleted",
            file_block_code = "PROJECT_${projectId}.cdk",
            file_source_code = "PROJECT_${projectId}.ino",
            created_at = System.currentTimeMillis(),
            updated_at = System.currentTimeMillis()
        )
        
        projectIOTDao.insertProject(project)
        
        // Verify the project exists
        val retrievedProject = projectIOTDao.getProjectById(projectId)
        assertEquals(projectId, retrievedProject?.id)
        
        // Delete the project
        projectIOTDao.deleteProjectById(projectId)
        
        // Verify the project no longer exists
        val deletedProject = projectIOTDao.getProjectById(projectId)
        assertNull(deletedProject)
    }
} 