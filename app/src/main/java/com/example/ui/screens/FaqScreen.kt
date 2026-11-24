package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.VideoRepository
import com.example.model.FAQItem
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.PrimaryRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun FaqScreen(modifier: Modifier = Modifier) {
  val expandedMap = remember {
    mutableStateMapOf<String, Boolean>().apply {
      // First item expanded by default
      put(VideoRepository.FAQ_ITEMS.first().id, true)
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp, vertical = 14.dp)
      .testTag("faq_screen"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Column {
        Text(
          text = "Frequently Asked Questions",
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Everything you need to know about formats, permissions, and copyright.",
          fontSize = 13.sp,
          color = TextSecondary
        )
      }
    }

    // FAQ Items
    items(VideoRepository.FAQ_ITEMS.size) { index ->
      val item = VideoRepository.FAQ_ITEMS[index]
      val isExpanded = expandedMap[item.id] == true

      FaqAccordionCard(
        item = item,
        isExpanded = isExpanded,
        onToggle = {
          expandedMap[item.id] = !isExpanded
        }
      )
    }

    // Legal / Terms & Conditions Section
    item {
      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = "Terms & Legal Guidelines",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
      )
    }

    item {
      LegalInfoCard(
        title = "Terms and Conditions",
        icon = Icons.Default.Policy,
        content = "By utilizing this software, users acknowledge that downloads are restricted strictly to media for which they hold explicit intellectual property authorization, public domain standing, or non-commercial Creative Commons licensing. Stream decryption of protected commercial YouTube broadcasts is neither supported nor offered."
      )
    }

    item {
      LegalInfoCard(
        title = "Privacy Policy",
        icon = Icons.Default.PrivacyTip,
        content = "We value your privacy. No personal viewing history, pasted URLs, or file metadata are stored on third-party servers. All URL parsing and video management operations are executed on-device using local sandbox storage."
      )
    }

    // Contact Card
    item {
      ContactCard()
    }

    // Copyright Footer
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "© 2026 STREAMLOAD Media Systems. All Rights Reserved.",
            fontSize = 11.sp,
            color = TextMuted
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Built in compliance with international copyright and fair use statutes.",
            fontSize = 10.sp,
            color = TextMuted
          )
        }
      }
    }
  }
}

@Composable
private fun FaqAccordionCard(
  item: FAQItem,
  isExpanded: Boolean,
  onToggle: () -> Unit
) {
  val rotation by animateFloatAsState(
    targetValue = if (isExpanded) 180f else 0f,
    label = "accordion_rotate"
  )

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("faq_item_${item.id}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = DarkCard),
    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = onToggle)
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(Color(0xFF221518)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.HelpOutline,
              contentDescription = null,
              tint = PrimaryRed,
              modifier = Modifier.size(15.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Text(
            text = item.question,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
          )
        }

        Icon(
          imageVector = Icons.Default.ExpandMore,
          contentDescription = if (isExpanded) "Collapse" else "Expand",
          tint = TextSecondary,
          modifier = Modifier
            .size(20.dp)
            .rotate(rotation)
        )
      }

      AnimatedVisibility(visible = isExpanded) {
        Column(modifier = Modifier.padding(top = 10.dp, start = 38.dp)) {
          Text(
            text = item.answer,
            fontSize = 12.sp,
            color = TextSecondary,
            lineHeight = 17.sp
          )
        }
      }
    }
  }
}

@Composable
private fun LegalInfoCard(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  content: String
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
        Icon(imageVector = icon, contentDescription = null, tint = PrimaryRed, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(text = content, fontSize = 12.sp, color = TextSecondary, lineHeight = 17.sp)
    }
  }
}

@Composable
private fun ContactCard() {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(Color(0xFF141419))
      .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
      .padding(14.dp)
  ) {
    Column {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = PrimaryRed, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Contact & Rights Enquiries", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "For copyright clearances or API questions, reach our compliance team at legal@streamload.media. We respond to verified rights inquiries within 24 business hours.",
        fontSize = 12.sp,
        color = TextSecondary,
        lineHeight = 16.sp
      )
    }
  }
}
