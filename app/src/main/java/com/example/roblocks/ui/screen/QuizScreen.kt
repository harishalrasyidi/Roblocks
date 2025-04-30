package com.example.roblocks.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.roblocks.domain.viewModel.ModuleQuizViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    navController: NavController,
    moduleId: String,
    viewModel: ModuleQuizViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val questions by viewModel.getQuestionsByModule(moduleId).collectAsState(initial = emptyList())
    var currentQuestionIndex by remember { mutableStateOf(0) }
    val answers = remember { mutableStateMapOf<Int, String>() }
    var showResult by remember { mutableStateOf(false) }
    var correctCount by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    actionIconContentColor = Color.Black
                )
            )
        }
    ) { innerPadding ->
        if (questions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading quiz...")
            }
            return@Scaffold
        }

        if (showResult) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Quiz Selesai!", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Skor: $correctCount dari ${questions.size}", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { navController.popBackStack() }) {
                    Text("Kembali")
                }
            }
        } else {
            val question = questions[currentQuestionIndex]
            val selectedAnswer = answers[currentQuestionIndex] ?: ""

            Column(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)) {

                Text(
                    text = "Soal ${currentQuestionIndex + 1} dari ${questions.size}",
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = question.question_text,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                listOf("A" to question.option_a, "B" to question.option_b, "C" to question.option_c, "D" to question.option_d)
                    .forEach { (key, text) ->
                        val isSelected = selectedAnswer == key
                        val backgroundColor = if (isSelected) Color(0xFFE3F2FD) else Color.White
                        val borderColor = if (isSelected) Color(0xFF2196F3) else Color.Gray

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .background(color = backgroundColor, shape = RoundedCornerShape(12.dp))
                                .border(width = 2.dp, color = borderColor, shape = RoundedCornerShape(12.dp))
                                .clickable { answers[currentQuestionIndex] = key }
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "$key. $text",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black
                            )
                        }
                    }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (currentQuestionIndex > 0) {
                        OutlinedButton(onClick = {
                            currentQuestionIndex--
                        }) {
                            Text("Sebelumnya")
                        }
                    }

                    Button(
                        onClick = {
                            if (currentQuestionIndex < questions.size - 1) {
                                currentQuestionIndex++
                            } else {
                                correctCount = questions.indices.count { i ->
                                    answers[i] == questions[i].correct_answer
                                }
                                showResult = true
                            }
                        },
                        enabled = selectedAnswer.isNotEmpty()
                    ) {
                        Text(if (currentQuestionIndex == questions.size - 1) "Selesai" else "Berikutnya")
                    }
                }
            }
        }
    }
}
