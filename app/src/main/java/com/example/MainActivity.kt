package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.DownloadStatus
import com.example.ui.components.AppNavbar
import com.example.ui.components.OfficialGuideModal
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.FaqScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.HowItWorksScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.PrimaryRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.VideoDownloaderTheme
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.VideoDownloaderViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

  private val viewModel: VideoDownloaderViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      VideoDownloaderTheme(darkTheme = true) {
        VideoDownloaderApp(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun VideoDownloaderApp(viewModel: VideoDownloaderViewModel) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val allTasks by viewModel.allTasks.collectAsStateWithLifecycle()
  val filteredTasks by viewModel.filteredTasks.collectAsStateWithLifecycle()

  // Count completed tasks for the library badge
  val completedCount = allTasks.count { it.status == DownloadStatus.COMPLETED }

  // Back handling: pop to Home screen if on secondary tab
  BackHandler(enabled = uiState.currentTab != AppTab.HOME) {
    viewModel.setTab(AppTab.HOME)
  }

  // Auto-dismiss notification after 3 seconds
  LaunchedEffect(uiState.notification) {
    if (uiState.notification != null) {
      delay(3200)
      viewModel.dismissNotification()
    }
  }

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .background(DarkBackground)
      .statusBarsPadding()
      .navigationBarsPadding(),
    containerColor = DarkBackground,
    contentWindowInsets = WindowInsets(0, 0, 0, 0)
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      contentAlignment = Alignment.TopCenter
    ) {
      // Centered container with max width constraint for tablets/landscape
      Column(
        modifier = Modifier
          .fillMaxSize()
          .widthIn(max = 680.dp)
      ) {
        // App Navigation Bar
        AppNavbar(
          currentTab = uiState.currentTab,
          onTabSelected = { viewModel.setTab(it) },
          libraryCount = completedCount
        )

        // Screen Body
        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
        ) {
          when (uiState.currentTab) {
            AppTab.HOME -> {
              HomeScreen(
                inputUrl = uiState.inputUrl,
                onUrlChange = { viewModel.updateInputUrl(it) },
                onPasteClick = { viewModel.onPasteFromClipboard(it) },
                onClearClick = { viewModel.clearInputUrl() },
                onGetVideoClick = { viewModel.fetchVideo() },
                onSelectSample = { viewModel.selectSample(it) },
                isLoadingInfo = uiState.isLoadingInfo,
                validationError = uiState.validationError,
                videoInfo = uiState.videoInfo,
                selectedOption = uiState.selectedOption,
                onOptionSelected = { viewModel.selectOption(it) },
                onDownloadClick = { viewModel.initiateDownload() },
                activeTasks = allTasks,
                onPauseResumeTask = { viewModel.togglePauseResume(it) },
                onCancelTask = { viewModel.deleteTask(it) },
                onNavigateTab = { viewModel.setTab(it) }
              )
            }

            AppTab.LIBRARY -> {
              LibraryScreen(
                tasks = filteredTasks,
                searchQuery = uiState.librarySearchQuery,
                onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                onDeleteTask = { viewModel.deleteTask(it) }
              )
            }

            AppTab.HOW_IT_WORKS -> {
              HowItWorksScreen(
                onNavigateTab = { viewModel.setTab(it) }
              )
            }

            AppTab.FAQ -> {
              FaqScreen()
            }

            AppTab.ABOUT -> {
              AboutScreen()
            }
          }
        }
      }

      // Official YouTube Offline Instructions Dialog
      if (uiState.showOfficialGuideDialog) {
        OfficialGuideModal(
          onDismiss = { viewModel.dismissOfficialGuide() }
        )
      }

      // Floating Notification Toast
      AnimatedVisibility(
        visible = uiState.notification != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = Modifier
          .align(Alignment.TopCenter)
          .padding(top = 16.dp, start = 16.dp, end = 16.dp)
      ) {
        uiState.notification?.let { notif ->
          Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF1E1E24),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (notif.isError) PrimaryRed else DarkBorder),
            shadowElevation = 8.dp,
            modifier = Modifier.testTag("app_notification_toast")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = if (notif.isError) Icons.Default.ErrorOutline else Icons.Default.CheckCircle,
                contentDescription = null,
                tint = if (notif.isError) PrimaryRed else AccentEmerald,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = notif.message,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
              )
            }
          }
        }
      }
    }
  }
}
