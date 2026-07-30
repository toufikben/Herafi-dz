package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlgeriaWilaya
import com.example.data.model.AlgeriaWilayas
import com.example.data.model.AppLanguage
import com.example.data.model.SortOption
import com.example.ui.Localization

@Composable
fun FilterBar(
    searchQuery: String,
    selectedWilayaCode: Int,
    selectedSortOption: SortOption,
    selectedLanguage: AppLanguage,
    onSearchQueryChange: (String) -> Unit,
    onWilayaSelect: (Int) -> Unit,
    onSortSelect: (SortOption) -> Unit,
    onLanguageSelect: (AppLanguage) -> Unit,
    onResetFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    var wilayaMenuExpanded by remember { mutableStateOf(false) }
    var sortMenuExpanded by remember { mutableStateOf(false) }
    var langMenuExpanded by remember { mutableStateOf(false) }

    val currentWilaya = if (selectedWilayaCode == 0) null else AlgeriaWilayas.getByCode(selectedWilayaCode)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Top Search Bar + Language Switcher
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        text = Localization.searchPlaceholder(selectedLanguage),
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("search_text_field")
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Language Selector Pill
            Box {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier
                        .testTag("language_selector_button")
                        .clickable { langMenuExpanded = true }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = selectedLanguage.nativeName,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                DropdownMenu(
                    expanded = langMenuExpanded,
                    onDismissRequest = { langMenuExpanded = false }
                ) {
                    AppLanguage.values().forEach { lang ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "${lang.nativeName} (${lang.displayName})",
                                    fontWeight = if (lang == selectedLanguage) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                onLanguageSelect(lang)
                                langMenuExpanded = false
                            },
                            modifier = Modifier.testTag("lang_option_${lang.code}")
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Filter Dropdowns Row: Wilaya Picker & Sort Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Wilaya Picker
            Box(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (selectedWilayaCode != 0) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("wilaya_filter_dropdown")
                        .clickable { wilayaMenuExpanded = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (currentWilaya != null)
                                AlgeriaWilayas.getNameForLanguage(currentWilaya, selectedLanguage)
                            else Localization.allWilayas(selectedLanguage),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    }
                }

                DropdownMenu(
                    expanded = wilayaMenuExpanded,
                    onDismissRequest = { wilayaMenuExpanded = false },
                    modifier = Modifier.height(300.dp)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = Localization.allWilayas(selectedLanguage),
                                fontWeight = if (selectedWilayaCode == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        onClick = {
                            onWilayaSelect(0)
                            wilayaMenuExpanded = false
                        }
                    )
                    AlgeriaWilayas.list.forEach { w ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = AlgeriaWilayas.getNameForLanguage(w, selectedLanguage),
                                    fontWeight = if (w.code == selectedWilayaCode) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                onWilayaSelect(w.code)
                                wilayaMenuExpanded = false
                            },
                            modifier = Modifier.testTag("wilaya_option_${w.code}")
                        )
                    }
                }
            }

            // Sort Selector Dropdown
            Box(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sort_filter_dropdown")
                        .clickable { sortMenuExpanded = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (selectedSortOption) {
                                SortOption.HIGHEST_RATED -> Localization.highestRated(selectedLanguage)
                                SortOption.MOST_REVIEWS -> Localization.mostReviews(selectedLanguage)
                                SortOption.NEAREST -> Localization.nearestProximity(selectedLanguage)
                                SortOption.PRICE_LOW -> Localization.lowestPrice(selectedLanguage)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    }
                }

                DropdownMenu(
                    expanded = sortMenuExpanded,
                    onDismissRequest = { sortMenuExpanded = false }
                ) {
                    SortOption.values().forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = when (option) {
                                        SortOption.HIGHEST_RATED -> Localization.highestRated(selectedLanguage)
                                        SortOption.MOST_REVIEWS -> Localization.mostReviews(selectedLanguage)
                                        SortOption.NEAREST -> Localization.nearestProximity(selectedLanguage)
                                        SortOption.PRICE_LOW -> Localization.lowestPrice(selectedLanguage)
                                    },
                                    fontWeight = if (option == selectedSortOption) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                onSortSelect(option)
                                sortMenuExpanded = false
                            },
                            modifier = Modifier.testTag("sort_option_${option.name}")
                        )
                    }
                }
            }
        }
    }
}
