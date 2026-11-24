package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.SubcomposeAsyncImage
import com.example.model.DownloadStatus
import com.example.model.DownloadTask
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.PrimaryRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LibraryScreen(
  tasks: List<DownloadTask>,
  searchQuery: String,
  onSearchQueryChange: (String) -> Unit,
  onDeleteTask: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var previewTask by remember { mutableStateOf<DownloadTask?>(null) }

  val completedTasks = remember(tasks) {
    tasks.filter { it.status == DownloadStatus.COMPLETED }
  }

  val totalBytes = remember(completedTasks) {
    completedTasks.sumOf { it.option.sizeBytes }
  }
  val totalMb = totalBytes / (1024 * 1024)

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp, vertical = 12.dp)
      .testTag("library_screen")
  ) {
    // Header & Storage Bar
    StorageStatsCard(
      totalMb = totalMb,
      fileCount = completedTasks.size
    )

    Spacer(modifier = Modifier.height(14.dp))

    // Search Bar
    OutlinedTextField(
      value = searchQuery,
      onValueChange = onSearchQueryChange,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("library_search_input"),
      placeholder = {
        Text("Search downloaded media by title or quality...", color = TextMuted, fontSize = 13.sp)
      },
      leadingIcon = {
        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = TextSecondary, modifier = Modifier.size(18.dp))
      },
      trailingIcon = {
        if (searchQuery.isNotEmpty()) {
          IconButton(onClick = { onSearchQueryChange("") }) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary, modifier = Modifier.size(16.dp))
          }
        }
      },
      singleLine = true,
      shape = RoundedCornerShape(12.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = DarkCard,
        unfocusedContainerColor = DarkCard,
        focusedBorderColor = PrimaryRed,
        unfocusedBorderColor = DarkBorder,
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary
      )
    )

    Spacer(modifier = Modifier.height(14.dp))

    // List of Completed Downloads
    if (completedTasks.isEmpty()) {
      EmptyLibraryView(isSearching = searchQuery.isNotEmpty())
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(completedTasks.size) { index ->
          val task = completedTasks[index]
          DownloadedVideoItem(
            task = task,
            onPlay = { previewTask = task },
            onShare = {
              val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Check out this video: ${task.video.title} (${task.video.originalUrl})")
                type = "text/plain"
              }
              val shareIntent = Intent.createChooser(sendIntent, "Share video link")
              context.startActivity(shareIntent)
            },
            onDelete = { onDeleteTask(task.id) }
          )
        }
      }
    }
  }

  // Playback Preview Dialog
  previewTask?.let { task ->
    MediaPlaybackDialog(
      task = task,
      onDismiss = { previewTask = null }
    )
  }
}

@Composable
private fun StorageStatsCard(totalMb: Long, fileCount: Int) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(DarkCard)
      .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
      .padding(14.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color(0xFF271318)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Storage,
            contentDescription = null,
            tint = PrimaryRed,
            modifier = Modifier.size(18.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "Device Library Storage",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )
          Text(
            text = "$totalMb MB used • $fileCount authorized media files",
            fontSize = 11.sp,
            color = TextSecondary
          )
        }
      }

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0xFF10281D))
          .border(1.dp, Color(0xFF194A34), RoundedCornerShape(8.dp))
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = AccentEmerald,
            modifier = Modifier.size(12.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Archived",
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = AccentEmerald
          )
        }
      }
    }
  }
}

@Composable
private fun DownloadedVideoItem(
  task: DownloadTask,
  onPlay: () -> Unit,
  onShare: () -> Unit,
  onDelete: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("downloaded_item_${task.id}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = DarkCard),
    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Thumbnail with play button overlay
      Box(
        modifier = Modifier
          .size(width = 86.dp, height = 56.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0xFF181822))
          .clickable(onClick = onPlay)
      ) {
        SubcomposeAsyncImage(
          model = task.video.thumbnailUrl,
          contentDescription = task.video.title,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
        Box(
          modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.65f))
            .align(Alignment.Center),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "Play",
            tint = Color.White,
            modifier = Modifier.size(16.dp)
          )
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      // Info
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = task.video.title,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = task.video.channelName,
          fontSize = 11.sp,
          color = TextSecondary,
          maxLines = 1
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(Color(0xFF222228))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = task.option.quality,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = PrimaryRed
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = task.option.sizeFormatted,
            fontSize = 10.sp,
            color = TextMuted
          )
        }
      }

      // Action Buttons
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = onShare,
          modifier = Modifier.size(32.dp).testTag("share_video_button")
        ) {
          Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "Share",
            tint = TextSecondary,
            modifier = Modifier.size(16.dp)
          )
        }
        IconButton(
          onClick = onDelete,
          modifier = Modifier.size(32.dp).testTag("delete_video_button")
        ) {
          Icon(
            imageVector = Icons.Default.DeleteOutline,
            contentDescription = "Delete",
            tint = TextMuted,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun EmptyLibraryView(isSearching: Boolean) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .padding(32.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Box(
        modifier = Modifier
          .size(56.dp)
          .clip(CircleShape)
          .background(Color(0xFF1C1C22)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.FolderOpen,
          contentDescription = null,
          tint = TextMuted,
          modifier = Modifier.size(28.dp)
        )
      }
      Spacer(modifier = Modifier.height(14.dp))
      Text(
        text = if (isSearching) "No matches found" else "No downloads in library",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = if (isSearching) "Try a different search keyword" else "Paste a video URL on the Home tab to download authorized media.",
        fontSize = 12.sp,
        color = TextSecondary,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
      )
    }
  }
}

@Composable
private fun MediaPlaybackDialog(
  task: DownloadTask,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(20.dp))
        .border(1.dp, DarkBorder, RoundedCornerShape(20.dp))
        .testTag("playback_dialog"),
      colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
      Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Local Media Player",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary, modifier = Modifier.size(18.dp))
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Screen area
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black),
          contentAlignment = Alignment.Center
        ) {
          SubcomposeAsyncImage(
            model = task.video.thumbnailUrl,
            contentDescription = task.video.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
          Box(
            modifier = Modifier
              .size(52.dp)
              .clip(CircleShape)
              .background(PrimaryRed),
            contentAlignment = Alignment.Center
          ) {
            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White, modifier = Modifier.size(32.dp))
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = task.video.title,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )
        Text(
          text = "${task.video.channelName} • ${task.option.quality} (${task.option.sizeFormatted})",
          fontSize = 12.sp,
          color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = onDismiss,
          modifier = Modifier.fillMaxWidth().height(44.dp),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed, contentColor = Color.White)
        ) {
          Text("Close Player", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
      }
    }
  }
}
