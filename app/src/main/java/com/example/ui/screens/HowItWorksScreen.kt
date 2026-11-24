package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.PrimaryRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppTab

@Composable
fun HowItWorksScreen(
  onNavigateTab: (AppTab) -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp, vertical = 14.dp)
      .testTag("how_it_works_screen"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Column {
        Text(
          text = "How It Works",
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Streamlined, legal, and authorized video management in 3 steps.",
          fontSize = 13.sp,
          color = TextSecondary
        )
      }
    }

    // Step 1
    item {
      StepDetailCard(
        stepNumber = "1",
        title = "Enter a Supported URL",
        subtitle = "YouTube videos, Shorts, and direct authorized MP4 streams",
        description = "Copy any video link from YouTube (standard watch URLs, short URLs like youtu.be, or Shorts links). Tap the 'Paste' button inside our search bar for instant 1-tap clipboard import.",
        icon = Icons.Default.Link,
        tag = "step_1_card"
      )
    }

    // Step 2
    item {
      StepDetailCard(
        stepNumber = "2",
        title = "Verify Download Permission",
        subtitle = "Zero-scraping copyright verification",
        description = "Our system analyzes the media's distribution rights. If it is licensed under Creative Commons, Public Domain, or educational open source, in-app high-speed downloading is enabled immediately. For protected YouTube videos, we display the official YouTube Premium offline method.",
        icon = Icons.Default.Security,
        tag = "step_2_card"
      )
    }

    // Step 3
    item {
      StepDetailCard(
        stepNumber = "3",
        title = "Choose an Available Option",
        subtitle = "HD 1080p, 720p, 480p, 360p or Crystal-Clear Audio",
        description = "Pick your preferred resolution or audio format. Watch live progress with speed and ETA indicators, then access and share your files anytime from the local Library tab.",
        icon = Icons.Default.Download,
        tag = "step_3_card"
      )
    }

    // Compliance Notice Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131317)),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
      ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Policy, contentDescription = null, tint = AccentEmerald, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Strict Compliance Standard", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "We strictly uphold YouTube Terms of Service and Digital Millennium Copyright Act regulations. We do not extract encrypted streams, bypass DRM, or encourage unauthorized copying of commercial intellectual property.",
            fontSize = 12.sp,
            color = TextSecondary,
            lineHeight = 17.sp
          )
        }
      }
    }

    // CTA
    item {
      Button(
        onClick = { onNavigateTab(AppTab.HOME) },
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed, contentColor = Color.White)
      ) {
        Text("Try It Now On Home Screen", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(6.dp))
        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
      }
    }

    item {
      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}

@Composable
private fun StepDetailCard(
  stepNumber: String,
  title: String,
  subtitle: String,
  description: String,
  icon: ImageVector,
  tag: String
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .background(DarkCard)
      .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
      .padding(16.dp)
      .testTag(tag)
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color(0xFF281318)),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = stepNumber,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = PrimaryRed
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )
          Text(
            text = subtitle,
            fontSize = 11.sp,
            color = TextMuted
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = description,
        fontSize = 12.sp,
        color = TextSecondary,
        lineHeight = 18.sp
      )
    }
  }
}
