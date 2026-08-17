package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.db.CraftsmanEntity
import com.example.data.model.AppLanguage
import com.example.ui.Localization
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.RatingStarGold

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RatingSubmissionDialog(
    craftsman: CraftsmanEntity,
    language: AppLanguage,
    currentUser: com.example.data.db.UserEntity? = null,
    onDismiss: () -> Unit,
    onSubmitRating: (
        reviewerName: String,
        scoreTen: Double,
        comment: String,
        qualityScore: Double,
        punctualityScore: Double,
        priceScore: Double,
        tagsCsv: String
    ) -> Unit
) {
    var scoreTen by remember { mutableDoubleStateOf(9.0) }
    var qualityScore by remember { mutableDoubleStateOf(9.0) }
    var punctualityScore by remember { mutableDoubleStateOf(9.0) }
    var priceScore by remember { mutableDoubleStateOf(9.0) }

    var reviewerName by remember { mutableStateOf(currentUser?.fullName ?: "") }
    var comment by remember { mutableStateOf("") }

    val availableTags = listOf(
        Localization.Ui.text("quick_on_time", language),
        Localization.Ui.text("quick_quality", language),
        Localization.Ui.text("quick_fair_price", language),
        Localization.Ui.text("quick_clean", language),
        Localization.Ui.text("quick_polite", language),
        Localization.Ui.text("quick_professional", language)
    )

    val selectedTags = remember { mutableStateOf(setOf(availableTags[0], availableTags[1])) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("rating_dialog_card")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Text(
                    text = Localization.addRatingTitle(language),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = craftsman.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Score Out of 10 display banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GoldAccent.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = Localization.scoreLabel(language),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = String.format("%.1f", scoreTen),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent
                            )
                            Text(
                                text = " / 10",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Interactive Star Row (maps 1..5 stars to 2..10 points)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            (1..5).forEach { starIndex ->
                                val starScoreThreshold = starIndex * 2.0
                                val isFilled = scoreTen >= starScoreThreshold - 1.0
                                Icon(
                                    imageVector = if (isFilled) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "Star $starIndex",
                                    tint = RatingStarGold,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clickable { scoreTen = starIndex * 2.0 }
                                        .testTag("star_rate_button_$starIndex")
                                )
                            }
                        }

                        // Fine Score Slider
                        Slider(
                            value = scoreTen.toFloat(),
                            onValueChange = { scoreTen = Math.round(it * 2.0) / 2.0 },
                            valueRange = 1f..10f,
                            steps = 17, // 0.5 steps
                            colors = SliderDefaults.colors(
                                thumbColor = GoldAccent,
                                activeTrackColor = GoldAccent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Sub-aspect ratings
                Text(
                    text = Localization.qualityLabel(language),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Slider(
                        value = qualityScore.toFloat(),
                        onValueChange = { qualityScore = Math.round(it * 2.0) / 2.0 },
                        valueRange = 1f..10f,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${String.format("%.1f", qualityScore)}/10",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Text(
                    text = Localization.punctualityLabel(language),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Slider(
                        value = punctualityScore.toFloat(),
                        onValueChange = { punctualityScore = Math.round(it * 2.0) / 2.0 },
                        valueRange = 1f..10f,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${String.format("%.1f", punctualityScore)}/10",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Text(
                    text = Localization.priceLabel(language),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Slider(
                        value = priceScore.toFloat(),
                        onValueChange = { priceScore = Math.round(it * 2.0) / 2.0 },
                        valueRange = 1f..10f,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${String.format("%.1f", priceScore)}/10",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Reviewer Name & Comment
                OutlinedTextField(
                    value = reviewerName,
                    onValueChange = { reviewerName = it },
                    label = { Text(Localization.fullName(language)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reviewer_name_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text(Localization.commentLabel(language)) },
                    minLines = 3,
                    maxLines = 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reviewer_comment_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Quality Tag Chips
                Text(
                    text = if (language == AppLanguage.AR) Localization.Ui.text("tags_label", language) else "Tags & Features",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    availableTags.forEach { tag ->
                        val isSelected = selectedTags.value.contains(tag)
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) GoldAccent else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable {
                                val current = selectedTags.value.toMutableSet()
                                if (isSelected) current.remove(tag) else current.add(tag)
                                selectedTags.value = current
                            }
                        ) {
                            Text(
                                text = tag,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Submit / Cancel Buttons
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(Localization.cancel(language))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSubmitRating(
                                reviewerName,
                                scoreTen,
                                comment,
                                qualityScore,
                                punctualityScore,
                                priceScore,
                                selectedTags.value.joinToString(",")
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                        modifier = Modifier.testTag("submit_rating_button")
                    ) {
                        Text(
                            text = Localization.submit(language),
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
