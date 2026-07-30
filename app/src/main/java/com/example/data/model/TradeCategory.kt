package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Carpenter
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Countertops
import androidx.compose.material.icons.filled.DoorFront
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Square
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Water
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector

data class TradeCategory(
    val key: String,
    val nameAr: String,
    val nameFr: String,
    val nameEn: String,
    val icon: ImageVector,
    val tagColorHex: Long
)

object TradeCategories {
    val ALL = TradeCategory(
        key = "ALL",
        nameAr = "جميع الحرف (الكل)",
        nameFr = "Tous les métiers",
        nameEn = "All Trades",
        icon = Icons.Default.Handyman,
        tagColorHex = 0xFF475569
    )

    val BUILDER = TradeCategory(
        key = "BUILDER",
        nameAr = "بناء / Maçon",
        nameFr = "Maçon / Bâtiment",
        nameEn = "Mason / Builder",
        icon = Icons.Default.Construction,
        tagColorHex = 0xFFB45309
    )

    val PAINTER = TradeCategory(
        key = "PAINTER",
        nameAr = "صباغ / Peintre",
        nameFr = "Peintre en bâtiment",
        nameEn = "Painter",
        icon = Icons.Default.FormatPaint,
        tagColorHex = 0xFF0284C7
    )

    val PLUMBER = TradeCategory(
        key = "PLUMBER",
        nameAr = "ترصيص صحي / Plombier",
        nameFr = "Plombier Sanitaire",
        nameEn = "Plumber",
        icon = Icons.Default.Plumbing,
        tagColorHex = 0xFF2563EB
    )

    val ELECTRICIAN = TradeCategory(
        key = "ELECTRICIAN",
        nameAr = "كهربائي / Électricien",
        nameFr = "Électricien",
        nameEn = "Electrician",
        icon = Icons.Default.ElectricBolt,
        tagColorHex = 0xFFD97706
    )

    val TILER = TradeCategory(
        key = "TILER",
        nameAr = "بلاطي / Carreleur",
        nameFr = "Carreleur / Faïencier",
        nameEn = "Tile Layer / Tiler",
        icon = Icons.Default.Square,
        tagColorHex = 0xFF0D9488
    )

    val PLASTERER = TradeCategory(
        key = "PLASTERER",
        nameAr = "جباس وبلاكو / Plâtrier",
        nameFr = "Plâtrier / Placo BA13",
        nameEn = "Plasterer / Gypsum",
        icon = Icons.Default.Architecture,
        tagColorHex = 0xFF7C3AED
    )

    val ALUMINUM_PVC = TradeCategory(
        key = "ALUMINUM_PVC",
        nameAr = "ألمنيوم و PVC / Alu",
        nameFr = "Menuiserie Alu & PVC",
        nameEn = "Aluminum & PVC",
        icon = Icons.Default.DoorFront,
        tagColorHex = 0xFF4B5563
    )

    val CARPENTER = TradeCategory(
        key = "CARPENTER",
        nameAr = "نجار خشبي / Menuisier",
        nameFr = "Menuisier Bois",
        nameEn = "Carpenter",
        icon = Icons.Default.Carpenter,
        tagColorHex = 0xFF854D0E
    )

    val WELDER = TradeCategory(
        key = "WELDER",
        nameAr = "حداد ولحام / Ferronnier",
        nameFr = "Soudeur & Ferronnier",
        nameEn = "Welder / Blacksmith",
        icon = Icons.Default.PrecisionManufacturing,
        tagColorHex = 0xFFDC2626
    )

    val AC_REPAIR = TradeCategory(
        key = "AC_REPAIR",
        nameAr = "مكيفات وتبريد / Climatisation",
        nameFr = "Climatisation & Froid",
        nameEn = "AC & Cooling Tech",
        icon = Icons.Default.Thermostat,
        tagColorHex = 0xFF0891B2
    )

    val MECHANIC = TradeCategory(
        key = "MECHANIC",
        nameAr = "ميكانيكي سيارات / Mécanicien",
        nameFr = "Mécanicien Auto & Tôle",
        nameEn = "Auto Mechanic",
        icon = Icons.Default.Build,
        tagColorHex = 0xFF4F46E5
    )

    val WATERPROOFING = TradeCategory(
        key = "WATERPROOFING",
        nameAr = "عازل رطوبة / Étanchéité",
        nameFr = "Étanchéité & Isolation",
        nameEn = "Waterproofing",
        icon = Icons.Default.WaterDrop,
        tagColorHex = 0xFF0369A1
    )

    val DRAIN_UNBLOCK = TradeCategory(
        key = "DRAIN_UNBLOCK",
        nameAr = "تسليك مجاري / Débouchage",
        nameFr = "Débouchage Canalisation",
        nameEn = "Drain Unblocking",
        icon = Icons.Default.Water,
        tagColorHex = 0xFF0F766E
    )

    val KITCHEN_INSTALLER = TradeCategory(
        key = "KITCHEN_INSTALLER",
        nameAr = "تركيب مطابخ / Cuisine",
        nameFr = "Montage Cuisine & Dressing",
        nameEn = "Kitchen Installer",
        icon = Icons.Default.Countertops,
        tagColorHex = 0xFFC05621
    )

    val SATELLITE_CAMERA = TradeCategory(
        key = "SATELLITE_CAMERA",
        nameAr = "كاميرات ودش / Caméras",
        nameFr = "Caméras & Paraboles",
        nameEn = "Cameras & Satellite",
        icon = Icons.Default.Videocam,
        tagColorHex = 0xFF6B21A8
    )

    val MOVER = TradeCategory(
        key = "MOVER",
        nameAr = "نقل أثاث / Déménagement",
        nameFr = "Déménagement & Transport",
        nameEn = "Mover & Transport",
        icon = Icons.Default.LocalShipping,
        tagColorHex = 0xFF16A34A
    )

    val GARDENER = TradeCategory(
        key = "GARDENER",
        nameAr = "حدائقي / Jardinier",
        nameFr = "Jardinier & Espaces verts",
        nameEn = "Gardener / Landscaper",
        icon = Icons.Default.Grass,
        tagColorHex = 0xFF15803D
    )

    val CLEANING = TradeCategory(
        key = "CLEANING",
        nameAr = "تنظيف شقق / Nettoyage",
        nameFr = "Nettoyage & Entretien",
        nameEn = "Cleaning Services",
        icon = Icons.Default.CleaningServices,
        tagColorHex = 0xFF0284C7
    )

    val list = listOf(
        ALL, BUILDER, PAINTER, PLUMBER, ELECTRICIAN, TILER, PLASTERER,
        ALUMINUM_PVC, CARPENTER, WELDER, AC_REPAIR, MECHANIC, WATERPROOFING,
        DRAIN_UNBLOCK, KITCHEN_INSTALLER, SATELLITE_CAMERA, MOVER, GARDENER, CLEANING
    )

    fun getByKey(key: String): TradeCategory {
        return list.find { it.key == key } ?: BUILDER
    }
}
