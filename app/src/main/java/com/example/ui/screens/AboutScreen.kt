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
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
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
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.PrimaryRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AboutScreen(modifier: Modifier = Modifier) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp, vertical = 14.dp)
      .testTag("about_screen"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Brand Hero Box
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(20.dp))
          .background(DarkCard)
          .border(1.dp, DarkBorder, RoundedCornerShape(20.dp))
          .padding(20.dp)
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
          Box(
            modifier = Modifier
              .size(52.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(Brush.linearGradient(listOf(PrimaryRed, Color(0xFFDC2626)))),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(30.dp)
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "STREAMLOAD",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = TextPrimary,
            letterSpacing = 1.sp
          )
          Text(
            text = "Modern Video Downloader & Manager",
            fontSize = 12.sp,
            color = PrimaryRed,
            fontWeight = FontWeight.SemiBold
          )

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "Version 1.0.0 (Production Release)",
            fontSize = 11.sp,
            color = TextMuted
          )
        }
      }
    }

    // Design & Architecture Specs
    item {
      Text(
        text = "Technical Architecture",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
      )
    }

    item {
      SpecCard(
        title = "Design System & UX",
        icon = Icons.Default.Palette,
        description = "Premium SaaS dashboard aesthetic (#09090B Dark mode, #18181B Cards, #EF4444 Crimson accent). Rounded card corners, fluid animations, and high contrast typography."
      )
    }

    item {
      SpecCard(
        title = "URL Parser & Resolution Matrix",
        icon = Icons.Default.Code,
        description = "Universal regex recognition for standard YouTube URLs, short URLs, Shorts links, and direct media streams. Formats: MP4 1080p, 720p, 480p, 360p and MP3/M4A audio."
      )
    }

    item {
      SpecCard(
        title = "Download Queue Engine",
        icon = Icons.Default.Layers,
        description = "Background coroutine download pipeline with pause/resume support, live throughput measurement, dynamic remaining time estimation, and local sandbox library management."
      )
    }

    item {
      SpecCard(
        title = "DRM & Compliance Safeguards",
        icon = Icons.Default.Security,
        description = "Operates under strict legal adherence: never scrapes restricted YouTube media, bypasses DRM or decrypts protected content. Provides official offline mode workflows."
      )
    }

    item {
      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}

@Composable
private fun SpecCard(
  title: String,
  icon: ImageVector,
  description: String
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(DarkCard)
      .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
      .padding(14.dp)
  ) {
    Column {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(Color(0xFF281318)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryRed,
            modifier = Modifier.size(16.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = title,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = description,
        fontSize = 12.sp,
        color = TextSecondary,
        lineHeight = 17.sp
      )
    }
  }
}
