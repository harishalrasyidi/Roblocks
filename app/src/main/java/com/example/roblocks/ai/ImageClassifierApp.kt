package com.example.roblocks.ai

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.roblocks.ui.BottomNavBar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.min
import com.example.roblocks.R
import com.example.roblocks.domain.viewModel.ClassifierViewModel
import java.io.File

// Data class to represent a class
data class ImageClass(val name: String, var imageCount: Int = 0, var images: List<Bitmap> = emptyList())


@Composable
fun FlexibleClassUI(
    imageClasses: StateFlow<List<ImageClass>>,
    selectedClass: Int,
    onAddImage: (Int, Bitmap) -> Unit,
    onClassSelected: (Int) -> Unit,
    onAddNewClass: () -> Unit
) {
    val context = LocalContext.current
    val classes by imageClasses.collectAsState()

    // Create a temporary file for the camera output
    val outputDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val photoFile = remember {
        File.createTempFile("photo_${System.currentTimeMillis()}", ".jpg", outputDirectory).apply {
            deleteOnExit() // Optional: Clean up the file when the app exits
        }
    }

    val photoUri = remember {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider", // Matches the authority in AndroidManifest.xml
            photoFile
        )
    }

    // Launcher for camera
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                val currentClass = classes.indexOfFirst { it.name == "Class $selectedClass" }
                if (currentClass != -1) {
                    onAddImage(currentClass, bitmap)
                }
            } else {
                // Handle failure (e.g., user canceled)
                Log.d("Camera", "Photo capture failed or canceled")
            }
        }
    )

    // Launcher for storage picker
    val getImagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            getBitmapFromUri(context, it)?.let { bmp ->
                val currentClass = classes.indexOfFirst { it.name == "Class $selectedClass" }
                if (currentClass != -1) {
                    onAddImage(currentClass, bmp)
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            classes.forEachIndexed { index, imageClass ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = (Color(0xFFFECD46))),
                    modifier = Modifier
                        .width(160.dp)
                        .padding(4.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = imageClass.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Jumlah Gambar: ${imageClass.imageCount}")

                        Spacer(Modifier.height(20.dp))

                        Row {
                            // Camera Button
                            IconButton(
                                onClick = {
                                    takePictureLauncher.launch(photoUri)
                                    onClassSelected(index + 1)
                                },
                                modifier = Modifier
                                    .size(32.dp) // Square shape
                                    .clip(RoundedCornerShape(8.dp)) // Slightly rounded corners
                                    .background(Color.White)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.add_camera_image_classification),
                                    contentDescription = "Capture Photo",
                                    modifier = Modifier.size(24.dp),
                                    tint = Color.Unspecified
                                )
                            }

                            Spacer(modifier = Modifier.width(20.dp))

                            // Storage Picker Button
                            IconButton(
                                onClick = {
                                    getImagePicker.launch("image/*")
                                    onClassSelected(index + 1)
                                },
                                modifier = Modifier
                                    .size(32.dp) // Square shape
                                    .clip(RoundedCornerShape(8.dp)) // Slightly rounded corners
                                    .background(Color.White) // White background
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.add_image_image_classification),
                                    contentDescription = "Add Image",
                                    modifier = Modifier.size(24.dp), // Adjusted size to fit
                                    tint = Color(0xFF2FCEEA)
                                )
                            }
                        }
                        imageClass.images.firstOrNull()?.let { bitmap ->
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Sample Image",
                                modifier = Modifier
                                    .size(100.dp)
                                    .padding(top = 8.dp)
                            )
                        } ?: run {
                        }
                    }
                }
            }
        }
        IconButton(
            onClick = { onAddNewClass() },
            modifier = Modifier
                .align(Alignment.End)
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Icon(
                painter = painterResource(id = R.drawable.button_add_class),
                contentDescription = "Add New Class",
                modifier = Modifier.size(30.dp),
                tint = Color(0xFFA199FF)
            )
        }
    }
}

