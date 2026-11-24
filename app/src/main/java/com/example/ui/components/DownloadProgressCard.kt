package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun DownloadProgressCard(
  task: DownloadTask,
  onPauseResume: () -> Unit,
  onCancel: () -> Unit,
  modifier: Modifier = Modifier
) {
  val animatedProgress by animateFloatAsState(
    targetValue = task.progress,
    label = "progress"
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .testTag("download_progress_card_${task.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = DarkCard),
    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(DarkBorder, Color(0xFF1E1E22))))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // Header: Video Title & Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = task.video.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "${task.option.quality} (${task.option.format.name}) • ${task.option.sizeFormatted}",
            fontSize = 11.sp,
            color = TextSecondary
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          // Pause / Resume
          IconButton(
            onClick = onPauseResume,
            modifier = Modifier
              .size(32.dp)
              .testTag("pause_resume_button")
          ) {
            Icon(
              imageVector = if (task.status == DownloadStatus.DOWNLOADING) Icons.Default.Pause else Icons.Default.PlayArrow,
              contentDescription = if (task.status == DownloadStatus.DOWNLOADING) "Pause" else "Resume",
              tint = PrimaryRed,
              modifier = Modifier.size(18.dp)
            )
          }

          Spacer(modifier = Modifier.width(4.dp))

          // Cancel
          IconButton(
            onClick = onCancel,
            modifier = Modifier
              .size(32.dp)
              .testTag("cancel_download_button")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Cancel",
              tint = TextMuted,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Custom Glowing Progress Bar
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(8.dp)
          .clip(CircleShape)
          .background(Color(0xFF222228))
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth(animatedProgress)
            .fillMaxHeight()
            .clip(CircleShape)
            .background(
              Brush.horizontalGradient(
                listOf(
                  PrimaryRed,
                  Color(0xFFF43F5E),
                  Color(0xFFFB7185)
                )
              )
            )
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Stats Row: Percentage, Speed, ETA
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "${(animatedProgress * 100).toInt()}% completed",
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = PrimaryRed
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Speed,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(12.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = task.downloadSpeed,
            fontSize = 11.sp,
            color = TextSecondary
          )

          if (task.status == DownloadStatus.DOWNLOADING && task.etaSeconds > 0) {
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
              imageVector = Icons.Default.Timer,
              contentDescription = null,
              tint = TextMuted,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "${task.etaSeconds}s left",
              fontSize = 11.sp,
              color = TextSecondary
            )
          }
        }
      }
    }
  }
}
