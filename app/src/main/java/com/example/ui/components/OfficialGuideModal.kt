package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.PrimaryRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun OfficialGuideModal(
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(20.dp))
        .border(1.dp, DarkBorder, RoundedCornerShape(20.dp))
        .testTag("official_guide_modal"),
      colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF332014)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = AccentAmber,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Official Offline Guide",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
              )
              Text(
                text = "Authorized Methods Only",
                fontSize = 11.sp,
                color = AccentAmber
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = TextSecondary,
              modifier = Modifier.size(20.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // YouTube Terms Notice
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF16161B))
            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
          Text(
            text = "This video is protected under Standard YouTube Copyright. In compliance with YouTube's Terms of Service and DRM protections, direct stream scraping is strictly prohibited. Here is how you can legitimately access this media offline:",
            fontSize = 12.sp,
            color = TextSecondary,
            lineHeight = 18.sp
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Method 1: YouTube Premium Offline Mode
        OfficialMethodCard(
          stepNumber = "1",
          title = "YouTube Premium Offline Downloads",
          subtitle = "Recommended for viewers",
          description = "Open the official YouTube app on your mobile device. Tap the 'Download' button directly beneath the video player. Downloaded videos are securely stored in your Library for offline watching with high resolution.",
          icon = Icons.Default.DownloadDone,
          accentColor = PrimaryRed
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Method 2: Google Takeout
        OfficialMethodCard(
          stepNumber = "2",
          title = "Google Takeout (Creator's Own Videos)",
          subtitle = "For content authors",
          description = "If you are the owner or creator of this YouTube channel, visit takeout.google.com or YouTube Studio to download your full original video archives with lossless fidelity.",
          icon = Icons.Default.Folder,
          accentColor = AccentEmerald
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Method 3: Creative Commons
        OfficialMethodCard(
          stepNumber = "3",
          title = "Creative Commons Content",
          subtitle = "Open distribution licenses",
          description = "Videos published under Creative Commons (CC BY) or Public Domain licenses can be directly saved for non-commercial educational study using our in-app download engine.",
          icon = Icons.Default.Gavel,
          accentColor = AccentAmber
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = onDismiss,
          modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .testTag("dismiss_guide_button"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryRed,
            contentColor = Color.White
          )
        ) {
          Text(
            text = "Understood",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@Composable
private fun OfficialMethodCard(
  stepNumber: String,
  title: String,
  subtitle: String,
  description: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  accentColor: Color
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(Color(0xFF131317))
      .border(1.dp, Color(0xFF222228), RoundedCornerShape(14.dp))
      .padding(12.dp)
  ) {
    Column {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(accentColor.copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = stepNumber,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
          )
          Text(
            text = subtitle,
            fontSize = 10.sp,
            color = TextMuted
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = description,
        fontSize = 12.sp,
        color = TextSecondary,
        lineHeight = 16.sp
      )
    }
  }
}
