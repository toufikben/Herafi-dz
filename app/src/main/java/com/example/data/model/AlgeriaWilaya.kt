package com.example.data.model

data class AlgeriaWilaya(
    val code: Int,
    val nameAr: String,
    val nameFr: String,
    val nameEn: String = nameFr
)

object AlgeriaWilayas {
    val list = listOf(
        AlgeriaWilaya(1, "أدرار", "Adrar"),
        AlgeriaWilaya(2, "الشلف", "Chlef"),
        AlgeriaWilaya(3, "الأغواط", "Laghouat"),
        AlgeriaWilaya(4, "أم البواقي", "Oum El Bouaghi"),
        AlgeriaWilaya(5, "باتنة", "Batna"),
        AlgeriaWilaya(6, "بجاية", "Béjaïa"),
        AlgeriaWilaya(7, "بسكرة", "Biskra"),
        AlgeriaWilaya(8, "بشار", "Béchar"),
        AlgeriaWilaya(9, "البليدة", "Blida"),
        AlgeriaWilaya(10, "البويرة", "Bouira"),
        AlgeriaWilaya(11, "تمنراست", "Tamanrasset"),
        AlgeriaWilaya(12, "تبسة", "Tébessa"),
        AlgeriaWilaya(13, "تلمسان", "Tlemcen"),
        AlgeriaWilaya(14, "تيارت", "Tiaret"),
        AlgeriaWilaya(15, "تيزي وزو", "Tizi Ouzou"),
        AlgeriaWilaya(16, "الجزائر العاصمة", "Alger"),
        AlgeriaWilaya(17, "الجلفة", "Djelfa"),
        AlgeriaWilaya(18, "جيجل", "Jijel"),
        AlgeriaWilaya(19, "سطيف", "Sétif"),
        AlgeriaWilaya(20, "سعيدة", "Saïda"),
        AlgeriaWilaya(21, "سكيكدة", "Skikda"),
        AlgeriaWilaya(22, "سيدي بلعباس", "Sidi Bel Abbès"),
        AlgeriaWilaya(23, "عنابة", "Annaba"),
        AlgeriaWilaya(24, "قالمة", "Guelma"),
        AlgeriaWilaya(25, "قسنطينة", "Constantine"),
        AlgeriaWilaya(26, "المدية", "Médéa"),
        AlgeriaWilaya(27, "مستغانم", "Mostaganem"),
        AlgeriaWilaya(28, "المسيلة", "M'Sila"),
        AlgeriaWilaya(29, "معسكر", "Mascara"),
        AlgeriaWilaya(30, "ورقلة", "Ouargla"),
        AlgeriaWilaya(31, "وهران", "Oran"),
        AlgeriaWilaya(32, "البيض", "El Bayadh"),
        AlgeriaWilaya(33, "إليزي", "Illizi"),
        AlgeriaWilaya(34, "برج بوعريريج", "Bordj Bou Arréridj"),
        AlgeriaWilaya(35, "بومرداس", "Boumerdès"),
        AlgeriaWilaya(36, "الطارف", "El Tarf"),
        AlgeriaWilaya(37, "تندوف", "Tindouf"),
        AlgeriaWilaya(38, "تسمسيلت", "Tissemsilt"),
        AlgeriaWilaya(39, "الوادي", "El Oued"),
        AlgeriaWilaya(40, "خنشلة", "Khenchela"),
        AlgeriaWilaya(41, "سوق أهراس", "Souk Ahras"),
        AlgeriaWilaya(42, "تيبازة", "Tipaza"),
        AlgeriaWilaya(43, "ميلة", "Mila"),
        AlgeriaWilaya(44, "عين الدفلى", "Aïn Defla"),
        AlgeriaWilaya(45, "النعامة", "Naâma"),
        AlgeriaWilaya(46, "عين تموشنت", "Aïn Témouchent"),
        AlgeriaWilaya(47, "غرداية", "Ghardaïa"),
        AlgeriaWilaya(48, "غليزان", "Relizane"),
        AlgeriaWilaya(49, "تيميمون", "Timimoun"),
        AlgeriaWilaya(50, "برج باجي مختار", "Bordj Badji Mokhtar"),
        AlgeriaWilaya(51, "أولاد جلال", "Ouled Djellal"),
        AlgeriaWilaya(52, "بني عباس", "Béni Abbès"),
        AlgeriaWilaya(53, "عين صالح", "In Salah"),
        AlgeriaWilaya(54, "عين قزام", "In Guezzam"),
        AlgeriaWilaya(55, "تقرت", "Touggourt"),
        AlgeriaWilaya(56, "جانت", "Djanet"),
        AlgeriaWilaya(57, "المغير", "El M'Ghair"),
        AlgeriaWilaya(58, "المنيعة", "El Meniaa")
    )

    fun getByCode(code: Int): AlgeriaWilaya {
        return list.find { it.code == code } ?: list[15] // Default Algiers
    }

    fun getNameForLanguage(wilaya: AlgeriaWilaya, lang: AppLanguage): String {
        return when (lang) {
            AppLanguage.AR -> "${wilaya.code} - ${wilaya.nameAr}"
            AppLanguage.FR -> "${wilaya.code} - ${wilaya.nameFr}"
            AppLanguage.EN -> "${wilaya.code} - ${wilaya.nameEn}"
        }
    }
}
