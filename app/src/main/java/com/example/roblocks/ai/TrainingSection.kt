package com.example.roblocks.ai

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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun TrainingSection(viewModel: ClassifierViewModel, context: Context) {
    val uiState = viewModel.uiState.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Training Section", style = MaterialTheme.typography.headlineSmall)

        // Training Metrics Graph
        uiState.trainingMetrics?.let { metrics ->
            val epochs = metrics["epochs"] ?: emptyList()
            val mAP50 = metrics["mAP50"] ?: emptyList()

            if (epochs.isNotEmpty() && mAP50.isNotEmpty() && epochs.size == mAP50.size) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    factory = { ctx ->
                        LineChart(ctx).apply {
                            description.isEnabled = false
                            setTouchEnabled(true)
                            isDragEnabled = true
                            setScaleEnabled(true)
                            setPinchZoom(true)
                        }
                    },
                    update = { chart ->
                        val entries = mAP50.mapIndexed { index, value ->
                            Entry(epochs[index], value)
                        }
                        val dataSet = LineDataSet(entries, "mAP50").apply {
                            color = android.graphics.Color.BLUE
                            setDrawCircles(true)
                            setDrawValues(false)
                        }
                        chart.data = LineData(dataSet)
                        chart.invalidate()
                    }
                )
            } else {
                Text(text = "No metrics available to display.")
            }
        }

        // Training Message or Error
        uiState.trainingMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.onSurface)
        }
        uiState.trainingError?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        // Training Button (Placeholder)
        if (!uiState.isTraining && !uiState.modelTrained) {
            Button(onClick = { viewModel.trainModel(context) }) {
                Text("Start Training")
            }
        }
    }
}