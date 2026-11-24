package com.example.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.model.DownloadOption
import com.example.model.VideoFormat
import com.example.model.VideoInfo
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.PrimaryRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun VideoPreviewCard(
  video: VideoInfo,
  selectedOption: DownloadOption?,
  onOptionSelected: (DownloadOption) -> Unit,
  onDownloadClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTabType by remember { mutableIntStateOf(0) } // 0 = Video, 1 = Audio

  val videoOptions = remember(video) {
    video.availableOptions.filterNot { it.isAudioOnly }
  }
  val audioOptions = remember(video) {
    video.availableOptions.filter { it.isAudioOnly }
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .testTag("video_preview_card"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = DarkCard),
    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.verticalGradient(listOf(DarkBorder, Color(0xFF1E1E22))))
  ) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
      // Thumbnail Box with Duration Badge and Shorts Pill
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(16f / 9f)
          .clip(RoundedCornerShape(14.dp))
          .background(Color(0xFF121216))
      ) {
        SubcomposeAsyncImage(
          model = video.thumbnailUrl,
          contentDescription = video.title,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize(),
          loading = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
              CircularProgressIndicator(color = PrimaryRed, modifier = Modifier.size(32.dp), strokeWidth = 2.dp)
            }
          },
          error = {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(listOf(Color(0xFF1A1A22), Color(0xFF28181D)))),
              contentAlignment = Alignment.Center
            ) {
              Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = PrimaryRed, modifier = Modifier.size(48.dp))
            }
          }
        )

        // Duration Badge (bottom-right)
        Box(
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(10.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color.Black.copy(alpha = 0.85f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = video.duration,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }

        // Shorts Pill (top-left) if applicable
        if (video.isShorts) {
          Box(
            modifier = Modifier
              .align(Alignment.TopStart)
              .padding(10.dp)
              .clip(RoundedCornerShape(6.dp))
              .background(PrimaryRed)
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = "SHORTS",
              color = Color.White,
              fontSize = 11.sp,
              fontWeight = FontWeight.Black
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Video Title
      Text(
        text = video.title,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        lineHeight = 22.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Channel & Stats Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = video.channelName,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary
          )
          Spacer(modifier = Modifier.width(4.dp))
          Icon(
            imageVector = Icons.Default.Verified,
            contentDescription = "Verified",
            tint = PrimaryRed,
            modifier = Modifier.size(15.dp)
          )
        }

        Text(
          text = "${video.views} • ${video.uploadDate}",
          fontSize = 12.sp,
          color = TextMuted
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // License / Compliance Pill
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(if (video.isAuthorizedDownload) Color(0xFF0F291E) else Color(0xFF2E2010))
          .border(
            1.dp,
            if (video.isAuthorizedDownload) Color(0xFF1B4D36) else Color(0xFF5C3C15),
            RoundedCornerShape(10.dp)
          )
          .padding(horizontal = 10.dp, vertical = 6.dp)
      ) {
        Icon(
          imageVector = if (video.isAuthorizedDownload) Icons.Default.CheckCircle else Icons.Default.Lock,
          contentDescription = null,
          tint = if (video.isAuthorizedDownload) AccentEmerald else AccentAmber,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = video.licenseType,
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium,
          color = if (video.isAuthorizedDownload) AccentEmerald else AccentAmber,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Download Options Section Header
      Text(
        text = "Available Download Options",
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimary
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Format Switcher Tabs: Video vs Audio
      TabRow(
        selectedTabIndex = selectedTabType,
        containerColor = Color(0xFF111115),
        contentColor = PrimaryRed,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabType]),
            color = PrimaryRed,
            height = 3.dp
          )
        },
        divider = {
          Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DarkBorder))
        }
      ) {
        Tab(
          selected = selectedTabType == 0,
          onClick = {
            selectedTabType = 0
            videoOptions.firstOrNull()?.let { onOptionSelected(it) }
          },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Video (MP4)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
          },
          selectedContentColor = PrimaryRed,
          unselectedContentColor = TextSecondary
        )
        Tab(
          selected = selectedTabType == 1,
          onClick = {
            selectedTabType = 1
            audioOptions.firstOrNull()?.let { onOptionSelected(it) }
          },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.Default.Headphones, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Audio Only", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
          },
          selectedContentColor = PrimaryRed,
          unselectedContentColor = TextSecondary
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Quality Options List
      val currentOptions = if (selectedTabType == 0) videoOptions else audioOptions
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        currentOptions.forEach { option ->
          val isSelected = selectedOption?.id == option.id
          OptionSelectionItem(
            option = option,
            isSelected = isSelected,
            onClick = { onOptionSelected(option) }
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Action Button: Download Now or View Official Offline Instructions
      if (video.isAuthorizedDownload) {
        Button(
          onClick = onDownloadClick,
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("download_video_button"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryRed,
            contentColor = Color.White
          )
        ) {
          Icon(
            imageVector = Icons.Default.Download,
            contentDescription = "Download",
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Download ${selectedOption?.quality ?: "Video"} • ${selectedOption?.sizeFormatted ?: ""}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
        }
      } else {
        Button(
          onClick = onDownloadClick,
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("view_official_guide_button"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF27272A),
            contentColor = TextPrimary
          )
        ) {
          Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Official Offline",
            tint = AccentAmber,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "View Official YouTube Offline Instructions",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    }
  }
}

@Composable
private fun OptionSelectionItem(
  option: DownloadOption,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  val borderCol by animateColorAsState(
    targetValue = if (isSelected) PrimaryRed else DarkBorder,
    label = "opt_border"
  )
  val bgCol by animateColorAsState(
    targetValue = if (isSelected) Color(0xFF2A1519) else Color(0xFF141418),
    label = "opt_bg"
  )

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(bgCol)
      .border(1.dp, borderCol, RoundedCornerShape(12.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 14.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(22.dp)
          .clip(CircleShape)
          .border(2.dp, if (isSelected) PrimaryRed else TextMuted, CircleShape)
          .background(if (isSelected) PrimaryRed else Color.Transparent),
        contentAlignment = Alignment.Center
      ) {
        if (isSelected) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Selected",
            tint = Color.White,
            modifier = Modifier.size(14.dp)
          )
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column {
        Text(
          text = option.quality,
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          color = TextPrimary
        )
        Text(
          text = "${option.resolution} • ${option.codec}",
          fontSize = 11.sp,
          color = TextMuted
        )
      }
    }

    // Size badge
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(8.dp))
        .background(Color(0xFF222228))
        .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
      Text(
        text = option.sizeFormatted,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = if (isSelected) PrimaryRed else TextSecondary
      )
    }
  }
}
