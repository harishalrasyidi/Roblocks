package com.example.roblocks.ai

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.roblocks.domain.viewModel.ClassifierViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.components.Description
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Popup
import kotlin.math.round
import kotlin.math.roundToInt
import android.graphics.Color as AndroidColor

// In TrainingSection.kt

@Composable
fun InfoWithTooltipLearningVariabel(tooltip: String) {
    var showTooltip by remember { mutableStateOf(false) }
    Box(contentAlignment = Alignment.TopStart) {
        IconButton(onClick = { showTooltip = !showTooltip }, modifier = Modifier.size(15.dp)) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = "Info",
                tint = Color(0xFFFECD46)
            )
        }
        if (showTooltip) {
            Popup(alignment = Alignment.TopStart, onDismissRequest = { showTooltip = false }) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = Color(0xFFA199FF),
                    contentColor = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Text(
                        tooltip,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun InfoWithTooltip(tooltip: String) {
    var showTooltip by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { showTooltip = !showTooltip }) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = "Info"
            )
        }
        if (showTooltip) {
            Popup(alignment = Alignment.TopStart, onDismissRequest = { showTooltip = false }) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = Color(0xFFA199FF),
                    contentColor = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Text(
                        tooltip,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun TrainingSection(viewModel: ClassifierViewModel, context: Context) {
    val uiState = viewModel.uiState.collectAsState().value
    var advancedVisible = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Training Section", style = MaterialTheme.typography.headlineSmall)

        TextButton(onClick =  {advancedVisible.value = !advancedVisible.value}) {
            Text(text = if (advancedVisible.value) "Sembunyikan Mode Advance" else "Perlihatkan Mode Advance")
        }
        if(advancedVisible.value){
            Row(verticalAlignment = Alignment.CenterVertically) {
                InfoWithTooltipLearningVariabel(tooltip = "Jumlah iterasi pelatihan model. Nilai lebih besar = pelatihan lebih lama, bisa meningkatkan akurasi.")
                Text("Epochs: ${uiState.epochs ?: 10}")
                Slider(
                    value = (uiState.epochs ?: 10).toFloat(),
                    onValueChange = { viewModel.updateEpochs(it.toInt()) },
                    valueRange = 1f..100f,
                    steps = 99
                )
            }
            val batchSizes = listOf(8, 16, 32, 64, 128, 256)
            val currentIndex = batchSizes.indexOf(uiState.batchSize ?: 32).coerceAtLeast(0)
            var sliderPosition by remember {mutableStateOf(currentIndex.toFloat())}

            Row(verticalAlignment = Alignment.CenterVertically) {
                InfoWithTooltipLearningVariabel(tooltip = "Jumlah sampel yang diproses sebelum model diperbarui. Batch kecil = pelatihan lebih lambat, batch besar = butuh memori lebih banyak.")
                Text("Batch Size: ${batchSizes[currentIndex]}")
                Slider(
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    valueRange = 0f..(batchSizes.size - 1).toFloat(),
                    steps = batchSizes.size - 2,
                    onValueChangeFinished = {
                        val index = sliderPosition.roundToInt().coerceIn(0, batchSizes.size - 1)
                        sliderPosition = index.toFloat()
                        viewModel.updateBatchSize(batchSizes[index])
                    }
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                InfoWithTooltipLearningVariabel(tooltip = "Seberapa besar model menyesuaikan diri setiap iterasi. Nilai terlalu besar bisa membuat pelatihan tidak stabil.")
                Text("Learning Rate: ${"%.3f".format(uiState.learningRate ?: 0.001f)}")
                Slider(
                    value = uiState.learningRate ?: 0.001f,
                    onValueChange = { viewModel.updateLearningRate(it) },
                    valueRange = 0.001f..0.01f,
                    steps = round((0.01f - 0.001)).toInt()
                )
            }
        }

        // Inside TrainingSection
        uiState.trainingMetrics?.let { metrics ->
            val epochs = metrics["accuracy"]?.indices?.map { it.toFloat() } ?: emptyList()
            val accuracy = metrics["accuracy"] ?: emptyList()
            val valAccuracy = metrics["val_accuracy"] ?: emptyList()
            val loss = metrics["loss"] ?: emptyList()
            val valLoss = metrics["val_loss"] ?: emptyList()

            if (epochs.isNotEmpty() && accuracy.isNotEmpty()) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    factory = { ctx ->
                        LineChart(ctx).apply {
                            description.isEnabled = true
                            setTouchEnabled(true)
                            isDragEnabled = true
                            setScaleEnabled(true)
                            setPinchZoom(true)
                        }
                    },
                    update = { chart ->
                        val accuracyEntries = accuracy.mapIndexed { i, v -> Entry(epochs[i], v) }
                        val valAccuracyEntries = valAccuracy.mapIndexed { i, v -> Entry(epochs[i], v) }
                        val lossEntries = loss.mapIndexed { i, v -> Entry(epochs[i], v) }
                        val valLossEntries = valLoss.mapIndexed { i, v -> Entry(epochs[i], v) }

                        val dataSets = mutableListOf<ILineDataSet>()
                        dataSets.add(LineDataSet(accuracyEntries, "Accuracy").apply { color = AndroidColor.BLUE })
                        if (valAccuracy.isNotEmpty()) dataSets.add(LineDataSet(valAccuracyEntries, "Val Accuracy").apply { color = AndroidColor.GREEN })
                        if (loss.isNotEmpty()) dataSets.add(LineDataSet(lossEntries, "Loss").apply { color = AndroidColor.RED })
                        if (valLoss.isNotEmpty()) dataSets.add(LineDataSet(valLossEntries, "Val Loss").apply { color = AndroidColor.MAGENTA })

                        chart.data = LineData(dataSets)
                        chart.invalidate()
                    }
                )
                Spacer(Modifier.height(12.dp))
                Column {
                    val overallAccuracy = accuracy.lastOrNull() ?: 0f
                    val overallValAccuracy = valAccuracy.lastOrNull() ?: 0f

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Akurasi Keseluruhan: ${"%.2f".format(overallAccuracy * 100)}%")
                        InfoWithTooltip(tooltip = "Akurasi model pada data pelatihan.")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Akurasi Validasi: ${"%.2f".format(overallValAccuracy * 100)}%")
                        InfoWithTooltip(tooltip = "Akurasi model pada data validasi (data yang tidak dilatih).")
                    }
                }
            } else {
                Text(text = "Belum ada grafik yang bisa ditampilkan.")
            }
        }

        if (uiState.trainingMetrics == null) {
            Text("Latih model dulu untuk bisa melihat grafik!.")
        }

        val classes by viewModel.imageClasses.collectAsState()
        var notEnoughImage = true

        if(!uiState.isTraining && !uiState.modelTrained) {
            classes.forEach { classItem ->
                if (classItem.imageCount >= 3) {
                    notEnoughImage = false
                    viewModel.setTrainingError(null)
                } else {
                    notEnoughImage = true
                    viewModel.setTrainingError("Pastikan gambar di tiap class minimal 3 gambar")
                }
            }
        }

        // Training Button (Placeholder)
        Button(
            onClick = { viewModel.trainModel(context) },
            enabled = !uiState.isTraining && !uiState.modelTrained && uiState.trainingError == null && !notEnoughImage,
        ) {
            Text("Start Training")
        }


        // Training Message or Error
        uiState.trainingError?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
            uiState.trainingMessage = null
        }

        uiState.trainingMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}