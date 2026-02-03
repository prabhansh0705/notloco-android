# NotLoco Android

Android application for NotLoco - Your mental health companion. This is the Android version of the iOS app, built with modern Android development practices.

## 📱 Features

- **Authentication**: Phone number-based login/signup with OTP verification
- **Journal**: Record audio/video journal entries with transcription
- **Chat**: Real-time messaging with therapists/coaches
- **Profile Management**: User profile and settings
- **Subscription**: Razorpay and Stripe payment integration
- **Push Notifications**: Firebase Cloud Messaging for real-time notifications

## 🏗️ Architecture

This app follows Clean Architecture principles with MVVM pattern:

- **Data Layer**: Network APIs, local storage (DataStore), repositories
- **Domain Layer**: Use cases and business logic
- **Presentation Layer**: ViewModels and Compose UI

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose + Material Design 3
- **Architecture**: MVVM + Clean Architecture
- **Dependency Injection**: Hilt
- **Networking**: Retrofit + OkHttp
- **Async**: Coroutines + Flow
- **Local Storage**: DataStore Preferences
- **Image Loading**: Coil
- **Payments**: Stripe SDK, Razorpay SDK
- **Notifications**: Firebase Cloud Messaging
- **Analytics**: Sentry

## 📋 Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK (API 34)
- Gradle 8.2+

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd NotLocoAndroid
```

### 2. Configuration

#### API Configuration
The app connects to the NotLoco backend API. The base URL is configured in `app/build.gradle.kts`:

```kotlin
buildConfigField("String", "BASE_URL", "\"https://api.notloco.com/v1/\"")
```

For development, you can change this to your local or staging server.

#### Firebase Setup

1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Add an Android app with package name `com.notloco.android`
3. Download `google-services.json` and place it in `app/` directory
4. Enable Firebase Cloud Messaging in your project

#### Payment Setup

**Stripe:**
- Get your publishable key from [Stripe Dashboard](https://dashboard.stripe.com/)
- Update the key in `app/build.gradle.kts`:
  ```kotlin
  buildConfigField("String", "STRIPE_PUBLISHABLE_KEY", "\"your_stripe_key\"")
  ```

**Razorpay:**
- Get your key from [Razorpay Dashboard](https://dashboard.razorpay.com/)
- Update the key in `app/build.gradle.kts`:
  ```kotlin
  buildConfigField("String", "RAZORPAY_KEY", "\"your_razorpay_key\"")
  ```

#### Google Places API
- Enable Google Places API in Google Cloud Console
- Update the API key in `AndroidManifest.xml`:
  ```xml
  <meta-data
      android:name="com.google.android.geo.API_KEY"
      android:value="your_google_places_api_key" />
  ```

### 3. Build and Run

#### Using Android Studio
1. Open the project in Android Studio
2. Let Gradle sync complete
3. Connect a device or start an emulator
4. Click Run ▶️

#### Using Command Line

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Install on connected device
./gradlew installDebug
```

## 📁 Project Structure

```
NotLocoAndroid/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/notloco/android/
│   │   │   │   ├── data/
│   │   │   │   │   ├── models/          # Data models
│   │   │   │   │   ├── network/         # API service
│   │   │   │   │   ├── local/           # Local storage
│   │   │   │   │   ├── repository/      # Repositories
│   │   │   │   │   └── service/         # Background services
│   │   │   │   ├── domain/
│   │   │   │   │   ├── usecase/         # Business logic
│   │   │   │   │   └── repository/      # Repository interfaces
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screens/         # Compose screens
│   │   │   │   │   ├── components/      # Reusable UI components
│   │   │   │   │   ├── theme/           # App theme
│   │   │   │   │   └── navigation/      # Navigation graph
│   │   │   │   ├── di/                  # Dependency injection
│   │   │   │   ├── utils/               # Utility classes
│   │   │   │   ├── MainActivity.kt
│   │   │   │   └── NotLocoApplication.kt
│   │   │   ├── res/                     # Resources
│   │   │   └── AndroidManifest.xml
│   │   └── test/                        # Unit tests
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## 🔐 Permissions

The app requires the following permissions:

- **INTERNET**: Network communication
- **RECORD_AUDIO**: Audio journal recording
- **CAMERA**: Video journal recording
- **POST_NOTIFICATIONS**: Push notifications (Android 13+)
- **ACCESS_NETWORK_STATE**: Check network connectivity

## 🎨 UI/UX

The app follows Material Design 3 guidelines with a custom color scheme matching the iOS version:

- **Primary Color**: `#F97F37` (Orange)
- **Background**: `#FFF9EA` (Cream)
- **Typography**: System font with custom font weights

## 🔄 Synchronization with iOS

This Android app mirrors the functionality of the iOS version:

| Feature | iOS | Android |
|---------|-----|---------|
| Authentication | ✅ | ✅ |
| Journal Recording | ✅ | ✅ |
| Chat | ✅ | ✅ |
| Profile | ✅ | ✅ |
| Payments (Stripe) | ✅ | ✅ |
| Payments (Razorpay) | ✅ | ✅ |
| Push Notifications | ✅ | ✅ |

## 📱 Minimum Requirements

- Android 7.0 (API 24) or higher
- 50 MB free storage
- Internet connection

## 🧪 Testing

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

## 🐛 Debugging

### Enable Logging
In debug builds, network logging is automatically enabled. Check Logcat for API requests/responses.

### Sentry Integration
Errors are automatically reported to Sentry in release builds. Update the DSN in `NotLocoApplication.kt` if needed.

## 📦 Building Release APK

1. Generate a signing key:
   ```bash
   keytool -genkey -v -keystore notloco-release.keystore -alias notloco -keyalg RSA -keysize 2048 -validity 10000
   ```

2. Create `keystore.properties` in the root directory:
   ```properties
   storePassword=your_store_password
   keyPassword=your_key_password
   keyAlias=notloco
   storeFile=notloco-release.keystore
   ```

3. Build release APK:
   ```bash
   ./gradlew assembleRelease
   ```

## 🚢 Deployment

### Google Play Store

1. Create a release in Google Play Console
2. Upload the signed APK/AAB
3. Fill in store listing details
4. Submit for review

### Internal Testing

Use Firebase App Distribution for beta testing:

```bash
./gradlew assembleDebug appDistributionUploadDebug
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Write/update tests
5. Submit a pull request

## 📄 License

[Add your license information here]

## 📞 Support

For issues or questions:
- Create an issue on GitHub
- Contact: support@notloco.com

## 🔄 Migration from iOS

If you're familiar with the iOS codebase, here's a quick mapping:

| iOS | Android |
|-----|---------|
| Swift | Kotlin |
| UIKit/SwiftUI | Jetpack Compose |
| Combine | Coroutines + Flow |
| UserDefaults | DataStore Preferences |
| Alamofire | Retrofit + OkHttp |
| MVVM Coordinators | Navigation Compose |
| Kingfisher | Coil |

## 📚 Additional Resources

- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Android Architecture Guide](https://developer.android.com/topic/architecture)
- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [Material Design 3](https://m3.material.io/)

## ✅ Checklist for First Run

- [ ] Add `google-services.json` file
- [ ] Update API keys (Stripe, Razorpay, Google Places)
- [ ] Configure Firebase project
- [ ] Update Sentry DSN (if using)
- [ ] Test authentication flow
- [ ] Test payment integration
- [ ] Verify push notifications

---

**Built with ❤️ for mental health support**
