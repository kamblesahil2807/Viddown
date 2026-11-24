package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DownloadOption
import com.example.model.DownloadStatus
import com.example.model.DownloadTask
import com.example.model.VideoInfo
import com.example.ui.components.DownloadProgressCard
import com.example.ui.components.HeroSection
import com.example.ui.components.VideoPreviewCard
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.PrimaryRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppTab

@Composable
fun HomeScreen(
  inputUrl: String,
  onUrlChange: (String) -> Unit,
  onPasteClick: (String) -> Unit,
  onClearClick: () -> Unit,
  onGetVideoClick: () -> Unit,
  onSelectSample: (String) -> Unit,
  isLoadingInfo: Boolean,
  validationError: String?,
  videoInfo: VideoInfo?,
  selectedOption: DownloadOption?,
  onOptionSelected: (DownloadOption) -> Unit,
  onDownloadClick: () -> Unit,
  activeTasks: List<DownloadTask>,
  onPauseResumeTask: (String) -> Unit,
  onCancelTask: (String) -> Unit,
  onNavigateTab: (AppTab) -> Unit,
  modifier: Modifier = Modifier
) {
  val downloadingTasks = activeTasks.filter {
    it.status == DownloadStatus.DOWNLOADING || it.status == DownloadStatus.PAUSED
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("home_screen"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Hero Section: URL Input & Actions
    item {
      HeroSection(
        urlInput = inputUrl,
        onUrlChange = onUrlChange,
        onPasteClick = onPasteClick,
        onClearClick = onClearClick,
        onGetVideoClick = onGetVideoClick,
        onSelectSample = onSelectSample,
        isLoading = isLoadingInfo,
        validationError = validationError
      )
    }

    // Active Downloads Section (if any downloading / paused)
    if (downloadingTasks.isNotEmpty()) {
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Active Downloads (${downloadingTasks.size})",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )
            Text(
              text = "View in Library",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = PrimaryRed,
              modifier = Modifier.clickable { onNavigateTab(AppTab.LIBRARY) }
            )
          }
          Spacer(modifier = Modifier.height(8.dp))
        }
      }

      items(downloadingTasks.size) { index ->
        val task = downloadingTasks[index]
        DownloadProgressCard(
          task = task,
          onPauseResume = { onPauseResumeTask(task.id) },
          onCancel = { onCancelTask(task.id) }
        )
      }
    }

    // Video Preview & Download Options Card (shown once video details are retrieved)
    if (videoInfo != null) {
      item {
        VideoPreviewCard(
          video = videoInfo,
          selectedOption = selectedOption,
          onOptionSelected = onOptionSelected,
          onDownloadClick = onDownloadClick
        )
      }
    }

    // Features Section (Requested in Prompt: Simple interface, Fast processing, Multiple formats, Mobile-friendly)
    item {
      FeaturesSection()
    }

    // How It Works Quick Teaser
    item {
      HowItWorksBanner(onViewFullGuide = { onNavigateTab(AppTab.HOW_IT_WORKS) })
    }

    // Bottom spacing
    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun FeaturesSection() {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
  ) {
    Text(
      text = "Engineered for Performance",
      fontSize = 18.sp,
      fontWeight = FontWeight.Bold,
      color = TextPrimary
    )
    Text(
      text = "High quality processing with strict copyright compliance",
      fontSize = 13.sp,
      color = TextSecondary,
      modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
    )

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      FeatureCard(
        title = "Fast Processing",
        subtitle = "Multi-threaded downloads with live speed and ETA indicators.",
        icon = Icons.Default.Bolt,
        modifier = Modifier.weight(1f)
      )
      FeatureCard(
        title = "Multiple Formats",
        subtitle = "MP4 up to 1080p 60fps & crystal-clear audio only extracts.",
        icon = Icons.Default.Layers,
        modifier = Modifier.weight(1f)
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      FeatureCard(
        title = "Authorized Only",
        subtitle = "Never bypasses DRM. Offers official offline guidance.",
        icon = Icons.Default.Shield,
        modifier = Modifier.weight(1f)
      )
      FeatureCard(
        title = "Mobile Optimized",
        subtitle = "Sleek touch design with local file manager and sharing.",
        icon = Icons.Default.Devices,
        modifier = Modifier.weight(1f)
      )
    }
  }
}

@Composable
private fun FeatureCard(
  title: String,
  subtitle: String,
  icon: ImageVector,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .background(DarkCard)
      .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
      .padding(14.dp)
  ) {
    Column {
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .background(Color(0xFF281318)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = PrimaryRed,
          modifier = Modifier.size(18.dp)
        )
      }
      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = subtitle,
        fontSize = 11.sp,
        color = TextSecondary,
        lineHeight = 15.sp
      )
    }
  }
}

@Composable
private fun HowItWorksBanner(onViewFullGuide: () -> Unit) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
      .clickable(onClick = onViewFullGuide),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF141419)),
    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFF3B1214), DarkBorder)))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "How It Works: 3 Simple Steps",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "Paste link • Verify license • Save authorized file",
          fontSize = 11.sp,
          color = TextSecondary
        )
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "Guide",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = PrimaryRed
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
          imageVector = Icons.Default.ArrowForward,
          contentDescription = null,
          tint = PrimaryRed,
          modifier = Modifier.size(16.dp)
        )
      }
    }
  }
}
