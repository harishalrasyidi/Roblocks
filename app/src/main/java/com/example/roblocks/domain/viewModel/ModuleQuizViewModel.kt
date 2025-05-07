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
                // Modul AI
                ModuleEntity(
                    id = "modul1",
                    title = "Belajar AI untuk Pemrograman",
                    description = "Modul ini membahas dasar-dasar Artificial Intelligence dan bagaimana penerapannya dalam pemrograman. Anda akan mempelajari algoritma pembelajaran mesin (machine learning), pengenalan pola, serta cara menggunakan bahasa pemrograman seperti Python untuk membangun model AI yang dapat memecahkan berbagai masalah dunia nyata.",
                    created_at = System.currentTimeMillis(),
                    updated_at = System.currentTimeMillis(),
                    link_video = "https://www.youtube.com/watch?v=tdo8vzA6y30"
                ),
                // Modul Programming
                ModuleEntity(
                    id = "modul2",
                    title = "Dasar-dasar Python untuk Pemrograman",
                    description = "Python adalah bahasa pemrograman yang sangat populer di kalangan pengembang AI dan pengembangan perangkat lunak. Di modul ini, Anda akan belajar tentang dasar-dasar Python, seperti variabel, struktur data, dan pengendalian alur program, yang merupakan fondasi untuk membangun aplikasi pemrograman yang efektif.",
                    created_at = System.currentTimeMillis(),
                    updated_at = System.currentTimeMillis(),
                    link_video = "https://www.youtube.com/watch?v=iA8lLwmtKQM"
                ),
                // Modul Visual Programming
                ModuleEntity(
                    id = "modul3",
                    title = "Belajar Visual Programming dengan Scratch",
                    description = "Visual programming memungkinkan Anda membuat program menggunakan elemen grafis, seperti blok yang dapat dipindahkan, alih-alih menulis kode teks. Modul ini menggunakan Scratch, bahasa pemrograman visual yang mudah dipahami, untuk membantu Anda memahami konsep dasar pemrograman dan logika algoritma.",
                    created_at = System.currentTimeMillis(),
                    updated_at = System.currentTimeMillis(),
                    link_video = "https://www.youtube.com/watch?v=3VZ-2ryeLV8"
                ),
                // Modul Mikrokontroler Sederhana
                ModuleEntity(
                    id = "modul4",
                    title = "Mikrokontroler dan Arduino Dasar",
                    description = "Modul ini mengajarkan dasar-dasar mikrokontroler dengan menggunakan platform Arduino. Anda akan belajar cara membuat proyek sederhana menggunakan Arduino, mulai dari menghubungkan komponen elektronik, menulis kode untuk mengontrol perangkat keras, hingga menciptakan sistem otomatisasi dasar.",
                    created_at = System.currentTimeMillis(),
                    updated_at = System.currentTimeMillis(),
                    link_video = "https://www.youtube.com/watch?v=EN0Et74bBrU"
                ),
                // Modul AI Lanjutan
                ModuleEntity(
                    id = "modul5",
                    title = "Pengenalan Deep Learning",
                    description = "Deep Learning adalah cabang dari pembelajaran mesin yang menggunakan jaringan saraf tiruan dengan banyak lapisan untuk memproses data yang sangat besar dan kompleks. Modul ini akan memperkenalkan Anda pada konsep dasar deep learning, serta bagaimana menggunakan framework seperti TensorFlow dan PyTorch untuk membangun model AI.",
                    created_at = System.currentTimeMillis(),
                    updated_at = System.currentTimeMillis(),
                    link_video = "https://www.youtube.com/watch?v=GkAj3MZVVBY"
                )
            )

            val sampleQuestions = listOf(
                // Pertanyaan untuk AI
                QuestionEntity(
                    id = "q1",
                    module_id = "modul1",
                    question_text = "Apa itu Artificial Intelligence (AI)?",
                    option_a = "Program yang bisa menulis kode",
                    option_b = "Sistem yang meniru kecerdasan manusia",
                    option_c = "Alat untuk membuat website",
                    option_d = "Pengolahan data menggunakan komputer",
                    correct_answer = "B",
                    created_at = System.currentTimeMillis(),
                    updated_at = System.currentTimeMillis()
                ),
                QuestionEntity(
                    id = "q2",
                    module_id = "modul1",
                    question_text = "Bahasa pemrograman yang paling populer untuk AI adalah?",
                    option_a = "JavaScript",
                    option_b = "Python",
                    option_c = "Java",
                    option_d = "Ruby",
                    correct_answer = "B",
                    created_at = System.currentTimeMillis(),
                    updated_at = System.currentTimeMillis()
                ),
                QuestionEntity(
                    id = "q3",
                    module_id = "modul1",
                    question_text = "Apa itu algoritma pembelajaran mesin (machine learning)?",
                    option_a = "Program yang bisa berpikir seperti manusia",
                    option_b = "Proses komputer mempelajari data dan membuat keputusan berdasarkan data tersebut",
                    option_c = "Proses mengumpulkan data untuk analisis manual",
                    option_d = "Program untuk menulis kode secara otomatis",
                    correct_answer = "B",
                    created_at = System.currentTimeMillis(),
                    updated_at = System.currentTimeMillis()
                ),
                QuestionEntity(
                    id = "q4",
                    module_id = "modul1",
                    question_text = "Model AI sering kali membutuhkan data dalam jumlah besar. Apa yang disebut dengan data ini?",
                    option_a = "Data terstruktur",
                    option_b = "Data tidak terstruktur",
                    option_c = "Data latih",
                    option_d = "Data pengujian",
                    correct_answer = "C",
                    created_at = System.currentTimeMillis(),
                    updated_at = System.currentTimeMillis()
                ),
                QuestionEntity(
                    id = "q5",
                    module_id = "modul1",
                    question_text = "Algoritma pembelajaran mesin yang digunakan untuk mengklasifikasikan data disebut?",
                    option_a = "Regresi Linear",
                    option_b = "Klasifikasi",
                    option_c = "Klasifikasi K-Nearest Neighbors",
                    option_d = "Clustering",
                    correct_answer = "B",
                    created_at = System.currentTimeMillis(),
                    updated_at = System.currentTimeMillis()
                ),
                // Pertanyaan untuk Programming
                QuestionEntity(
                    id = "q6",
                    module_id = "modul2",
                    question_text = "Apa itu fungsi dalam Python?",
                    option_a = "Struktur data untuk menyimpan nilai",
                    option_b = "Blok kode yang dapat digunakan kembali untuk melakukan tugas tertentu",
                    option_c = "Tipe data yang digunakan untuk menyimpan angka",
                    option_d = "Operator untuk perbandingan nilai",
                    correct_answer = "B",
                    created_at = System.currentTimeMillis(),
                    updated_at = System.currentTimeMillis()
                ),
                QuestionEntity(
                    id = "q7",
                    module_id = "modul2",
                    question_text = "Bagaimana cara mendeklarasikan sebuah variabel di Python?",
                    option_a = "let x = 10",
                    option_b = "var x = 10",
                    option_c = "x = 10",
                    option_d = "int x = 10",
                    correct_answer = "C",
                    created_at = System.currentTimeMillis(),
                    updated_at = System.currentTimeMillis()
                ),
                QuestionEntity(
                    id = "q8",
                    module_id = "modul2",
                    question_text = "Apa output dari kode Python berikut: print(2 + 3 * 5)?",
                    option_a = "17",
                    option_b = "25",
                    option_c = "15",
                    option_d = "5",
                    correct_answer = "C",
                    created_at = System.currentTimeMillis(),
                    updated_at = System.currentTimeMillis()
                ),
                QuestionEntity(
                    id = "q9",
                    module_id = "modul2",
                    question_text = "Apa yang dimaksud dengan list dalam Python?",
                    option_a = "Tipe data untuk menyimpan satu elemen",
                    option_b = "Tipe data untuk menyimpan koleksi elemen dalam urutan tertentu",
                    option_c = "Fungsi untuk melakukan perulangan",
                    option_d = "Tipe data untuk menyimpan bilangan bulat",
                    correct_answer = "B",
                    created_at = System.currentTimeMillis(),
                    updated_at = System.currentTimeMillis()
                ),
                QuestionEntity(
                    id = "q10",
                    module_id = "modul2",
                    question_text = "Bagaimana cara mendeklarasikan sebuah fungsi di Python?",
                    option_a = "function nama_fungsi():",
                    option_b = "def nama_fungsi()",
                    option_c = "def nama_fungsi():",
                    option_d = "create nama_fungsi()",
                    correct_answer = "C",
                    created_at = System.currentTimeMillis(),
                    updated_at = System.currentTimeMillis()
                ),
                // Pertanyaan untuk Visual Programming
                QuestionEntity(
                    id = "q11",
                    module_id = "modul3",
                    question_text = "Apa keuntungan utama dari visual programming?",
                    option_a = "Memungkinkan pemrogram untuk menulis kode dengan cepat",
                    option_b = "Mengurangi kesalahan sintaksis dengan menggunakan elemen grafis",
                    option_c = "Memungkinkan pemrograman untuk dilakukan tanpa komputer",
                    option_d = "Memungkinkan pemrograman yang hanya menggunakan teks",
                    correct_answer = "B",
                    created_at = System.currentTimeMillis(),
                    updated_at = System.currentTimeMillis()
                ),
                QuestionEntity(
                    id = "q12",
                    module_id = "modul3",
                    question_text = "Di Scratch, apa yang terjadi jika dua blok 'move' diletakkan berdampingan?",
                    option_a = "Program akan berhenti",
                    option_b = "Karakter akan bergerak dua kali lebih cepat",
                    option_c = "Karakter akan bergerak dua kali",
                    option_d = "Tidak ada yang terjadi",
                    correct_answer = "C",
                    created_at = System.currentTimeMillis(),
                    updated_at = System.currentTimeMillis()
                ),
                QuestionEntity(
                    id = "q13",
                    module_id = "modul3",
                    question_text = "Apa yang dimaksud dengan event dalam visual programming?",
                    option_a = "Bagian dari kode yang dieksekusi secara otomatis",
                    option_b = "Tindakan yang memulai blok kode tertentu",
                    option_c = "Fungsi untuk menambahkan variabel",
                    option_d = "Pernyataan kondisi dalam kode",
                    correct_answer = "B",
                    created_at = System.currentTimeMillis(),
                    updated_at = System.currentTimeMillis()
                ),
                QuestionEntity(
                    id = "q14",
                    module_id = "modul3",
                    question_text = "Di Scratch, apa yang dimaksud dengan sprite?",
                    option_a = "Karakter yang dapat bergerak dalam proyek",
                    option_b = "Bagian dari kode untuk menjalankan fungsi",
                    option_c = "Blok untuk kontrol alur program",
                    option_d = "Jenis variabel dalam Scratch",
                    correct_answer = "A",
                    created_at = System.currentTimeMillis(),
                    updated_at = System.currentTimeMillis()
                ),
                // Pertanyaan untuk Mikrokontroler
                QuestionEntity(
                    id = "q15",
                    module_id = "modul4",
                    question_text = "Apa itu mikrokontroler?",
                    option_a = "Komputer yang hanya dapat digunakan untuk pengolahan data",
                    option_b = "Perangkat keras kecil yang dapat diprogram untuk mengontrol sistem elektronik",
                    option_c = "Sistem untuk membuat program aplikasi komputer",
                    option_d = "Alat untuk merakit perangkat keras komputasi",
                    correct_answer = "B",
                    created_at = System.currentTimeMillis(),
                    updated_at = System.currentTimeMillis()
                ),
                // Pertanyaan untuk AI Lanjutan
                QuestionEntity(
                    id = "q16",
                    module_id = "modul5",
                    question_text = "Apa itu Deep Learning?",
                    option_a = "Pembelajaran mesin menggunakan jaringan saraf tiruan dengan beberapa lapisan",
                    option_b = "Pembelajaran mesin untuk pengolahan data sederhana",
                    option_c = "Proses mengenali pola dalam data menggunakan model statistik",
                    option_d = "Menggunakan data kecil untuk membuat keputusan",
                    correct_answer = "A",
                    created_at = System.currentTimeMillis(),
                    updated_at = System.currentTimeMillis()
                )
            )

            appDatabase.moduleDao().insertModules(sampleModule)
            appDatabase.questionDao().insertQuestions(sampleQuestions)
        }
    }

}
