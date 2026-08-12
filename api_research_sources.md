# مصادر مقارنة خدمات Backend/API

تاريخ البحث: 2026-08-12

## Supabase
المصدر الرسمي: https://supabase.com/pricing

المعلومات المستخرجة من صفحة التسعير الرسمية: الخطة المجانية تتضمن 50,000 مستخدم نشط شهريًا، قاعدة بيانات PostgreSQL بحجم 500 MB، 5 GB egress، 5 GB cached egress، و1 GB تخزين ملفات. تتضمن أيضًا API غير محدود، مصادقة، تحكمًا بالوصول إلى الملفات، Postgres Changes، و2 مليون رسالة شهريًا. المشاريع المجانية تتوقف بعد أسبوع من عدم النشاط، والحد هو مشروعان نشطان.

## Firebase
المصدر الرسمي للتسعير: https://firebase.google.com/pricing
المصدر الرسمي للمصادقة: https://firebase.google.com/docs/auth

Firebase يوفّر خطة Spark بدون تكلفة وبدون الحاجة إلى وسيلة دفع، مع خدمات مجانية مثل Analytics وApp Check وCrashlytics وCloud Messaging وRemote Config. المصادقة تدعم البريد وكلمة المرور، أرقام الهاتف، Google ومزوّدي OAuth، وإعادة تعيين كلمة المرور. صفحة المصادقة الرسمية تذكر أن الترقية إلى Identity Platform تضيف خصائص متقدمة، لكنها تغيّر الحدود والتسعير؛ المشاريع Spark بعد الترقية محدودة بـ3,000 مستخدم نشط يوميًا لمعظم المزوّدين. التخزين وقاعدة البيانات والخدمات الأخرى يجب التحقق من حدودها داخل صفحة التسعير قبل الإنتاج لأن بعض الاستخدامات مرتبطة بخطة Blaze أو برسوم Google Cloud.

## Appwrite Cloud
المصدر الرسمي: https://appwrite.io/pricing

الخطة المجانية تتضمن 5 GB bandwidth، و2 GB storage، و750,000 execution، و75,000 مستخدم نشط شهريًا، وقاعدة بيانات واحدة، وBucket واحد، ووظيفتين لكل مشروع، ودعم المجتمع. المشاريع المجانية تتوقف بعد أسبوع من عدم النشاط، والحد هو مشروعان.

## قراءة أولية للمشروع
Herafi DZ يحتاج إلى مصادقة مركزية، قاعدة بيانات للحرفيين والتقييمات والطلبات، تخزين صور، API، وربما إشعارات. لذلك لا يكفي API عام بلا قاعدة بيانات ومصادقة. يجب اختيار Backend كامل، مع إبقاء Room كذاكرة مؤقتة/cache داخل Android.

## ملاحظات المقارنة
Supabase مناسب إذا كانت الأولوية PostgreSQL وSQL ومرونة الاستعلامات وربط Android عبر REST/Kotlin. Firebase مناسب إذا كانت الأولوية التكامل الأصلي مع Android وFCM وCrashlytics وسرعة بناء MVP. Appwrite مناسب إذا كانت الأولوية حلًا مفتوح المصدر وواجهة Backend بسيطة مع خيار الاستضافة الذاتية لاحقًا، مع الانتباه إلى إيقاف المشاريع المجانية بعد أسبوع من عدم النشاط.

## الروابط
1. https://supabase.com/pricing
2. https://firebase.google.com/pricing
3. https://firebase.google.com/docs/auth
4. https://appwrite.io/pricing
5. https://supabase.com/docs/guides/platform
6. https://firebase.google.com/docs/cloud-messaging
7. https://appwrite.io/docs

هذه الحدود قابلة للتغير، ويجب إعادة التحقق منها في لوحة الخدمة قبل تفعيل مشروع إنتاجي أو إدخال بيانات مستخدمين حقيقية.
