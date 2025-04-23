package com.example.roblocks.ai

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.roblocks.domain.viewModel.ClassifierViewModel
import com.example.roblocks.ui.BottomNavBar

@Composable
fun ImageClassifierApp(navController: NavController) {
    val context = LocalContext.current
    val viewModel: ClassifierViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val modelTrainer = remember { ModelTrainer(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            viewModel.setTrainingError("Storage permission required")
        }
    }

    val manageStorageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else false
        if (!hasPermission) {
            viewModel.setTrainingError("Storage permission required")
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            viewModel.setTrainingError("Storage permission required")
        }
    }

    LaunchedEffect(Unit) {
        if (!viewModel.hasStoragePermissions(context)) {
            viewModel.requestStoragePermission(context)
        }
    }

    if (uiState.showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissPermissionDialog() },
            title = { Text("Storage Permission Needed") },
            text = { Text("This app needs access to manage storage to save and load models") },
            confirmButton = {
                Button(onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        try {
                            val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                            manageStorageLauncher.launch(intent)
                        } catch (e: Exception) {
                            val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION).apply {
                                data = Uri.parse("package:${context.packageName}")
                            }
                            manageStorageLauncher.launch(intent)
                        }
                    }
                    viewModel.dismissPermissionDialog()
                }) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                Button(onClick = {
                    viewModel.dismissPermissionDialog()
                    viewModel.setTrainingError("Storage permission required")
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (uiState.shouldRequestStoragePermission) {
        LaunchedEffect(Unit) {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            viewModel.resetPermissionRequest()
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    val class1ImagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            getBitmapFromUri(context, it)?.let { bmp ->
                viewModel.addClass1Image(bmp)
            }
        }
    }

    val class2ImagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            getBitmapFromUri(context, it)?.let { bmp ->
                viewModel.addClass2Image(bmp)
            }
        }
    }

    val testImagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            getBitmapFromUri(context, it)?.let { bmp ->
                viewModel.classifyImage(bmp, context)
            }
        }
    }

    val selectedIndex = remember { mutableStateOf(1) }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedIndex = selectedIndex.value,
                onItemSelected = {
                        index ->
                    when(index){
                        0 -> navController.navigate("main_screen")
                        1 -> {}
                        2 -> navController.navigate("robotics_screen")
                        3 -> navController.navigate("learn_screen")
                        4 -> navController.navigate("profile_screen")
                    }
                }
            )
        },
        containerColor = Color(0xFFF9F9FF)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Class 1 Section
            Text("Class 1", style = MaterialTheme.typography.headlineSmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { class1ImagePicker.launch("image/*") }) {
                    Text("Add Image")
                }
                Button(onClick = { viewModel.setSelectedClass(1) }) {
                    Text("${uiState.class1Images.size} Images")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Class 2 Section
            Text("Class 2", style = MaterialTheme.typography.headlineSmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { class2ImagePicker.launch("image/*") }) {
                    Text("Add Image")
                }
                Button(onClick = { viewModel.setSelectedClass(2) }) {
                    Text("${uiState.class2Images.size} Images")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Training Button
            Button(
                onClick = { viewModel.trainModel(context) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isTraining && hasEnoughTrainingData(uiState)
            ) {
                Text(if (uiState.modelTrained) "Model Trained" else "Train Model")
            }

            // Training Progress & Error
            if (uiState.isTraining) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
                uiState.trainingMessage?.let {
                    Text(it)
                }
            }


            uiState.trainingError?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error, // Error message in red
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (uiState.modelTrained) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Model Trained Successfully", // Pesan sukses pelatihan
                    color = Color.Green, // Warna hijau untuk pesan sukses
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ✅ Download Model Section
//            DownloadModelSection(viewModel, context)
// Sementara dinonaktifkan karena endpoint /download-model sudah terakses setelah train model selesai
            Spacer(modifier = Modifier.height(16.dp))

            // Test Button
            Button(
                onClick = { testImagePicker.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.modelTrained
            ) {
                Text("Test Image")
            }

            uiState.predictionResult?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Prediction: $it", style = MaterialTheme.typography.headlineSmall)
                Text("Confidence: ${"%.2f".format(uiState.confidence * 100)}%")
            }

            // Show selected class images
            when (uiState.selectedClass) {
                1 -> ImageGrid(images = uiState.class1Images)
                2 -> ImageGrid(images = uiState.class2Images)
                else -> Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun DownloadModelSection(viewModel: ClassifierViewModel, context: Context) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Button(
            onClick = {
                val modelUrl = "http://192.168.1.11:5000/download-model" // ganti sesuai IP backend Flask
                viewModel.downloadTrainedModel(
                    context,
                    url = modelUrl,
                    onSuccess = { file ->
                        Toast.makeText(context, "Model downloaded: ${file.name}", Toast.LENGTH_LONG).show()
                        // TODO: load model ke interpreter jika perlu
                    },
                    onError = { message ->
                        Toast.makeText(context, "Download failed: $message", Toast.LENGTH_LONG).show()
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Download Model")
        }

        if (uiState.modelTrained) {
            Text("Model is ready", color = Color.Green, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
fun ImageGrid(images: List<Bitmap>) {
    LazyColumn {
        items(images) { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .padding(4.dp)
            )
        }
    }
}

private fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    return try {
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    } catch (e: Exception) {
        null
    }
}

private fun hasEnoughTrainingData(uiState: ClassifierViewModel.ClassifierUiState): Boolean {
    return uiState.class1Images.size >= 3 && uiState.class2Images.size >= 3
}