@Composable
fun TrainingSection(viewModel: ClassifierViewModel, context: Context) {
    val uiState by viewModel.uiState.collectAsState()
    Card(
        colors = CardDefaults.cardColors(containerColor = (Color(0xFF4A65FE))),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)) {
            Row(verticalAlignment = Alignment.CenterVertically){
                Text(
                    text = "Melatih Model",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(Modifier.width(50.dp))

                Image(
                    painter = painterResource(R.drawable.train_model_illustration),
                    contentDescription = "Train Model",
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(Modifier.height(20.dp))

            Column(
                modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
//                    Text("Latian Lagi", fontSize = 14.sp)
                    Button(
                        onClick = { viewModel.trainModel(context) },
                        enabled = !uiState.isTraining && hasEnoughTrainingData(viewModel.imageClasses),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFECD46), contentColor = Color.White),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(if (uiState.modelTrained) "Model Trained" else "Train Model")
                    }
                }
                Column {
                    Text("Advanced", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Epochs: ${uiState.epochs}", color = Color.White)
                        Slider(
                            value = uiState.epochs.toFloat(),
                            onValueChange = { /* Implement update logic */ },
                            valueRange = 1f..100f,
                            steps = 99
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Batch-Size: ${uiState.batchSize}", color = Color.White)
                        Slider(
                            value = uiState.batchSize.toFloat(),
                            onValueChange = { /* Implement update logic */ },
                            valueRange = 1f..64f,
                            steps = 63
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Learning-Rate: ${"%.3f".format(uiState.learningRate)}", color = Color.White)
                        Slider(
                            value = uiState.learningRate,
                            onValueChange = { /* Implement update logic */ },
                            valueRange = 0.001f..0.1f,
                            steps = 99
                        )
                    }
                }
            }
            if (uiState.isTraining) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
                uiState.trainingMessage?.let { Text(it) }
            }
            uiState.trainingError?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }
            if (uiState.modelTrained) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Model Trained Successfully", color = Color.Green)
                // Placeholder for Epochs vs Accuracy graph
                Box(modifier = Modifier
                    .size(200.dp)
                    .background(Color.LightGray)) {
                    Text("Epochs vs Accuracy Graph", color = Color.Black, modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@Composable
fun TestSection(viewModel: ClassifierViewModel, context: Context, onClassifyImage: (Bitmap) -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    val outputDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val photoFile = remember {
        File.createTempFile("photo_${System.currentTimeMillis()}", ".jpg", outputDirectory).apply {
            deleteOnExit()
        }
    }

    val photoUri = remember {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
    }

    // Launcher for camera
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                onClassifyImage(bitmap)
            } else {
                Log.d("Camera", "Photo capture failed or canceled")
            }
        }
    )

    // Launcher for storage picker
    val getImagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            getBitmapFromUri(context, it)?.let { bmp ->
                onClassifyImage(bmp)
            }
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2FCEEA)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)) {
            Text(
                text = "Uji Coba Model",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(
                    text = "Upload Gambar Uji Coba",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White
                )

                Spacer(Modifier.height(20.dp))
                Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Row {
                        // Camera Button
                        IconButton(
                            onClick = {
                                takePictureLauncher.launch(photoUri)
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.add_camera_image_classification),
                                contentDescription = "Capture Photo",
                                modifier = Modifier.size(24.dp),
                                tint = Color.Unspecified
                            )
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        // Storage Picker Button
                        IconButton(
                            onClick = {
                                getImagePicker.launch("image/*")
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.add_image_image_classification),
                                contentDescription = "Add Image",
                                modifier = Modifier.size(24.dp),
                                tint = Color(0xFF2FCEEA)
                            )
                        }
                    }
                }
            }
            uiState.predictionResult?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Prediction: $it", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Confidence: ${"%.2f".format(uiState.confidence * 100)}%")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageClassifierApp(navController: NavController) {
    val context = LocalContext.current
    val viewModel: ClassifierViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val imageClasses by viewModel.imageClasses.collectAsState()
    val scrollState = rememberScrollState()

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
        topBar = {
            TopAppBar(
                title = { Text("Nama Proyek") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            BottomNavBar(
                selectedIndex = selectedIndex.value,
                onItemSelected = { index ->
                    when (index) {
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
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = CardDefaults.cardColors(Color(0xFF2FCEEA))
            ) {
                Text(
                    text = "Buatlah AI Pendeteksi Object",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp),
                    color = Color.White
                )
            }

            Spacer(Modifier.height(20.dp))

            FlexibleClassUI(
                imageClasses = viewModel.imageClasses,
                selectedClass = uiState.selectedClass,
                onAddImage = { classIndex, bitmap ->
                    viewModel.addImageToClass(classIndex, bitmap)
                },
                onClassSelected = { index ->
                    viewModel.setSelectedClass(index)
                },
                onAddNewClass = {
                    viewModel.addNewClass()
                }
            )
            TrainingSection(viewModel, context)
            TestSection(
                viewModel,
                context,
                onClassifyImage = { bitmap ->
                    viewModel.classifyImage(bitmap, context)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ImageGrid(images: List<Bitmap>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val rowSize = 3
        for (i in images.indices step rowSize) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                for (j in i until min(i + rowSize, images.size)) {
                    Image(
                        bitmap = images[j].asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(4.dp)
                    )
                }
            }
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

@Composable
private fun hasEnoughTrainingData(
    imageClasses: StateFlow<List<ImageClass>>,
): Boolean {
    val classes by imageClasses.collectAsState()

    // Iterate through each class and check if any has fewer than 3 images
    return classes.all { imageClass ->
        imageClass.imageCount >= 3
    }
}