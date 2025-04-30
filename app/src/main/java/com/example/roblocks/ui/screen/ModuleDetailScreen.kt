package com.example.roblocks.ui.screen

import android.webkit.WebChromeClient
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.roblocks.domain.viewModel.ModuleQuizViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


@Composable
fun YoutubePlayer(videoUrl: String) {
    // Extract video ID from URL (works with multiple URL formats)
    val videoId = remember(videoUrl) {
        when {
            videoUrl.contains("youtube.com/watch?v=") -> {
                videoUrl.substringAfter("v=").substringBefore("&")
            }
            videoUrl.contains("youtu.be/") -> {
                videoUrl.substringAfterLast("/")
            }
            videoUrl.contains("embed/") -> {
                videoUrl.substringAfter("embed/").substringBefore("?")
            }
            else -> videoUrl // Assume it's already a video ID
        }
    }

    AndroidView(
        factory = { context ->
            YouTubePlayerView(context).apply {
                enableAutomaticInitialization = false
                addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.loadVideo(videoId, 0f)
                    }
                })
                initialize(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.loadVideo(videoId, 0f)
                    }
                }, true)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
    )
}

@Composable
fun ModuleDetailScreen(
    navController: NavController,
    moduleId: String,
    viewModel: ModuleQuizViewModel = viewModel()
) {
    val module by viewModel.getModuleById(moduleId).collectAsState(initial = null)

    if (module != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = module!!.title,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = module!!.description,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            YoutubePlayer(module!!.link_video)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    navController.navigate("quiz_screen/${moduleId}")
                }
            ) {
                Text("Start Quiz")
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
