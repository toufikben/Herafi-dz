# Herafi DZ

Herafi DZ is an Android prototype for discovering craftsmen and service workers in Algeria. It provides a local directory with search, Wilaya and trade filters, sorting, saved workers, ratings, local account flows, and direct phone/WhatsApp contact.

## Current scope

The current version is a **local-first prototype**. Craftsmen, reviews, bookmarks, and users are stored in a Room database on the device. The initial directory is seeded from `SeedData.kt`. There is currently no shared backend, central account service, server-side moderation, booking workflow, payment, or cross-device synchronization.

The most important consequence is that a worker added on one device is not automatically visible on another device. The app should therefore be presented as a prototype or offline directory until a backend is introduced.

## Architecture

- `MainActivity` creates the Compose content and hosts `HomeScreen`.
- `MainViewModel` owns UI state, filters, authentication flow, bookmarks, worker registration, and ratings.
- `CraftsmanRepository` and `UserRepository` isolate data operations.
- Room stores craftsmen, reviews, bookmarks, and local users.
- `SeedData.kt` supplies demo data for the initial experience.
- Compose components render search, cards, detail sheets, authentication, ratings, and worker registration.

## Development requirements

The project requires Android Studio with an Android SDK matching the configured compile and target SDK, Java 11-compatible project settings, Kotlin, Android Gradle Plugin, and the dependencies declared in `gradle/libs.versions.toml`. The repository currently does not contain a Gradle Wrapper, so adding the wrapper in the development environment is recommended before onboarding contributors or enabling CI.

Recommended local commands after the Gradle Wrapper is generated:

```bash
./gradlew testDebugUnitTest
./gradlew assembleDebug
```

## Security status

This prototype uses local authentication only. A production release must use a real authentication provider or a reviewed local authentication design, must never store plaintext passwords, must verify phone/email ownership, and must not mark newly submitted workers as verified without a review process. Personal phone and WhatsApp data should only be published with consent and should be covered by a privacy policy and deletion process.

## Product roadmap

The recommended production sequence is:

1. Stabilize the Android build, add the Gradle Wrapper, CI, unit tests, Room migrations, and structured validation.
2. Replace local-only authentication with a backend authentication service and secure session handling.
3. Introduce a central API and database for worker profiles, reviews, moderation, and synchronization.
4. Add real location search, worker verification, service requests, notifications, and reporting.
5. Add booking, quotations, payments, analytics, and an administration dashboard only after the trust and security foundations are in place.

## Contribution rules

Keep UI code in Compose components, keep business logic in repositories or ViewModels, avoid putting secrets in source control, add tests for every new data operation, and document schema changes with Room migrations. Do not describe local demo data as verified marketplace data.

## Repository analysis

A detailed Arabic analysis is available in [`Herafi-DZ-تحليل-شامل.md`](Herafi-DZ-تحليل-شامل.md). It records the current capabilities, limitations, security findings, and implementation priorities.
