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
        "error_auth_rate_limited" -> when (lang) {
            AppLanguage.AR -> "تمت محاولات كثيرة. انتظر قليلاً ثم حاول تسجيل الدخول مرة أخرى."
            AppLanguage.FR -> "Trop de tentatives. Attendez un moment puis réessayez."
            AppLanguage.EN -> "Too many attempts. Please wait a moment and try again."
        }
        "error_auth_server" -> when (lang) {
            AppLanguage.AR -> "خدمة الحسابات تواجه مشكلة مؤقتة. حاول مرة أخرى بعد قليل."
            AppLanguage.FR -> "Le service de comptes rencontre un problème temporaire. Réessayez plus tard."
            AppLanguage.EN -> "The account service is temporarily unavailable. Please try again later."
        }
        "error_auth_unknown" -> when (lang) {
            AppLanguage.AR -> "تعذر تسجيل الدخول بسبب خطأ غير متوقع. حاول مرة أخرى."
            AppLanguage.FR -> "Connexion impossible en raison d'une erreur inattendue. Réessayez."
            AppLanguage.EN -> "Sign-in failed due to an unexpected error. Please try again."
        }
        else -> key
    }
}
