package com.example.data

import com.example.data.db.CraftsmanEntity
import com.example.data.db.ReviewEntity

object SeedData {
    val initialCraftsmen = listOf(
        // BUILDERS / MASONS
        CraftsmanEntity(
            id = "c1_builder_algiers",
            name = "عمي رشيد القاسمي",
            categoryKey = "BUILDER",
            phone = "0661234567",
            whatsapp = "213661234567",
            wilayaCode = 16, // Algiers
            commune = "باب الزوار (Bab Ezzouar)",
            ratingScore = 9.8,
            ratingCount = 42,
            dailyRateDzd = 4500,
            isVerified = true,
            yearsExperience = 18,
            description = "معلم بناء خبرة 18 سنة في بناء الفيلات، الشقق، الأساسات والخرسانة المسلحة. إتقان تام للمخططات المعمارية والدقة في المواعيد.",
            skillsCsv = "بناء الفيلات,صب الخرسانة,تقسيم الغرف,إعادة التهيئة",
            avatarIndex = 0,
            distanceKmSimulated = 1.2
        ),
        CraftsmanEntity(
            id = "c2_builder_oran",
            name = "الأخ مصطفى الوهراني",
            categoryKey = "BUILDER",
            phone = "0550987654",
            whatsapp = "213550987654",
            wilayaCode = 31, // Oran
            commune = "عين الترك (Aïn El Turk)",
            ratingScore = 9.5,
            ratingCount = 29,
            dailyRateDzd = 4000,
            isVerified = true,
            yearsExperience = 12,
            description = "مقاول بناء وترميم مباني قديمة في وهران ونواحيها. جودة عالية وأسعار معقولة.",
            skillsCsv = "ترميم المنازل,بناء بالآجور,تلييس الجدران",
            avatarIndex = 1,
            distanceKmSimulated = 3.5
        ),

        // PAINTERS / PEINTRES
        CraftsmanEntity(
            id = "c3_painter_algiers",
            name = "حمزة للديكور والصباغة",
            categoryKey = "PAINTER",
            phone = "0771122334",
            whatsapp = "213771122334",
            wilayaCode = 16, // Algiers
            commune = "الشراقة (Chéraga)",
            ratingScore = 9.9,
            ratingCount = 56,
            dailyRateDzd = 3500,
            isVerified = true,
            yearsExperience = 10,
            description = "متخصص في جميع أنواع الدهانات الحديثة: ستوكو، خيال، صقلي، دهانات زيتية ومائية، عزل الرطوبة وحماية الواجهات.",
            skillsCsv = "دهان ستوكو وخيال,صباغة مائية,عزل الرطوبة,واجهات خارجية",
            avatarIndex = 2,
            distanceKmSimulated = 2.1
        ),
        CraftsmanEntity(
            id = "c4_painter_setif",
            name = "كمال الصباغ السطايفي",
            categoryKey = "PAINTER",
            phone = "0668998877",
            whatsapp = "213668998877",
            wilayaCode = 19, // Sétif
            commune = "العلمة (El Eulma)",
            ratingScore = 9.2,
            ratingCount = 18,
            dailyRateDzd = 3000,
            isVerified = true,
            yearsExperience = 8,
            description = "صباغة منازل ومحلات تجارية بسرعة ونظافة فائقة، نضمن لك اللمسة النهائية الفخمة.",
            skillsCsv = "صباغة شقق,صباغة خشب وألمنيوم,معجون الجدران Enduit",
            avatarIndex = 3,
            distanceKmSimulated = 4.8
        ),

        // PLUMBERS / PLOMBIERS
        CraftsmanEntity(
            id = "c5_plumber_blida",
            name = "الأسطى نذير الترصيص الصحي",
            categoryKey = "PLUMBER",
            phone = "0561445566",
            whatsapp = "213561445566",
            wilayaCode = 9, // Blida
            commune = "أولاد يعيش (Ouled Yaïch)",
            ratingScore = 9.7,
            ratingCount = 38,
            dailyRateDzd = 3500,
            isVerified = true,
            yearsExperience = 14,
            description = "ترصيص صحي وغاز، تركيب السخانات وقنوات التدفئة المركزية، كشف ومعالجة تسربات المياه بأحدث الأجهزة.",
            skillsCsv = "تدفئة مركزية,تركيب سخانات الغاز,شبكات المياه PEX/Multouche,كشف التسربات",
            avatarIndex = 4,
            distanceKmSimulated = 1.8
        ),
        CraftsmanEntity(
            id = "c6_plumber_constantine",
            name = "عبد القادر القسنطيني",
            categoryKey = "PLUMBER",
            phone = "0799334455",
            whatsapp = "213799334455",
            wilayaCode = 25, // Constantine
            commune = "الخروب (Khroub)",
            ratingScore = 9.4,
            ratingCount = 22,
            dailyRateDzd = 3200,
            isVerified = true,
            yearsExperience = 9,
            description = "ترصيص صحي وتصليح السخانات والمضخات المائية بسرعة على مدار الأسبوع.",
            skillsCsv = "تركيب الحمامات,مضخات المياه,سخانات حمام",
            avatarIndex = 5,
            distanceKmSimulated = 3.0
        ),

        // ELECTRICIANS / ÉLECTRICIENS
        CraftsmanEntity(
            id = "c7_electrician_annaba",
            name = "الكهربائي يوسف العنابي",
            categoryKey = "ELECTRICIAN",
            phone = "0670556677",
            whatsapp = "213670556677",
            wilayaCode = 23, // Annaba
            commune = "البوني (El Bouni)",
            ratingScore = 9.6,
            ratingCount = 31,
            dailyRateDzd = 4000,
            isVerified = true,
            yearsExperience = 11,
            description = "كهرباء المعمارية والصناعية، إنجاز مخططات الكهرباء للمنازل، تركيب لوحات التوزيع وقواطع الحماية والكاميرات.",
            skillsCsv = "كهرباء منازل,لوحات التوزيع,تركيب الأضواء المخفية LED,أنظمة الإنذار",
            avatarIndex = 6,
            distanceKmSimulated = 2.4
        ),
        CraftsmanEntity(
            id = "c8_electrician_algiers",
            name = "سفيان تكنو كهرباء",
            categoryKey = "ELECTRICIAN",
            phone = "0555123987",
            whatsapp = "213555123987",
            wilayaCode = 16, // Algiers
            commune = "القبة (Kouba)",
            ratingScore = 9.9,
            ratingCount = 64,
            dailyRateDzd = 4500,
            isVerified = true,
            yearsExperience = 15,
            description = "تقني سامي في الكهرباء، صيانة الأعطال المستعجلة، تركيب الطاقة الشمسية والأنظمة الذكية.",
            skillsCsv = "تصليح أطبات الكهرباء,طاقة شمسية,إنتركوم وكاميرات",
            avatarIndex = 7,
            distanceKmSimulated = 0.9
        ),

        // TILERS / CARRELEURS
        CraftsmanEntity(
            id = "c9_tiler_tlemcen",
            name = "الأستاذ مراد البلاطي",
            categoryKey = "TILER",
            phone = "0664887766",
            whatsapp = "213664887766",
            wilayaCode = 13, // Tlemcen
            commune = "منصورة (Mansourah)",
            ratingScore = 9.8,
            ratingCount = 35,
            dailyRateDzd = 4000,
            isVerified = true,
            yearsExperience = 16,
            description = "تركيب البلاط والدال دو سول والبورسلان والموزاييك بدقة متناهية واستخدام جهاز الليزر للتسوية.",
            skillsCsv = "بورسلان كبير الحجم,دال دو سول,سيراميك الحمامات,بلاط الديكور",
            avatarIndex = 8,
            distanceKmSimulated = 2.9
        ),

        // AC TECHNICIANS / CLIMATISATION
        CraftsmanEntity(
            id = "c10_ac_batna",
            name = "وليد تكييف وتبريد",
            categoryKey = "AC_REPAIR",
            phone = "0778990011",
            whatsapp = "213778990011",
            wilayaCode = 5, // Batna
            commune = "عين توتة (Aïn Touta)",
            ratingScore = 9.5,
            ratingCount = 27,
            dailyRateDzd = 3800,
            isVerified = true,
            yearsExperience = 7,
            description = "تركيب وتنظيف وصيانة المكيفات الهوائية بجميع أنواعها وشحن غاز الفريون R410A / R32 / R22 مع ضمان الخدمة.",
            skillsCsv = "شحن الفريون,تنظيف المكيفات,تصليح الكروت الإلكترونية,تركيب مكيفات إنفرتر",
            avatarIndex = 9,
            distanceKmSimulated = 3.8
        ),

        // PLASTERERS / GYPSUM WORKERS
        CraftsmanEntity(
            id = "c11_plasterer_tizi",
            name = "إسماعيل جبس وبلاكو باثر",
            categoryKey = "PLASTERER",
            phone = "0560112233",
            whatsapp = "213560112233",
            wilayaCode = 15, // Tizi Ouzou
            commune = "عزازقة (Azazga)",
            ratingScore = 9.7,
            ratingCount = 41,
            dailyRateDzd = 4200,
            isVerified = true,
            yearsExperience = 13,
            description = "ديكورات الجبس المغربي والأسقف المستعارة بالبلاكو بلاتر (Placo Plâtre BA13)، إضاءة مخفية وأقواس فنية.",
            skillsCsv = "Placo Plâtre BA13,أسقف مستعارة,ديكور شاشات التلفزيون,عزل صوتي",
            avatarIndex = 10,
            distanceKmSimulated = 1.5
        ),

        // CARPENTERS & WELDERS
        CraftsmanEntity(
            id = "c12_welder_chlef",
            name = "المعلم طارق لحام وحداد",
            categoryKey = "WELDER",
            phone = "0672334455",
            whatsapp = "213672334455",
            wilayaCode = 2, // Chlef
            commune = "الشلف المركز (Chlef Center)",
            ratingScore = 9.3,
            ratingCount = 19,
            dailyRateDzd = 3800,
            isVerified = true,
            yearsExperience = 10,
            description = "حدادة فنية، صناعة الأبواب والنوافذ والسلالم الحديدية والمظلات (Rideaux métalliques & bâche).",
            skillsCsv = "حدادة فنية,أبواب أمان,سقائف ومظلات,لحام الأرجون",
            avatarIndex = 11,
            distanceKmSimulated = 4.1
        ),

        // NEW / UNRATED CRAFTSMEN (TIER 2 - DISCOVERY)
        CraftsmanEntity(
            id = "c13_builder_djelfa",
            name = "إبراهيم المعلم البنّاء",
            categoryKey = "BUILDER",
            phone = "0669112233",
            whatsapp = "213669112233",
            wilayaCode = 17, // Djelfa
            commune = "الجلفة المركز (Djelfa Center)",
            ratingScore = 0.0,
            ratingCount = 0,
            dailyRateDzd = 3500,
            isVerified = true,
            yearsExperience = 6,
            description = "حرفي بنّاء مسجل حديثاً في الدليل. بناء جدران الآجور والأساسات والمنازل الفردية.",
            skillsCsv = "بناء الآجور,تقسيم الجدران,ترميم",
            avatarIndex = 0,
            distanceKmSimulated = 2.0
        ),
        CraftsmanEntity(
            id = "c14_painter_medea",
            name = "أسامة الصباغ المدية",
            categoryKey = "PAINTER",
            phone = "0770445566",
            whatsapp = "213770445566",
            wilayaCode = 26, // Médéa
            commune = "المدية المركز (Médéa Center)",
            ratingScore = 0.0,
            ratingCount = 0,
            dailyRateDzd = 2800,
            isVerified = true,
            yearsExperience = 4,
            description = "صباغة شقق ومنازل بديكورات حديثة وأسعار تنافسية. جديد في المنصة.",
            skillsCsv = "صباغة مائية,معجون الجدران,طلاء خشب",
            avatarIndex = 1,
            distanceKmSimulated = 3.2
        ),
        CraftsmanEntity(
            id = "c15_mechanic_algiers",
            name = "شريف ميكانيك وسيارات",
            categoryKey = "MECHANIC",
            phone = "0554889900",
            whatsapp = "213554889900",
            wilayaCode = 16, // Algiers
            commune = "حسين داي (Hussein Dey)",
            ratingScore = 0.0,
            ratingCount = 0,
            dailyRateDzd = 4000,
            isVerified = true,
            yearsExperience = 9,
            description = "ميكانيكي سيارات، صيانة وسكانير المحركات، تغيير الزيت وتصليح الفرامل.",
            skillsCsv = "سكانير السيارات,تغيير زيت,تصليح الفرامل,ميكانيك عام",
            avatarIndex = 2,
            distanceKmSimulated = 1.1
        ),
        CraftsmanEntity(
            id = "c16_mover_boumerdes",
            name = "أنور ونقل البضائع",
            categoryKey = "MOVER",
            phone = "0671223344",
            whatsapp = "213671223344",
            wilayaCode = 35, // Boumerdès
            commune = "بومرداس المركز",
            ratingScore = 0.0,
            ratingCount = 0,
            dailyRateDzd = 5000,
            isVerified = true,
            yearsExperience = 5,
            description = "نقل الأثاث والمعدات بشاحنات مغلقة وآمنة مع فريق لشحن والتفريغ عبر كل الولايات.",
            skillsCsv = "نقل الأثاث,تغليف الأثاث,نقل بضائع,تفريغ وشحن",
            avatarIndex = 3,
            distanceKmSimulated = 2.8
        ),

        // LOWER / AVERAGE RATED CRAFTSMEN (TIER 3)
        CraftsmanEntity(
            id = "c17_builder_low",
            name = "صالح الأشغال السريعة",
            categoryKey = "BUILDER",
            phone = "0551002233",
            whatsapp = "213551002233",
            wilayaCode = 16, // Algiers
            commune = "براقي (Baraki)",
            ratingScore = 4.8,
            ratingCount = 5,
            dailyRateDzd = 2500,
            isVerified = false,
            yearsExperience = 3,
            description = "أشغال بناء بسيطة وترميمات سريعة للجدران والحدائق.",
            skillsCsv = "بناء بسيط,تلييس جدران",
            avatarIndex = 4,
            distanceKmSimulated = 5.0
        ),
        CraftsmanEntity(
            id = "c18_painter_low",
            name = "عادل الصباغ البسيط",
            categoryKey = "PAINTER",
            phone = "0772110099",
            whatsapp = "213772110099",
            wilayaCode = 31, // Oran
            commune = "السانية (Es Sénia)",
            ratingScore = 5.2,
            ratingCount = 7,
            dailyRateDzd = 2200,
            isVerified = false,
            yearsExperience = 2,
            description = "صباغة مائية عادية ورخيصة للمستودعات والمحلات.",
            skillsCsv = "صباغة عادية",
            avatarIndex = 5,
            distanceKmSimulated = 6.2
        )
    )

