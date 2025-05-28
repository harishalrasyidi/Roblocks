package com.example.roblocks.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.roblocks.domain.viewModel.ModuleQuizViewModel

@Composable
fun QuizScreen(
    navController: NavController,
    moduleId: String,
    viewModel: ModuleQuizViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    LaunchedEffect(moduleId) {
        viewModel.fetchQuestions(moduleId)
    }
    val questions by viewModel.getQuestionsByModule(moduleId).collectAsState(initial = emptyList())

    var currentQuestionIndex by remember { mutableStateOf(0) }
    val answers = remember { mutableStateMapOf<Int, String>() }
    var showResult by remember { mutableStateOf(false) }
    var correctCount by remember { mutableStateOf(0) }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF56A2DD), // Biru terang
            Color(0xFF1E5F9E)  // Biru gelap
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundGradient)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                        start = 8.dp,
                        end = 8.dp,
                        bottom = 8.dp
                    )
            )
            {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3076B3))
                        .clickable { navController.popBackStack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "Quiz",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (questions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Loading quiz...",
                        color = Color.White
                    )
                }
            } else if (showResult) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Quiz Selesai!",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Skor: $correctCount dari ${questions.size}",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { navController.popBackStack() }) {
                        Text("Kembali")
                    }
                }
            } else {
                val question = questions[currentQuestionIndex]
                val selectedAnswer = answers[currentQuestionIndex] ?: ""

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Column {
                        // Pertanyaan
                        Text(
                            text = question.question_text,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Option
                        Text(
                            text = "a. ${question.option_a}",
                            fontSize = 14.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Text(
                            text = "b. ${question.option_b}",
                            fontSize = 14.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Text(
                            text = "c. ${question.option_c}",
                            fontSize = 14.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Text(
                            text = "d. ${question.option_d}",
                            fontSize = 14.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Row pertama untuk A dan B
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Option A
                        OptionButton(
                            option = "A",
                            isSelected = selectedAnswer == "A",
                            onClick = { answers[currentQuestionIndex] = "A" },
                            modifier = Modifier.weight(1f)
                        )

                        // Option B
                        OptionButton(
                            option = "B",
                            isSelected = selectedAnswer == "B",
                            onClick = { answers[currentQuestionIndex] = "B" },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row untuk C dan D
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Option C
                        OptionButton(
                            option = "C",
                            isSelected = selectedAnswer == "C",
                            onClick = { answers[currentQuestionIndex] = "C" },
                            modifier = Modifier.weight(1f)
                        )

                        // Option D
                        OptionButton(
                            option = "D",
                            isSelected = selectedAnswer == "D",
                            onClick = { answers[currentQuestionIndex] = "D" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Bottom nav
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Prev Button
                    if (currentQuestionIndex > 0) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.CenterStart)
                                .clip(CircleShape)
                                .background(Color.White)
                                .clickable { currentQuestionIndex-- },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowLeft,
                                contentDescription = "Previous",
                                tint = Color.Black
                            )
                        }
                    }

                    // Counter Pertanyaan
                    Text(
                        text = "${currentQuestionIndex + 1}/${questions.size}",
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    // Next button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.CenterEnd)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable {
                                if (currentQuestionIndex < questions.size - 1) {
                                    currentQuestionIndex++
                                } else {
                                    correctCount = questions.indices.count { i ->
                                        answers[i] == questions[i].correct_answer
                                    }
                                    showResult = true
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Next",
                            tint = Color.Black
                        )
                    }
                }

                // Indikator Progress
                LinearProgressIndicator(
                    progress = (currentQuestionIndex + 1).toFloat() / questions.size,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(4.dp),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )

                Text(
                    text = "Pilih jawaban yang paling tepat",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            }
        }
    }
}

@Composable
fun OptionButton(
    option: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (option) {
        "A" -> Color(0xFF3498DB)
        "B" -> Color(0xFFE74C3C)
        "C" -> Color.White
        "D" -> Color(0xFFB28C00)
        else -> Color.White
    }

    val alpha = if (isSelected) 2f else 0.5f

    Card(
        modifier = modifier
            .height(65.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor.copy(alpha = alpha)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = option,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (option == "C") Color.Black else Color.White
            )
        }
    }
}