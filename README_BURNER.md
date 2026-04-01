# Burner Mode screen

Files added

- app/src/main/java/com/example/burnermode/BurnerActivity.kt
- app/src/main/java/com/example/burnermode/EncryptedPrefsManager.kt
- app/src/main/res/layout/activity_burner.xml

Integration notes

1. Add dependencies to your app module `build.gradle` (or use the provided `app/build.gradle` as a reference):

```gradle
implementation "androidx.security:security-crypto:1.1.0"
implementation "com.google.android.material:material:1.9.0"
implementation "androidx.biometric:biometric:1.1.0"
```

2. Register `BurnerActivity` in your `AndroidManifest.xml` or integrate it into your navigation.

Behavior summary

- Burner Mode toggle: flips UI into a dark-red secure mode and stores `burner_active` and `start_time` in encrypted prefs.
- Session timer: shows elapsed HH:MM:SS while Burner Mode is active and resets to `00:00:00` when session is wiped.
- Biometric auth: requires biometric verification before enabling Burner Mode.
- PIN lock: optional PIN entry after biometric auth. If a normal PIN is configured the user must enter it to enable the session.
- Duress PIN: a second PIN that, when entered on the PIN lock screen, silently triggers a panic wipe and returns the user to the normal UI as if nothing happened.
- Panic Wipe button: shows a confirmation dialog and immediately wipes burner-related encrypted prefs, stops timer, and animates the UI back to default with a "Session wiped" snackbar.
- Wipe on toggle-off: toggling Burner Mode off will wipe burner data.

Security notes

- Uses `EncryptedSharedPreferences` with a `MasterKey` (AES-256). Ensure your `minSdk` is compatible with the chosen `security-crypto` version.
- PINs are stored in encrypted prefs for convenience; consider using stronger protections (rate-limiting, secure key management) in production.

Duress PIN setup & testing

- The code exposes `putPin(pin: String)` and `putDuressPin(pin: String)` on `EncryptedPrefsManager` for programmatic setup/testing.
- Example test setup (run from an Activity/Console during dev):

```kotlin
val prefs = EncryptedPrefsManager(context)
prefs.putPin("1234")        // normal PIN
prefs.putDuressPin("0000")  // duress PIN
```

- Flow: User toggles ON -> biometric prompt -> (if PIN exists) PIN dialog shown -> normal PIN enables session, duress PIN triggers silent wipe.

Lint/build

- Quick build steps (use Gradle wrapper if present):

```bash
./gradlew assembleDebug
./gradlew lint
```

- If you use `ktlint`/`detekt`, add them to the project and I can wire up checks and tasks.

Notes

- The example `app/build.gradle` in this workspace is a minimal reference; merge dependency lines into your existing Gradle files if you already have an app module.
- I can add a small UI for users to set/change PINs securely if you'd like.
