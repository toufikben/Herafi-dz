package com.example.ui

import com.example.data.model.AppLanguage

object Localization {

    fun appTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "حرفي الجزائر"
        AppLanguage.FR -> "Herafi DZ"
        AppLanguage.EN -> "Herafi DZ"
    }

    fun appSubTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "دليل العمال والحرفيين في جميع ولايات الجزائر"
        AppLanguage.FR -> "Guide des artisans et ouvriers en Algérie"
        AppLanguage.EN -> "Directory of craftsmen & manual workers in Algeria"
    }

    fun searchPlaceholder(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "ابحث عن اسم، بناء، صباغ، سباك، بلدية..."
        AppLanguage.FR -> "Rechercher maçon, peintre, plombier, ville..."
        AppLanguage.EN -> "Search builder, painter, plumber, city..."
    }

    fun selectWilaya(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "اختر الولاية"
        AppLanguage.FR -> "Sélectionner la Wilaya"
        AppLanguage.EN -> "Select Wilaya"
    }

    fun allWilayas(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "جميع الولايات (58)"
        AppLanguage.FR -> "Toutes les Wilayas (58)"
        AppLanguage.EN -> "All Wilayas (58)"
    }

    fun sortBy(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "ترتيب حسب"
        AppLanguage.FR -> "Trier par"
        AppLanguage.EN -> "Sort by"
    }

    fun highestRated(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "الأعلى تقييماً (10/10)"
        AppLanguage.FR -> "Mieux notés (10/10)"
        AppLanguage.EN -> "Highest Rated (10/10)"
    }

    fun mostReviews(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "الأكثر تقييماً"
        AppLanguage.FR -> "Plus d'avis"
        AppLanguage.EN -> "Most Reviews"
    }

    fun nearestProximity(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "الأقرب مسافة"
        AppLanguage.FR -> "Plus proches"
        AppLanguage.EN -> "Nearest Proximity"
    }

    fun lowestPrice(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "السعر المناسب"
        AppLanguage.FR -> "Prix le plus bas"
        AppLanguage.EN -> "Lowest Rate"
    }

    fun directCall(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "اتصال مباشر"
        AppLanguage.FR -> "Appeler"
        AppLanguage.EN -> "Call Directly"
    }

    fun whatsAppMsg(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "واتساب"
        AppLanguage.FR -> "WhatsApp"
        AppLanguage.EN -> "WhatsApp"
    }

    fun shareProfile(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "مشاركة الملف"
        AppLanguage.FR -> "Partager le profil"
        AppLanguage.EN -> "Share profile"
    }

    fun rateAndReview(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "إضافة تقييم للعمل"
        AppLanguage.FR -> "Évaluer cet artisan"
        AppLanguage.EN -> "Rate & Review Work"
    }

    fun reviewsHeader(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "تقييمات وآراء المستعملين"
        AppLanguage.FR -> "Avis et évaluations des clients"
        AppLanguage.EN -> "User Reviews & Ratings"
    }

    fun addWorkerButton(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "تسجيل حرفي جديد"
        AppLanguage.FR -> "Inscrire un artisan"
        AppLanguage.EN -> "Register a Worker"
    }

    fun bookmarksTab(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "المفضلة"
        AppLanguage.FR -> "Favoris"
        AppLanguage.EN -> "Saved"
    }

    fun allWorkersTab(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "دليل الحرفيين"
        AppLanguage.FR -> "Tous les artisans"
        AppLanguage.EN -> "All Craftsmen"
    }

    fun verifiedBadge(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "حرفي موثوق"
        AppLanguage.FR -> "Artisan Vérifié"
        AppLanguage.EN -> "Verified Craftsman"
    }

    fun yearsExpFormat(lang: AppLanguage, years: Int): String = when (lang) {
        AppLanguage.AR -> "$years سنوات خبرة"
        AppLanguage.FR -> "$years ans d'expérience"
        AppLanguage.EN -> "$years yrs experience"
    }

    fun dailyRateFormat(lang: AppLanguage, rateDzd: Int): String = when (lang) {
        AppLanguage.AR -> "$rateDzd دج / اليوم"
        AppLanguage.FR -> "$rateDzd DA / jour"
        AppLanguage.EN -> "$rateDzd DZD / day"
    }

    fun scoreOutOfTenFormat(score: Double): String = String.format("%.1f / 10", score)

    fun scoreRatingText(lang: AppLanguage, score: Double): String {
        return when {
            score >= 9.5 -> when (lang) {
                AppLanguage.AR -> "ممتاز جداً"
                AppLanguage.FR -> "Excellent"
                AppLanguage.EN -> "Outstanding"
            }
            score >= 8.5 -> when (lang) {
                AppLanguage.AR -> "جيد جداً"
                AppLanguage.FR -> "Très bon"
                AppLanguage.EN -> "Very Good"
            }
            score >= 7.0 -> when (lang) {
                AppLanguage.AR -> "جيد"
                AppLanguage.FR -> "Bon"
                AppLanguage.EN -> "Good"
            }
            else -> when (lang) {
                AppLanguage.AR -> "متوسط"
                AppLanguage.FR -> "Moyen"
                AppLanguage.EN -> "Average"
            }
        }
    }

    fun noWorkersFound(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "لم نجد حرفيين طابقوا خيارات البحث الحالية"
        AppLanguage.FR -> "Aucun artisan ne correspond aux critères de recherche"
        AppLanguage.EN -> "No craftsmen matched your current filters"
    }

    fun clearFilters(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "إعادة ضبط التصفية"
        AppLanguage.FR -> "Réinitialiser les filtres"
        AppLanguage.EN -> "Reset Filters"
    }

    fun addRatingTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "تقييم عمل الحرفي بعد الإنجاز"
        AppLanguage.FR -> "Évaluer le travail de l'artisan"
        AppLanguage.EN -> "Rate Craftsman After Job"
    }

    fun scoreLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "العلامة الإجمالية (من 10)"
        AppLanguage.FR -> "Note globale (sur 10)"
        AppLanguage.EN -> "Overall Score (out of 10)"
    }

    fun qualityLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "جودة الإتقان والنظافة"
        AppLanguage.FR -> "Qualité & Finition"
        AppLanguage.EN -> "Work Finish & Quality"
    }

    fun punctualityLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "دقة واحترام المواعيد"
        AppLanguage.FR -> "Ponctualité"
        AppLanguage.EN -> "Punctuality"
    }

    fun priceLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "عدالة ومناسبة السعر"
        AppLanguage.FR -> "Équité du prix"
        AppLanguage.EN -> "Fairness of Price"
    }

    fun commentLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "ملاحظاتك وانطباعك عن الخدمة"
        AppLanguage.FR -> "Votre commentaire sur le travail"
        AppLanguage.EN -> "Your review notes"
    }

    fun submit(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "حفظ ونشر التقييم"
        AppLanguage.FR -> "Enregistrer l'avis"
        AppLanguage.EN -> "Submit Rating"
    }

    fun cancel(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "إلغاء"
        AppLanguage.FR -> "Annuler"
        AppLanguage.EN -> "Cancel"
    }

    fun registerWorkerTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "تسجيل حرفي جديد في القاعدة"
        AppLanguage.FR -> "Ajouter un nouvel artisan"
        AppLanguage.EN -> "Register New Worker"
    }

    fun fullName(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "الاسم الكامل"
        AppLanguage.FR -> "Nom complet"
        AppLanguage.EN -> "Full Name"
    }

    fun phoneLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "رقم الهاتف (مثل 0661...)"
        AppLanguage.FR -> "Numéro de téléphone"
        AppLanguage.EN -> "Phone Number"
    }

    fun whatsappLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "رقم الواتساب"
        AppLanguage.FR -> "Numéro WhatsApp"
        AppLanguage.EN -> "WhatsApp Number"
    }

    fun communeLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "البلدية / الحي"
        AppLanguage.FR -> "Commune / Quartier"
        AppLanguage.EN -> "Commune / Neighborhood"
    }

    fun descriptionLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "وصف الحرفة والتخصصات"
        AppLanguage.FR -> "Description & Spécialités"
        AppLanguage.EN -> "Description & Specialties"
    }

    fun wilayaFilterLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "الولاية"
        AppLanguage.FR -> "Wilaya"
        AppLanguage.EN -> "Wilaya"
    }

    fun dailyRateLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "السعر اليومي (دج)"
        AppLanguage.FR -> "Tarif jour (DA)"
        AppLanguage.EN -> "Daily Rate (DZD)"
    }

    fun experienceLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "سنوات الخبرة"
        AppLanguage.FR -> "Années d'expérience"
        AppLanguage.EN -> "Years of Experience"
    }

    fun skillsLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "المهارات (مفصولة بفواصل)"
        AppLanguage.FR -> "Compétences (séparées par des virgules)"
        AppLanguage.EN -> "Skills (separated by commas)"
    }

    // AUTH LOCALIZATION
    fun selectRolePrompt(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "أنا أريد التسجيل بصفة:"
        AppLanguage.FR -> "Je souhaite m'inscrire en tant que :"
        AppLanguage.EN -> "I want to register as:"
    }

    // Short labels keep role cards readable on narrow phones and in RTL layouts.
    fun clientRole(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "زبون"
        AppLanguage.FR -> "Client"
        AppLanguage.EN -> "Client"
    }

    fun craftsmanRole(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "حرفي"
        AppLanguage.FR -> "Artisan"
        AppLanguage.EN -> "Craftsman"
    }

    fun loginTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "تسجيل دخول المستعمل"
        AppLanguage.FR -> "Connexion Utilisateur"
        AppLanguage.EN -> "User Login"
    }

    fun signUpTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "إنشاء حساب مستعمل جديد"
        AppLanguage.FR -> "Créer un compte client"
        AppLanguage.EN -> "Create User Account"
    }

    fun emailLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "البريد الإلكتروني (مثال: client@gmail.com)"
        AppLanguage.FR -> "Adresse Email"
        AppLanguage.EN -> "Email Address"
    }

    fun passwordLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "كلمة المرور (8 أحرف/أرقام على الأقل)"
        AppLanguage.FR -> "Mot de passe"
        AppLanguage.EN -> "Password"
    }

    fun loginButton(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "دخول"
        AppLanguage.FR -> "Se connecter"
        AppLanguage.EN -> "Sign In"
    }

    fun signUpButton(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "إنشاء الحساب"
        AppLanguage.FR -> "S'inscrire"
        AppLanguage.EN -> "Sign Up"
    }

    fun noAccountPrompt(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "ليس لديك حساب؟ اضغط لإنشاء حساب جديد"
        AppLanguage.FR -> "Pas encore de compte ? Cliquez pour vous inscrire"
        AppLanguage.EN -> "Don't have an account? Tap to sign up"
    }

    fun haveAccountPrompt(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "لديك حساب بالفعل؟ اضغط لتسجيل الدخول"
        AppLanguage.FR -> "Vous avez déjà un compte ? Se connecter"
        AppLanguage.EN -> "Already have an account? Tap to sign in"
    }

    fun accountProfileTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "حساب المستعمل"
        AppLanguage.FR -> "Mon Compte"
        AppLanguage.EN -> "My Account"
    }

    fun logoutButton(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "تسجيل الخروج"
        AppLanguage.FR -> "Déconnexion"
        AppLanguage.EN -> "Sign Out"
    }

    fun guestUser(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "مستعمل زائر"
        AppLanguage.FR -> "Invité"
        AppLanguage.EN -> "Guest User"
    }

    fun loginRequiredToRateNotice(lang: AppLanguage): String = when (lang) {
        AppLanguage.AR -> "لضمان مصداقية التقييمات، يتوجب عليك تسجيل الدخول أو إنشاء حساب بسيط أولاً لإضافة تقييمك للحرفي."
        AppLanguage.FR -> "Afin de garantir la crédibilité des avis, veuillez vous connecter ou créer un compte avant de noter cet artisan."
        AppLanguage.EN -> "To ensure review authenticity, please log in or create an account before rating this craftsman."
    }

    fun authErrorMessage(lang: AppLanguage, key: String): String = when (key) {
        "error_name_empty" -> when (lang) {
            AppLanguage.AR -> "يرجى كتابة الاسم الكامل"
            AppLanguage.FR -> "Veuillez entrer votre nom complet"
            AppLanguage.EN -> "Please enter your full name"
        }
        "error_invalid_email" -> when (lang) {
            AppLanguage.AR -> "يرجى كتابة بريد إلكتروني صحيح"
            AppLanguage.FR -> "Adresse email invalide"
            AppLanguage.EN -> "Invalid email address"
        }
        "error_password_too_short" -> when (lang) {
            AppLanguage.AR -> "كلمة المرور يجب أن تكون 8 أحرف/أرقام على الأقل"
            AppLanguage.FR -> "Mot de passe trop court (min 8 caractères)"
            AppLanguage.EN -> "Password must be at least 8 characters"
        }
        "error_password_empty" -> when (lang) {
            AppLanguage.AR -> "يرجى كتابة كلمة المرور"
            AppLanguage.FR -> "Veuillez saisir votre mot de passe"
            AppLanguage.EN -> "Please enter your password"
        }
        "error_email_already_registered" -> when (lang) {
            AppLanguage.AR -> "هذا البريد مرتبط بحساب موجود أو بمحاولة تسجيل سابقة. أكّد البريد من الرسالة ثم سجّل الدخول."
            AppLanguage.FR -> "Cet email est déjà associé à un compte ou à une inscription précédente. Confirmez l'email puis connectez-vous."
            AppLanguage.EN -> "This email is linked to an existing account or a previous sign-up. Confirm the email, then log in."
        }
        "error_user_not_found" -> when (lang) {
            AppLanguage.AR -> "لم نجد حساباً بهذا البريد الإلكتروني"
            AppLanguage.FR -> "Compte non trouvé pour cet email"
            AppLanguage.EN -> "No account found with this email"
        }
        "error_incorrect_password" -> when (lang) {
            AppLanguage.AR -> "كلمة المرور غير صحيحة"
            AppLanguage.FR -> "Mot de passe incorrect"
            AppLanguage.EN -> "Incorrect password"
        }
        "error_auth_network" -> when (lang) {
            AppLanguage.AR -> "تعذر الاتصال بخدمة الحسابات. تحقق من الإنترنت وحاول مرة أخرى."
            AppLanguage.FR -> "Connexion au service de comptes impossible. Vérifiez Internet et réessayez."
            AppLanguage.EN -> "Unable to connect to the account service. Check your internet connection and try again."
        }
        "error_email_confirmation_required" -> when (lang) {
            AppLanguage.AR -> "تم إنشاء الحساب. يرجى تأكيد بريدك الإلكتروني قبل تسجيل الدخول."
            AppLanguage.FR -> "Compte créé. Confirmez votre adresse email avant de vous connecter."
            AppLanguage.EN -> "Account created. Confirm your email address before signing in."
        }
        "email_confirmation_sent" -> when (lang) {
            AppLanguage.AR -> "تم إنشاء الحساب وإرسال رسالة تأكيد إلى بريدك الإلكتروني. افتح الرسالة ثم سجّل الدخول."
            AppLanguage.FR -> "Compte créé. Un email de confirmation a été envoyé. Ouvrez-le puis connectez-vous."
            AppLanguage.EN -> "Account created. A confirmation email was sent. Open it, then sign in."
        }
        "error_auth_rate_limited" -> when (lang) {
            AppLanguage.AR -> "تمت محاولات كثيرة. انتظر قليلاً ثم حاول تسجيل الدخول مرة أخرى."
            AppLanguage.FR -> "Trop de tentatives. Attendez un moment puis réessayez."
            AppLanguage.EN -> "Too many attempts. Please wait a moment and try again."
        }
        "error_signup_rate_limited" -> when (lang) {
            AppLanguage.AR -> "تم تجاوز عدد محاولات إنشاء الحساب. انتظر قليلاً ثم حاول مرة أخرى."
            AppLanguage.FR -> "Trop de tentatives de création de compte. Attendez un moment puis réessayez."
            AppLanguage.EN -> "Too many account creation attempts. Please wait a moment and try again."
        }
        "error_auth_server" -> when (lang) {
            AppLanguage.AR -> "خدمة الحسابات تواجه مشكلة مؤقتة. حاول مرة أخرى بعد قليل."
            AppLanguage.FR -> "Le service de comptes rencontre un problème temporaire. Réessayez plus tard."
            AppLanguage.EN -> "The account service is temporarily unavailable. Please try again later."
        }
        "error_auth_unknown" -> when (lang) {
            AppLanguage.AR -> "تعذر إتمام العملية بسبب خطأ غير متوقع. حاول مرة أخرى."
            AppLanguage.FR -> "Opération impossible en raison d'une erreur inattendue. Réessayez."
            AppLanguage.EN -> "The operation failed due to an unexpected error. Please try again."
        }
        else -> key
    }


    // GENERATED MULTI-LANGUAGE UI LOOKUP (v1.7.0 audit)
    object Ui {
        fun text(key: String, lang: AppLanguage, vararg args: Pair<String, String>): String {
            val raw = when (key) {
                "status_open" -> when (lang) { AppLanguage.AR -> "جديد"; AppLanguage.FR -> "Nouveau"; AppLanguage.EN -> "New" }
                "status_quoted" -> when (lang) { AppLanguage.AR -> "وصل عرض سعر"; AppLanguage.FR -> "Devis reçu"; AppLanguage.EN -> "Quote received" }
                "status_accepted" -> when (lang) { AppLanguage.AR -> "مقبول"; AppLanguage.FR -> "Accepté"; AppLanguage.EN -> "Accepted" }
                "status_in_progress" -> when (lang) { AppLanguage.AR -> "قيد التنفيذ"; AppLanguage.FR -> "En cours"; AppLanguage.EN -> "In progress" }
                "status_completed" -> when (lang) { AppLanguage.AR -> "مكتمل"; AppLanguage.FR -> "Terminé"; AppLanguage.EN -> "Completed" }
                "status_cancelled" -> when (lang) { AppLanguage.AR -> "ملغي"; AppLanguage.FR -> "Annulé"; AppLanguage.EN -> "Cancelled" }
                "new_badge" -> when (lang) { AppLanguage.AR -> "جديد"; AppLanguage.FR -> "Nouveau"; AppLanguage.EN -> "New" }
                "not_rated_yet" -> when (lang) { AppLanguage.AR -> "لم يقيّم بعد (كن أول من يقيّمه)"; AppLanguage.FR -> "Pas encore noté (soyez le premier !)"; AppLanguage.EN -> "Not rated yet (be the first to rate)" }
                "review_count_word" -> when (lang) { AppLanguage.AR -> "تقييم"; AppLanguage.FR -> "avis"; AppLanguage.EN -> "reviews" }
                "rate_button" -> when (lang) { AppLanguage.AR -> "قيّم"; AppLanguage.FR -> "Noter"; AppLanguage.EN -> "Rate" }
                "rating_word" -> when (lang) { AppLanguage.AR -> "تقييم"; AppLanguage.FR -> "Avis"; AppLanguage.EN -> "Review" }
                "request_service_button" -> when (lang) { AppLanguage.AR -> "طلب خدمة من هذا الحرفي"; AppLanguage.FR -> "Demander un service à cet artisan"; AppLanguage.EN -> "Request service from this craftsman" }
                "about_header" -> when (lang) { AppLanguage.AR -> "عن الحرفي والخدمات"; AppLanguage.FR -> "À propos de l'artisan et des services"; AppLanguage.EN -> "About the craftsman & services" }
                "daily_rate_header" -> when (lang) { AppLanguage.AR -> "الأجر اليومي"; AppLanguage.FR -> "Tarif journalier"; AppLanguage.EN -> "Daily rate" }
                "experience_header" -> when (lang) { AppLanguage.AR -> "الخبرة الميدانية"; AppLanguage.FR -> "Expérience terrain"; AppLanguage.EN -> "Field experience" }
                "skills_header" -> when (lang) { AppLanguage.AR -> "التخصصات والمهارات"; AppLanguage.FR -> "Spécialités & compétences"; AppLanguage.EN -> "Specialties & skills" }
                "no_reviews_placeholder" -> when (lang) { AppLanguage.AR -> "لا توجد تقييمات بعد. كن أول من يقيّم عمل هذا الحرفي!"; AppLanguage.FR -> "Aucun avis pour l'instant. Soyez le premier à évaluer ce travail !"; AppLanguage.EN -> "No reviews yet. Be the first to rate this work!" }
                "phone_label" -> when (lang) { AppLanguage.AR -> "الهاتف: "; AppLanguage.FR -> "Téléphone : "; AppLanguage.EN -> "Phone: " }
                "quick_on_time" -> when (lang) { AppLanguage.AR -> "دقة المواعيد"; AppLanguage.FR -> "Ponctualité"; AppLanguage.EN -> "On time" }
                "quick_quality" -> when (lang) { AppLanguage.AR -> "عمل متقن"; AppLanguage.FR -> "Travail soigné"; AppLanguage.EN -> "Quality work" }
                "quick_fair_price" -> when (lang) { AppLanguage.AR -> "سعر عادل"; AppLanguage.FR -> "Prix juste"; AppLanguage.EN -> "Fair price" }
                "quick_clean" -> when (lang) { AppLanguage.AR -> "نظافة الموقع"; AppLanguage.FR -> "Chantier propre"; AppLanguage.EN -> "Clean worksite" }
                "quick_polite" -> when (lang) { AppLanguage.AR -> "احترام وأخلاق"; AppLanguage.FR -> "Respect & courtoisie"; AppLanguage.EN -> "Respectful" }
                "quick_professional" -> when (lang) { AppLanguage.AR -> "سريع ومحترف"; AppLanguage.FR -> "Rapide et professionnel"; AppLanguage.EN -> "Fast & professional" }
                "tags_label" -> when (lang) { AppLanguage.AR -> "كلمات دلالية للخدمة"; AppLanguage.FR -> "Mots-clés du service"; AppLanguage.EN -> "Service tags" }
                "photos_count" -> when (lang) { AppLanguage.AR -> "صور (${'$'}{size}/${'$'}{max})"; AppLanguage.FR -> "Photos (${'$'}{size}/${'$'}{max})"; AppLanguage.EN -> "Photos (${'$'}{size}/${'$'}{max})" }
                "add_photos_label" -> when (lang) { AppLanguage.AR -> "إضافة صور (اختياري، حتى 3)"; AppLanguage.FR -> "Ajouter des photos (facultatif, 3 max)"; AppLanguage.EN -> "Add photos (optional, up to 3)" }
                "request_dialog_title" -> when (lang) { AppLanguage.AR -> "طلب خدمة من ${'$'}{name}"; AppLanguage.FR -> "Demande de service à ${'$'}{name}"; AppLanguage.EN -> "Service request from ${'$'}{name}" }
                "describe_placeholder" -> when (lang) { AppLanguage.AR -> "صف المشكلة أو الخدمة المطلوبة"; AppLanguage.FR -> "Décrivez le problème ou le service demandé"; AppLanguage.EN -> "Describe the problem or required service" }
                "send_request_button" -> when (lang) { AppLanguage.AR -> "إرسال الطلب"; AppLanguage.FR -> "Envoyer la demande"; AppLanguage.EN -> "Submit request" }
                "uploading_photos" -> when (lang) { AppLanguage.AR -> "جاري رفع الصور..."; AppLanguage.FR -> "Envoi des photos..."; AppLanguage.EN -> "Uploading photos..." }
                "connection_lost_photos" -> when (lang) { AppLanguage.AR -> "تعذر الاتصال؛ سترفع الصور تلقائيًا عند عودة الإنترنت"; AppLanguage.FR -> "Connexion impossible ; les photos seront envoyées automatiquement dès le retour d'Internet"; AppLanguage.EN -> "Could not connect; photos will upload automatically when the internet returns" }
                "commune_label" -> when (lang) { AppLanguage.AR -> "البلدية"; AppLanguage.FR -> "Commune"; AppLanguage.EN -> "Commune" }
                "description_hint" -> when (lang) { AppLanguage.AR -> "اكتب وصفًا بين 10 و2000 حرف"; AppLanguage.FR -> "Écrivez une description entre 10 et 2000 caractères"; AppLanguage.EN -> "Write a description between 10 and 2000 characters" }
                "cancel_button" -> when (lang) { AppLanguage.AR -> "إلغاء"; AppLanguage.FR -> "Annuler"; AppLanguage.EN -> "Cancel" }
                "my_requests_title" -> when (lang) { AppLanguage.AR -> "طلباتي"; AppLanguage.FR -> "Mes demandes"; AppLanguage.EN -> "My requests" }
                "pending_sync_banner" -> when (lang) { AppLanguage.AR -> "${'$'}{count} طلب بانتظار المزامنة"; AppLanguage.FR -> "${'$'}{count} demande en attente de synchronisation"; AppLanguage.EN -> "${'$'}{count} request(s) pending sync" }
                "no_requests_yet" -> when (lang) { AppLanguage.AR -> "لا توجد طلبات بعد"; AppLanguage.FR -> "Aucune demande pour l'instant"; AppLanguage.EN -> "No requests yet" }
                "close_button" -> when (lang) { AppLanguage.AR -> "إغلاق"; AppLanguage.FR -> "Fermer"; AppLanguage.EN -> "Close" }
                "retry_button" -> when (lang) { AppLanguage.AR -> "إعادة"; AppLanguage.FR -> "Réessayer"; AppLanguage.EN -> "Retry" }
                "unsynced_label" -> when (lang) { AppLanguage.AR -> "لم تتم المزامنة"; AppLanguage.FR -> "Non synchronisé"; AppLanguage.EN -> "Unsynced" }
                "waiting_sync_label" -> when (lang) { AppLanguage.AR -> "بانتظار المزامنة"; AppLanguage.FR -> "En attente de synchronisation"; AppLanguage.EN -> "Waiting for sync" }
                "customer_label" -> when (lang) { AppLanguage.AR -> "العميل: "; AppLanguage.FR -> "Client : "; AppLanguage.EN -> "Customer: " }
                "craftsman_label" -> when (lang) { AppLanguage.AR -> "الحرفي: "; AppLanguage.FR -> "Artisan : "; AppLanguage.EN -> "Craftsman: " }
                "send_quote_action" -> when (lang) { AppLanguage.AR -> "أرسل تسعير"; AppLanguage.FR -> "Envoyer un devis"; AppLanguage.EN -> "Send quote" }
                "reject_action" -> when (lang) { AppLanguage.AR -> "رفض"; AppLanguage.FR -> "Refuser"; AppLanguage.EN -> "Reject" }
                "confirm_accept_action" -> when (lang) { AppLanguage.AR -> "تأكيد القبول"; AppLanguage.FR -> "Confirmer l'acceptation"; AppLanguage.EN -> "Confirm acceptance" }
                "start_work_action" -> when (lang) { AppLanguage.AR -> "بدء التنفيذ"; AppLanguage.FR -> "Commencer les travaux"; AppLanguage.EN -> "Start work" }
                "complete_work_action" -> when (lang) { AppLanguage.AR -> "إكمال"; AppLanguage.FR -> "Terminer"; AppLanguage.EN -> "Complete" }
                "craftsman_name_placeholder" -> when (lang) { AppLanguage.AR -> "اسم الحرفي / الورشة بالكامل"; AppLanguage.FR -> "Nom complet de l'artisan / atelier"; AppLanguage.EN -> "Full craftsman / workshop name" }
                "choose_trade_placeholder" -> when (lang) { AppLanguage.AR -> "اختر الحرفة / التخصص:"; AppLanguage.FR -> "Choisir le métier / la spécialité :"; AppLanguage.EN -> "Choose trade / specialty:" }
                "whatsapp_optional_label" -> when (lang) { AppLanguage.AR -> "رقم الواتساب (اختياري)"; AppLanguage.FR -> "Numéro WhatsApp (facultatif)"; AppLanguage.EN -> "WhatsApp number (optional)" }
                "sending_button" -> when (lang) { AppLanguage.AR -> "جارٍ الإرسال..."; AppLanguage.FR -> "Envoi en cours..."; AppLanguage.EN -> "Sending..." }
                "register_as_craftsman_button" -> when (lang) { AppLanguage.AR -> "تسجيل كحرفي جديد 🛠️"; AppLanguage.FR -> "S'inscrire comme artisan 🛠️"; AppLanguage.EN -> "Register as new craftsman 🛠️" }
                "verified_craftsman_badge" -> when (lang) { AppLanguage.AR -> "حساب حرفي معتمد 🛠️"; AppLanguage.FR -> "Compte Artisan 🛠️"; AppLanguage.EN -> "Verified craftsman account 🛠️" }
                "verified_client_badge" -> when (lang) { AppLanguage.AR -> "حساب زبون مسجل 👤"; AppLanguage.FR -> "Compte Client 👤"; AppLanguage.EN -> "Registered client account 👤" }
                "home_search_subtitle" -> when (lang) { AppLanguage.AR -> "ابحث عن أفضل الحرفيين والعمال في الجزائر"; AppLanguage.FR -> "Trouvez les meilleurs artisans et ouvriers en Algérie"; AppLanguage.EN -> "Find the best craftsmen & workers in Algeria" }
                "home_rating_subtitle" -> when (lang) { AppLanguage.AR -> "تقييم حقيقي 10/10 لاتخاذ القرار الصحيح لأعمالك"; AppLanguage.FR -> "Notation réelle 10/10 pour choisir en toute confiance"; AppLanguage.EN -> "Real 10/10 ratings to make the right choice for your projects" }
                "synced_banner" -> when (lang) { AppLanguage.AR -> "تمت مزامنة ${'$'}{count} طلب"; AppLanguage.FR -> "${'$'}{count} demande(s) synchronisée(s)"; AppLanguage.EN -> "${'$'}{count} request(s) synced" }
                "pending_local_banner" -> when (lang) { AppLanguage.AR -> "توجد طلبات محلية بانتظار الاتصال"; AppLanguage.FR -> "Des demandes locales attendent la connexion"; AppLanguage.EN -> "Local requests are waiting for a connection" }
                "status_updated_ok" -> when (lang) { AppLanguage.AR -> "تم تحديث حالة الطلب"; AppLanguage.FR -> "État de la demande mis à jour"; AppLanguage.EN -> "Request status updated" }
                "status_updated_fail" -> when (lang) { AppLanguage.AR -> "تعذر تحديث حالة الطلب"; AppLanguage.FR -> "Impossible de mettre à jour l'état de la demande"; AppLanguage.EN -> "Could not update request status" }
                "update_success_count" -> when (lang) { AppLanguage.AR -> "تم تحديث ${'$'}{count} حرفي من الخادم"; AppLanguage.FR -> "${'$'}{count} artisan(s) mis à jour depuis le serveur"; AppLanguage.EN -> "${'$'}{count} craftsman record(s) updated from the server" }
                "update_failed_local" -> when (lang) { AppLanguage.AR -> "تعذر تحديث البيانات، يتم عرض النسخة المحلية"; AppLanguage.FR -> "Impossible de mettre à jour les données ; affichage de la version locale"; AppLanguage.EN -> "Could not refresh data; showing local version" }
                "new_request_title" -> when (lang) { AppLanguage.AR -> "طلب خدمة جديد!"; AppLanguage.FR -> "Nouvelle demande de service !"; AppLanguage.EN -> "New service request!" }
                "new_request_body" -> when (lang) { AppLanguage.AR -> "لديك طلب خدمة جديد بانتظار ردك. افتح التطبيق للاطلاع على التفاصيل."; AppLanguage.FR -> "Vous avez une nouvelle demande de service en attente de votre réponse. Ouvrez l'application pour plus de détails."; AppLanguage.EN -> "You have a new service request waiting for your reply. Open the app for details." }
                "status_change_title" -> when (lang) { AppLanguage.AR -> "تحديث في طلباتك"; AppLanguage.FR -> "Mise à jour de vos demandes"; AppLanguage.EN -> "Update on your requests" }
                "status_change_body" -> when (lang) { AppLanguage.AR -> "حالة طلبك تغيّرت إلى: ${'$'}{status}. افتح التطبيق للاطلاع."; AppLanguage.FR -> "L'état de votre demande a changé en : ${'$'}{status}. Ouvrez l'application pour plus de détails."; AppLanguage.EN -> "Your request status changed to: ${'$'}{status}. Open the app for details." }
                "requests_update_title" -> when (lang) { AppLanguage.AR -> "تحديث جديد على طلباتك"; AppLanguage.FR -> "Mise à jour de vos demandes"; AppLanguage.EN -> "New update on your requests" }
                "requests_update_body" -> when (lang) { AppLanguage.AR -> "تحديث جديد على طلباتك. افتح التطبيق للاطلاع."; AppLanguage.FR -> "Mise à jour de vos demandes. Ouvrez l'application pour plus de détails."; AppLanguage.EN -> "New update on your requests. Open the app to check." }
                "request_sent_ok" -> when (lang) { AppLanguage.AR -> "تم إرسال طلب الخدمة إلى الحرفي"; AppLanguage.FR -> "Demande de service envoyée à l'artisan"; AppLanguage.EN -> "Service request sent to the craftsman" }
                "request_saved_local" -> when (lang) { AppLanguage.AR -> "تم حفظ الطلب محليًا وسيتم إرساله عند توفر الاتصال"; AppLanguage.FR -> "Demande enregistrée localement ; elle sera envoyée dès la disponibilité de la connexion"; AppLanguage.EN -> "Request saved locally and will be sent when the connection is available" }
                "login_required" -> when (lang) { AppLanguage.AR -> "يجب تسجيل الدخول لإرسال طلب خدمة"; AppLanguage.FR -> "Connexion requise pour envoyer une demande de service"; AppLanguage.EN -> "Sign in is required to send a service request" }
                "login_success" -> when (lang) { AppLanguage.AR -> "تم تسجيل الدخول بنجاح! مرحباً ${'$'}{name}"; AppLanguage.FR -> "Connexion réussie ! Bienvenue ${'$'}{name}"; AppLanguage.EN -> "Signed in successfully! Welcome ${'$'}{name}" }
                "client_created_success" -> when (lang) { AppLanguage.AR -> "تم إنشاء حساب الزبون بنجاح! مرحباً ${'$'}{name}"; AppLanguage.FR -> "Compte client créé avec succès ! Bienvenue ${'$'}{name}"; AppLanguage.EN -> "Client account created! Welcome ${'$'}{name}" }
                "profile_saved_local" -> when (lang) { AppLanguage.AR -> "تم إنشاء الحساب وحفظ الملف محليًا، لكن تعذر رفعه للسحابة وسيعادَت المزامنة لاحقًا"; AppLanguage.FR -> "Compte créé et profil enregistré localement ; impossible de l'envoyer au serveur pour l'instant, resynchronisation à venir"; AppLanguage.EN -> "Account created and profile saved locally; upload to the cloud failed and will retry later" }
                "craftsman_registered_success" -> when (lang) { AppLanguage.AR -> "تم تسجيلك كحرفي بنجاح! ملفك الحرفي متاح في دليل Herafi DZ"; AppLanguage.FR -> "Inscription réussie comme artisan ! Votre profil est disponible dans le guide Herafi DZ"; AppLanguage.EN -> "Successfully registered as a craftsman! Your profile is available in the Herafi DZ directory" }
                "profile_sync_failed" -> when (lang) { AppLanguage.AR -> "تعذر مزامنة ملفك الحرفي، يتم الاحتفاظ بالنسخة المحلية"; AppLanguage.FR -> "Impossible de synchroniser votre profil artisan ; la version locale est conservée"; AppLanguage.EN -> "Could not sync your craftsman profile; keeping the local version" }
                "logged_out" -> when (lang) { AppLanguage.AR -> "تم تسجيل الخروج"; AppLanguage.FR -> "Déconnexion réussie"; AppLanguage.EN -> "Signed out" }
                "password_changed_ok" -> when (lang) { AppLanguage.AR -> "تم تغيير كلمة المرور بنجاح"; AppLanguage.FR -> "Mot de passe changé avec succès"; AppLanguage.EN -> "Password changed successfully" }
                "password_changed_fail" -> when (lang) { AppLanguage.AR -> "تعذر تغيير كلمة المرور. تحقق من اتصالك بالإنترنت وحاول مرة أخرى"; AppLanguage.FR -> "Impossible de changer le mot de passe. Vérifiez votre connexion et réessayez"; AppLanguage.EN -> "Could not change the password. Check your internet connection and try again" }
                "craftsman_word" -> when (lang) { AppLanguage.AR -> "حرفي"; AppLanguage.FR -> "Artisan"; AppLanguage.EN -> "Craftsman" }
                "availability_local" -> when (lang) { AppLanguage.AR -> "تم تحديث التوفر محليًا؛ تعذر مزامنته مع الخادم"; AppLanguage.FR -> "Disponibilité mise à jour localement ; impossible de synchroniser avec le serveur"; AppLanguage.EN -> "Availability updated locally; could not sync with the server" }
                "now_available" -> when (lang) { AppLanguage.AR -> "أصبحت متاحًا لاستقبال الطلبات"; AppLanguage.FR -> "Vous êtes désormais disponible pour recevoir des demandes"; AppLanguage.EN -> "You are now available to receive requests" }
                "now_unavailable" -> when (lang) { AppLanguage.AR -> "أصبحت غير متاح مؤقتًا"; AppLanguage.FR -> "Vous êtes temporairement indisponible"; AppLanguage.EN -> "You are now temporarily unavailable" }
                "profile_edits_local" -> when (lang) { AppLanguage.AR -> "تم حفظ التعديلات محليًا؛ تعذر مزامنتها مع الخادم"; AppLanguage.FR -> "Modifications enregistrées localement ; impossible de synchroniser avec le serveur"; AppLanguage.EN -> "Edits saved locally; could not sync with the server" }
                "profile_updated_ok" -> when (lang) { AppLanguage.AR -> "تم تحديث ملفك الحرفي بنجاح"; AppLanguage.FR -> "Votre profil artisan a été mis à jour avec succès"; AppLanguage.EN -> "Your craftsman profile was updated successfully" }
                "profile_reset_ok" -> when (lang) { AppLanguage.AR -> "تم إعادة تعيين ملفك الحرفي إلى الحالة الأولية"; AppLanguage.FR -> "Votre profil artisan a été réinitialisé"; AppLanguage.EN -> "Your craftsman profile was reset to its initial state" }
                "cannot_rate_self" -> when (lang) { AppLanguage.AR -> "لا يمكنك تقييم ملفك الخاص"; AppLanguage.FR -> "Vous ne pouvez pas évaluer votre propre profil"; AppLanguage.EN -> "You cannot rate your own profile" }
                "rating_added_ok" -> when (lang) { AppLanguage.AR -> "تمت إضافة تقييمك بنجاح! شكراً لك."; AppLanguage.FR -> "Votre avis a été ajouté avec succès ! Merci."; AppLanguage.EN -> "Your rating was added successfully! Thank you." }
                "worker_registered_ok" -> when (lang) { AppLanguage.AR -> "تم تسجيل الحرفي بنجاح وإضافته إلى الدليل!"; AppLanguage.FR -> "Artisan enregistré avec succès et ajouté au guide !"; AppLanguage.EN -> "Craftsman registered and added to the directory!" }
                "new_notification_title" -> when (lang) { AppLanguage.AR -> "تحديثات طلبات الخدمة"; AppLanguage.FR -> "Mises à jour des demandes de service"; AppLanguage.EN -> "Service request updates" }
                "new_notification_body" -> when (lang) { AppLanguage.AR -> "إشعار عند وصول طلب خدمة جديد أو تغيّر حالة طلبك"; AppLanguage.FR -> "Alerte en cas de nouvelle demande de service ou de changement d'état de votre demande"; AppLanguage.EN -> "Notification when a new service request arrives or your request status changes" }
                "settings_success_suffix" -> when (lang) { AppLanguage.AR -> " بنجاح"; AppLanguage.FR -> " avec succès"; AppLanguage.EN -> " successfully" }
                "wilaya_default" -> when (lang) { AppLanguage.AR -> "الجزائر العاصمة"; AppLanguage.FR -> "Alger"; AppLanguage.EN -> "Algiers" }
                else -> key
            }
            var result = raw
            for ((placeholder, value) in args) {
                result = result.replace("$" + placeholder, value).replace("$" + "{" + placeholder + "}", value)
            }
            return result
        }
    }

    // GENERATED SETTINGS CONVENIENCE FUNCTIONS (v1.7.0 audit)
    fun settingsTitle(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "الإعدادات"; AppLanguage.FR -> "Paramètres"; AppLanguage.EN -> "Settings" }
    fun settingsClose(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "إغلاق"; AppLanguage.FR -> "Fermer"; AppLanguage.EN -> "Close" }
    fun craftsmanClientHint(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "أنت مسجّل كزبون. يمكنك التسجيل كحرفي للحصول على خيارات إدارة ملفك."; AppLanguage.FR -> "Vous êtes connecté en tant que client. Inscrivez-vous comme artisan pour gérer votre profil."; AppLanguage.EN -> "You are signed in as a client. Register as a craftsman to manage your profile." }
    fun accountSectionTitle(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "الحساب والأمان"; AppLanguage.FR -> "Compte et sécurité"; AppLanguage.EN -> "Account & security" }
    fun accountInfoLine(lang: AppLanguage, name: String, email: String): String = when (lang) { AppLanguage.AR -> "الاسم: %s — البريد: %s".replace("%s", name).replace("%s", email); AppLanguage.FR -> "Nom : %s — E-mail : %s".replace("%s", name).replace("%s", email); AppLanguage.EN -> "Name: %s — Email: %s".replace("%s", name).replace("%s", email) }
    fun newPasswordLabel(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "كلمة المرور الجديدة"; AppLanguage.FR -> "Nouveau mot de passe"; AppLanguage.EN -> "New password" }
    fun newPasswordPlaceholder(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "أدخل كلمة مرور قوية"; AppLanguage.FR -> "Entrez un mot de passe fort"; AppLanguage.EN -> "Enter a strong password" }
    fun confirmPasswordLabel(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "تأكيد كلمة المرور"; AppLanguage.FR -> "Confirmer le mot de passe"; AppLanguage.EN -> "Confirm password" }
    fun passwordTooShortError(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "كلمة المرور يجب أن تكون 8 أحرف على الأقل"; AppLanguage.FR -> "Le mot de passe doit contenir au moins 8 caractères"; AppLanguage.EN -> "Password must be at least 8 characters" }
    fun passwordMismatchError(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "كلمتا المرور غير متطابقتين"; AppLanguage.FR -> "Les mots de passe ne correspondent pas"; AppLanguage.EN -> "Passwords do not match" }
    fun changePasswordButton(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "تغيير كلمة المرور"; AppLanguage.FR -> "Modifier le mot de passe"; AppLanguage.EN -> "Change password" }
    fun changePasswordUpdating(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "جارٍ التحديث..."; AppLanguage.FR -> "Mise à jour en cours..."; AppLanguage.EN -> "Updating..." }
    fun logoutCaption(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "تسجيل الخروج"; AppLanguage.FR -> "Se déconnecter"; AppLanguage.EN -> "Sign out" }
    fun appearanceSectionTitle(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "المظهر واللغة"; AppLanguage.FR -> "Apparence et langue"; AppLanguage.EN -> "Appearance & language" }
    fun themeLabel(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "المظهر"; AppLanguage.FR -> "Thème"; AppLanguage.EN -> "Theme" }
    fun themeSystem(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "اتبع النظام"; AppLanguage.FR -> "Système"; AppLanguage.EN -> "System default" }
    fun themeLight(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "فاتح"; AppLanguage.FR -> "Clair"; AppLanguage.EN -> "Light" }
    fun themeDark(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "داكن"; AppLanguage.FR -> "Sombre"; AppLanguage.EN -> "Dark" }
    fun languageLabel(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "اللغة"; AppLanguage.FR -> "Langue"; AppLanguage.EN -> "Language" }
    fun languageChangeHint(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "يُطبَّق التغيير فورًا على كامل التطبيق"; AppLanguage.FR -> "La modification s'applique immédiatement à toute l'application"; AppLanguage.EN -> "The change applies immediately across the app" }
    fun notificationsSectionTitle(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "التنبيهات والمزامنة"; AppLanguage.FR -> "Notifications et synchronisation"; AppLanguage.EN -> "Notifications & sync" }
    fun requestNotificationsTitle(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "تنبيهات طلبات الخدمة"; AppLanguage.FR -> "Notifications de demandes de service"; AppLanguage.EN -> "Service request alerts" }
    fun requestNotificationsDescription(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "تنبيه فوري عند وصول طلب خدمة جديد أو تغيّر حالة طلبك"; AppLanguage.FR -> "Alerte immédiate en cas de nouvelle demande ou de changement d'état"; AppLanguage.EN -> "Instant alert for new requests or status changes" }
    fun refreshIntervalLabel(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "فترة إعادة التحقق من الطلبات"; AppLanguage.FR -> "Fréquence de vérification des demandes"; AppLanguage.EN -> "Request check interval" }
    fun refreshIntervalOption(lang: AppLanguage, seconds: Int): String = when (lang) { AppLanguage.AR -> "كل %d ثانية".replace("%d", seconds.toString()); AppLanguage.FR -> "Toutes les %d secondes".replace("%d", seconds.toString()); AppLanguage.EN -> "Every %d seconds".replace("%d", seconds.toString()) }
    fun refreshIntervalValue(lang: AppLanguage, seconds: Int): String = when (lang) { AppLanguage.AR -> "%d ثانية".replace("%d", seconds.toString()); AppLanguage.FR -> "%d secondes".replace("%d", seconds.toString()); AppLanguage.EN -> "%d seconds".replace("%d", seconds.toString()) }
    fun craftsmanNotificationsTitle(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "تنبيهات وصول حرفيين جدد"; AppLanguage.FR -> "Notifications d'artisans disponibles"; AppLanguage.EN -> "New craftsman alerts" }
    fun craftsmanNotificationsDescription(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "إشعار عند إضافة حرفيين جدد إلى الدليل"; AppLanguage.FR -> "Alerte lorsqu'un nouvel artisan rejoint le guide"; AppLanguage.EN -> "Alert when new craftsmen join the directory" }
    fun craftsmanSectionTitle(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "إدارة الملف الحرفي"; AppLanguage.FR -> "Gestion du profil artisan"; AppLanguage.EN -> "Craftsman profile management" }
    fun editProfileHint(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "عدّل معلوماتك في الدليل. التغييرات تُحفظ محليًا وتُرسل للخادم عند الاتصال."; AppLanguage.FR -> "Modifiez vos informations du guide. Les changements sont enregistrés localement puis envoyés au serveur."; AppLanguage.EN -> "Edit your directory information. Changes save locally and upload to the server when connected." }
    fun professionalNameLabel(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "الاسم المهني"; AppLanguage.FR -> "Nom professionnel"; AppLanguage.EN -> "Professional name" }
    fun professionalDescriptionLabel(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "وصف الخدمات والتخصص"; AppLanguage.FR -> "Description des services"; AppLanguage.EN -> "Service description" }
    fun phoneFieldLabel(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "رقم الهاتف"; AppLanguage.FR -> "Téléphone"; AppLanguage.EN -> "Phone number" }
    fun wilayaFieldLabel(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "الولاية"; AppLanguage.FR -> "Wilaya"; AppLanguage.EN -> "Wilaya" }
    fun communeFieldLabel(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "البلدية"; AppLanguage.FR -> "Commune"; AppLanguage.EN -> "Commune" }
    fun dailyRateOptionalLabel(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "الأجر اليومي التقريبي (اختياري، بالدينار)"; AppLanguage.FR -> "Tarif journalier indicatif (facultatif, en DZD)"; AppLanguage.EN -> "Approximate daily rate (optional, in DZD)" }
    fun saveEditsButton(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "حفظ التعديلات"; AppLanguage.FR -> "Enregistrer les modifications"; AppLanguage.EN -> "Save changes" }
    fun updateSuccess(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "تم الحفظ بنجاح"; AppLanguage.FR -> "Enregistré avec succès"; AppLanguage.EN -> "Saved successfully" }
    fun resetCraftsmanButton(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "إعادة تعيين الملف إلى الحالة الأولية"; AppLanguage.FR -> "Réinitialiser le profil"; AppLanguage.EN -> "Reset profile" }
    fun availabilityToggleTitle(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "متاح حاليًا لاستقبال الطلبات"; AppLanguage.FR -> "Disponible pour de nouvelles demandes"; AppLanguage.EN -> "Available for new requests" }
    fun availabilityToggleDescription(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "عند التعطيل لن تظهر طلبات جديدة لديك حتى تعيد تفعيله"; AppLanguage.FR -> "Une fois désactivé, aucune nouvelle demande ne vous sera attribuée"; AppLanguage.EN -> "When disabled, new requests won't reach you until re-enabled" }
    fun photos_count(lang: AppLanguage, loaded: Int, max: Int): String = when (lang) { AppLanguage.AR -> "الصور (%d/%d)".replace("%d", loaded.toString()).replace("%d", max.toString()); AppLanguage.FR -> "Photos (%d/%d)".replace("%d", loaded.toString()).replace("%d", max.toString()); AppLanguage.EN -> "Photos (%d/%d)".replace("%d", loaded.toString()).replace("%d", max.toString()) }
    fun add_photos_label(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "إضافة صور"; AppLanguage.FR -> "Ajouter des photos"; AppLanguage.EN -> "Add photos" }
    fun describe_placeholder(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "اكتب وصفًا واضحًا للعمل المطلوب..."; AppLanguage.FR -> "Décrivez le travail souhaité..."; AppLanguage.EN -> "Describe the work you need..." }
    fun connection_lost_photos(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "انقطع الاتصال — ستُرسل الصور عند عودة الشبكة"; AppLanguage.FR -> "Connexion perdue — les photos seront envoyées au retour du réseau"; AppLanguage.EN -> "Connection lost — photos will be sent when the network returns" }
    fun phone_required_error(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "رقم الهاتف مطلوب"; AppLanguage.FR -> "Le téléphone est requis"; AppLanguage.EN -> "Phone number is required" }
    fun trade_specialty_label(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "التخصص المهني"; AppLanguage.FR -> "Spécialité professionnelle"; AppLanguage.EN -> "Trade specialty" }
    fun worker_intro_text(lang: AppLanguage): String = when (lang) { AppLanguage.AR -> "أضف حرفيًا إلى الدليل حتى تتمكن من طلب خدماته"; AppLanguage.FR -> "Ajoutez un artisan au guide pour pouvoir demander ses services"; AppLanguage.EN -> "Add a craftsman to the directory to request their services" }
    fun pending_sync_banner(lang: AppLanguage, count: Int): String = when (lang) { AppLanguage.AR -> "توجد %d طلبات محلية بانتظار المزامنة".replace("%d", count.toString()); AppLanguage.FR -> "%d demandes locales en attente de synchronisation".replace("%d", count.toString()); AppLanguage.EN -> "%d local requests waiting to sync".replace("%d", count.toString()) }
}
