package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.CraftsmanEntity
import com.example.data.model.AlgeriaWilayas
import com.example.data.model.AppLanguage
import com.example.data.model.TradeCategories
import com.example.ui.Localization
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.PhoneBlue
import com.example.ui.theme.RatingStarGold
import com.example.ui.theme.WhatsAppGreen

private val CraftsmanAvatarGradients = listOf(
    listOf(Color(0xFF102A43), Color(0xFF243B53)),
    listOf(Color(0xFF0F766E), Color(0xFF14B8A6)),
    listOf(Color(0xFFB45309), Color(0xFFF59E0B)),
    listOf(Color(0xFF3730A3), Color(0xFF6366F1)),
    listOf(Color(0xFF9F1239), Color(0xFFF43F5E)),
    listOf(Color(0xFF15803D), Color(0xFF22C55E))
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CraftsmanCard(
    craftsman: CraftsmanEntity,
    isBookmarked: Boolean,
    language: AppLanguage,
    onToggleBookmark: () -> Unit,
    onClickDetails: () -> Unit,
    onOpenRatingDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val category = remember(craftsman.categoryKey) {
        TradeCategories.getByKey(craftsman.categoryKey)
    }
    val wilaya = remember(craftsman.wilayaCode) {
        AlgeriaWilayas.getByCode(craftsman.wilayaCode)
    }

    val categoryName = remember(language, category) {
        when (language) {
            AppLanguage.AR -> category.nameAr
            AppLanguage.FR -> category.nameFr
            AppLanguage.EN -> category.nameEn
        }
    }

    val wilayaName = remember(wilaya, language) {
        AlgeriaWilayas.getNameForLanguage(wilaya, language)
    }
    val visibleSkills = remember(craftsman.skillsCsv) {
        craftsman.skillsCsv.split(',').asSequence()
            .map(String::trim)
            .filter(String::isNotBlank)
            .take(3)
            .toList()
    }
    val gradient = CraftsmanAvatarGradients[
        kotlin.math.abs(craftsman.avatarIndex) % CraftsmanAvatarGradients.size
    ]

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("craftsman_card_${craftsman.id}")
            .clickable { onClickDetails() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Avatar, Name, Verified, Bookmark
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(gradient)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = craftsman.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (craftsman.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = Localization.verifiedBadge(language),
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Category Chip
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(category.tagColorHex).copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = categoryName,
                                color = Color(category.tagColorHex),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Location
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "$wilayaName (${craftsman.commune})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier.testTag("bookmark_button_${craftsman.id}")
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) RatingStarGold else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 10-Point Score Rating Bar Banner or New Unrated Banner
            val isUnrated = craftsman.ratingCount == 0 || craftsman.ratingScore == 0.0
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isUnrated) Color(0xFF0D9488).copy(alpha = 0.1f) else GoldAccent.copy(alpha = 0.1f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (isUnrated) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0D9488))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (language == AppLanguage.AR) "جديد" else if (language == AppLanguage.FR) "Nouveau" else "New",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = if (language == AppLanguage.AR) "لم يقيّم بعد (كن أول من يقيّمه)" else if (language == AppLanguage.FR) "Pas encore évalué" else "Not rated yet",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0D9488)
                            )
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GoldAccent)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = String.format("%.1f", craftsman.ratingScore),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column {
                                Text(
                                    text = "${Localization.scoreOutOfTenFormat(craftsman.ratingScore)} - ${
                                        Localization.scoreRatingText(
                                            language,
                                            craftsman.ratingScore
                                        )
                                    }",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent
                                )
                                Text(
                                    text = "(${craftsman.ratingCount} ${
                                        if (language == AppLanguage.AR) "تقييم" else if (language == AppLanguage.FR) "avis" else "reviews"
                                    })",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Price & Exp info
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = Localization.dailyRateFormat(language, craftsman.dailyRateDzd),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = Localization.yearsExpFormat(language, craftsman.yearsExperience),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Description summary
            if (craftsman.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = craftsman.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Skills Chips
            if (visibleSkills.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    visibleSkills.forEach { skill ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        ) {
                            Text(
                                text = skill.trim(),
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Actions Row: Call, WhatsApp, Add Rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Phone Call
                Button(
                    onClick = { launchPhoneCall(context, craftsman.phone) },
                    colors = ButtonDefaults.buttonColors(containerColor = PhoneBlue),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("call_button_${craftsman.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = Localization.directCall(language),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // WhatsApp
                Button(
                    onClick = { launchWhatsApp(context, craftsman.whatsapp) },
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("whatsapp_button_${craftsman.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Message,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = Localization.whatsAppMsg(language),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Rate Action Button
                OutlinedButton(
                    onClick = {
                        onClickDetails()
                        onOpenRatingDialog()
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(0.82f)
                        .testTag("rate_button_${craftsman.id}"),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (language == AppLanguage.AR) "قيّم" else if (language == AppLanguage.FR) "Noter" else "Rate",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

fun launchPhoneCall(context: Context, phone: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phone")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Cannot open dialer: $phone", Toast.LENGTH_SHORT).show()
    }
}

fun launchWhatsApp(context: Context, whatsappNumber: String) {
    try {
        val formattedNumber = if (!whatsappNumber.startsWith("213") && whatsappNumber.startsWith("0")) {
            "213" + whatsappNumber.substring(1)
        } else whatsappNumber

        val url = "https://api.whatsapp.com/send?phone=$formattedNumber&text=${Uri.encode("السلام عليكم، رأيت إعلانك في تطبيق Herafi DZ وأحتاج معلومات عن خدماتك.")}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "WhatsApp error: $whatsappNumber", Toast.LENGTH_SHORT).show()
    }
}
