package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.PrimaryRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AppTab

@Composable
fun AppNavbar(
  currentTab: AppTab,
  onTabSelected: (AppTab) -> Unit,
  libraryCount: Int = 0,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    color = DarkCard,
    tonalElevation = 2.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      // Top Row: Logo & Safe Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Brand Logo
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.clickable { onTabSelected(AppTab.HOME) }
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(
                Brush.linearGradient(
                  listOf(PrimaryRed, Color(0xFFDC2626), Color(0xFF991B1B))
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = "Logo",
              tint = Color.White,
              modifier = Modifier.size(20.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "STREAM",
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary,
                letterSpacing = 0.5.sp
              )
              Text(
                text = "LOAD",
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                color = PrimaryRed,
                letterSpacing = 0.5.sp
              )
            }
            Text(
              text = "Authorized Video Downloader",
              fontSize = 10.sp,
              color = TextMuted
            )
          }
        }

        // Compliance & Safety Badge
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF102A1E))
            .border(1.dp, Color(0xFF1B4D36), RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(AccentEmerald)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Official & Safe",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = AccentEmerald
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Navigation Tabs Bar (Scrollable on smaller screens)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        TabPill(
          label = "Home",
          icon = Icons.Default.CloudDownload,
          isSelected = currentTab == AppTab.HOME,
          onClick = { onTabSelected(AppTab.HOME) },
          tag = "nav_home_button"
        )
        TabPill(
          label = if (libraryCount > 0) "Library ($libraryCount)" else "Library",
          icon = Icons.Default.VideoLibrary,
          isSelected = currentTab == AppTab.LIBRARY,
          onClick = { onTabSelected(AppTab.LIBRARY) },
          tag = "nav_library_button"
        )
        TabPill(
          label = "How It Works",
          icon = Icons.Default.Shield,
          isSelected = currentTab == AppTab.HOW_IT_WORKS,
          onClick = { onTabSelected(AppTab.HOW_IT_WORKS) },
          tag = "nav_how_it_works_button"
        )
        TabPill(
          label = "FAQ & Legal",
          icon = Icons.Default.HelpOutline,
          isSelected = currentTab == AppTab.FAQ,
          onClick = { onTabSelected(AppTab.FAQ) },
          tag = "nav_faq_button"
        )
        TabPill(
          label = "About",
          icon = Icons.Default.Info,
          isSelected = currentTab == AppTab.ABOUT,
          onClick = { onTabSelected(AppTab.ABOUT) },
          tag = "nav_about_button"
        )
      }
    }
  }
}

@Composable
private fun TabPill(
  label: String,
  icon: ImageVector,
  isSelected: Boolean,
  onClick: () -> Unit,
  tag: String
) {
  val bgColor by animateColorAsState(
    targetValue = if (isSelected) PrimaryRed else Color(0xFF202024),
    label = "tab_bg"
  )
  val textColor by animateColorAsState(
    targetValue = if (isSelected) Color.White else TextSecondary,
    label = "tab_text"
  )

  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .clip(RoundedCornerShape(24.dp))
      .background(bgColor)
      .border(
        1.dp,
        if (isSelected) PrimaryRed else DarkBorder,
        RoundedCornerShape(24.dp)
      )
      .clickable(onClick = onClick)
      .padding(horizontal = 12.dp, vertical = 7.dp)
      .testTag(tag)
  ) {
    Icon(
      imageVector = icon,
      contentDescription = label,
      tint = textColor,
      modifier = Modifier.size(15.dp)
    )
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = label,
      fontSize = 12.sp,
      fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
      color = textColor
    )
  }
}
