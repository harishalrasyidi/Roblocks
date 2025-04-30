package com.example.roblocks.domain.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roblocks.appDatabase
import com.example.roblocks.data.entities.ModuleEntity
import com.example.roblocks.data.entities.QuestionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class ModuleQuizViewModel : ViewModel() {

    val allModules: Flow<List<ModuleEntity>> = flow {
        emit(appDatabase.moduleDao().getAllModules())
    }

    fun getModuleById(id: String): Flow<ModuleEntity?> {
        return appDatabase.moduleDao().getModuleById(id)
    }

    fun getQuestionsByModule(moduleId: String): Flow<List<QuestionEntity>> = flow {
        emit(appDatabase.questionDao().getQuestionsByModule(moduleId))
    }

    fun insertSampleData() {
        viewModelScope.launch {
            val sampleModule = listOf(
                ModuleEntity(
                    id = "modul1",
                    title = "Belajar AI",
                    description = "Dasar-dasar AI untuk anak",
                    created_at = System.currentTimeMillis(),
                    updated_at = System.currentTimeMillis(),
                    link_video = "https://www.youtube.com/embed/X6Tj2PT41v8?si=LCl2bubCD-a0FiQY"
                ),
                ModuleEntity(
                    id = "modul2",
                    title = "Belajar Programming",
                    description = " Programming adalah proses membuat instruksi yang dapat dimengerti oleh komputer. Dengan programming, kita bisa membuat aplikasi, game, robot pintar, dan bahkan mengontrol alat-alat di rumah secara otomatis. Programming mengajarkan kita cara berpikir logis, menyelesaikan masalah, dan berkreasi dengan teknologi. Dalam dunia digital saat ini, kemampuan programming sangat penting karena hampir semua teknologi menggunakan kode di dalamnya. Dengan belajar programming sejak dini, kamu bisa menjadi pencipta teknologi masa depan, bukan hanya penggunanya.",
                    created_at = System.currentTimeMillis(),
                    updated_at = System.currentTimeMillis(),
                    link_video = "https://www.youtube.com/embed/X6Tj2PT41v8?si=LCl2bubCD-a0FiQY"
                )
            )
            val sampleQuestions = listOf(
                QuestionEntity(
                    id = "q1",
                    module_id = "modul1",
                    question_text = "Apa itu AI?",
                    option_a = "Artificial Intelligence",
                    option_b = "Artistik Intelek",
                    option_c = "Auto Insert",
                    option_d = "Anak Indonesia",
                    correct_answer = "A",
                    created_at = System.currentTimeMillis(),
                    updated_at = System.currentTimeMillis()
                ),
                QuestionEntity(
                    id = "q2",
                    module_id = "modul1",
                    question_text = "Artifical Intelligence disingkat menjadi apa?",
                    option_a = "aI",
                    option_b = "AI",
                    option_c = "ai",
                    option_d = "Ai",
                    correct_answer = "B",
                    created_at = System.currentTimeMillis(),
                    updated_at = System.currentTimeMillis()
                ),
                QuestionEntity(
                    id = "q3",
                    module_id = "modul2",
                    question_text = "Apa itu looping?",
                    option_a = "Murah",
                    option_b = "Mengulang aksi yang sama",
                    option_c = "Mengakhiri program",
                    option_d = "Looping",
                    correct_answer = "B",
                    created_at = System.currentTimeMillis(),
                    updated_at = System.currentTimeMillis()
                )
            )
            appDatabase.moduleDao().insertModules(sampleModule)
            appDatabase.questionDao().insertQuestions(sampleQuestions)
        }
    }
}