    val sampleReviews = listOf(
        ReviewEntity(
            id = "r1",
            craftsmanId = "c1_builder_algiers",
            reviewerName = "أحمد ب.",
            scoreTen = 10.0,
            comment = "عمي رشيد معلم إحسان بامتياز، أتم بناء الطابق العلوي بالفيلّا في الموعد المحدد وبدقة جبارة.",
            qualityFinishScore = 10.0,
            punctualityScore = 10.0,
            priceFairnessScore = 9.5,
            tagsCsv = "دقة المواعيد,عمل متقن,سعر عادل,احترام"
        ),
        ReviewEntity(
            id = "r2",
            craftsmanId = "c1_builder_algiers",
            reviewerName = "سفيان م.",
            scoreTen = 9.6,
            comment = "عمل ممتاز ونظيف جداً، يسدي النصائح المفيدة ويقتصد في مواد البناء.",
            qualityFinishScore = 9.8,
            punctualityScore = 9.5,
            priceFairnessScore = 9.5,
            tagsCsv = "نظافة العمل,خبرة عالية"
        ),
        ReviewEntity(
            id = "r3",
            craftsmanId = "c3_painter_algiers",
            reviewerName = "مريم ك.",
            scoreTen = 10.0,
            comment = "حمزة قام بصباغة الصالون بديكور الخيال، النتيجة كانت أكثر من رائعة والألوان متناسقة جداً.",
            qualityFinishScore = 10.0,
            punctualityScore = 10.0,
            priceFairnessScore = 10.0,
            tagsCsv = "لمسة فنية,نظافة التلوين,سريع"
        ),
        ReviewEntity(
            id = "r4",
            craftsmanId = "c5_plumber_blida",
            reviewerName = "كريم ت.",
            scoreTen = 9.5,
            comment = "تم تركيب التدفئة المركزية في منزلنا باحترافية، وقام باختبار جميع الأنابيب قبل المغادرة.",
            qualityFinishScore = 9.5,
            punctualityScore = 9.5,
            priceFairnessScore = 9.5,
            tagsCsv = "تقني محترف,ضمان الخدمة"
        )
    )
}
