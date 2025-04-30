package com.example.roblocks.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.roblocks.data.entities.QuestionEntity
import com.example.roblocks.domain.viewModel.ModuleQuizViewModel

@Composable
fun QuizScreen(
    navController: NavController,
    moduleId: String,
    viewModel: ModuleQuizViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val questions by viewModel.getQuestionsByModule(moduleId).collectAsState(initial = emptyList())
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf("") }
    var showResult by remember { mutableStateOf(false) }
    var correctCount by remember { mutableStateOf(0) }

    if (questions.isEmpty()) {
        Text("Loading quiz...")
        return
    }

    if (showResult) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Quiz Selesai!", style = MaterialTheme.typography.headlineSmall)
            Text("Skor: $correctCount dari ${questions.size}")
            Button(onClick = { navController.popBackStack() }) {
                Text("Kembali")
            }
        }
    } else {
        val question = questions[currentQuestionIndex]
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = question.question_text, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            listOf("A" to question.option_a, "B" to question.option_b, "C" to question.option_c, "D" to question.option_d).forEach { (key, text) ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    RadioButton(
                        selected = selectedAnswer == key,
                        onClick = { selectedAnswer = key }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "$key. $text")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (selectedAnswer == question.correct_answer) correctCount++
                    if (currentQuestionIndex < questions.size - 1) {
                        currentQuestionIndex++
                        selectedAnswer = ""
                    } else {
                        showResult = true
                    }
                },
                enabled = selectedAnswer.isNotEmpty()
            ) {
                Text("Next")
            }
        }
    }
}
