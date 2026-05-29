# CLAUDE.md — composeApp module

## Role: KMP Android Developer (Compose Multiplatform)

Bạn là developer chuyên Compose Multiplatform, hiểu rõ:
- Jetpack Compose và Compose Multiplatform (CMP) khác nhau ở đâu
- `expect`/`actual` pattern cho platform-specific UI components
- Navigation với `androidx.navigation.compose`
- `StateFlow` + `collectAsStateWithLifecycle` pattern
- Khi nào code nên ở `commonMain` vs `androidMain` vs `iosMain`

## Module Overview

`composeApp/` chứa **toàn bộ UI** của app. Business logic KHÔNG được viết ở đây — chỉ presentation.

```
composeApp/src/
├── commonMain/
│   ├── presentation/
│   │   ├── auth/           # GoogleSignInManager (expect), GoogleSignInLauncher interface
│   │   ├── graph/          # Navigation graphs: AuthGraph, MainGraph, SplashGraph
│   │   ├── theme/          # Color, Typography, Theme
│   │   └── ui/             # Screens + Components
│   │       ├── dashboard/  # DashboardScreen
│   │       ├── login/      # LoginScreen
│   │       ├── sessionDetails/ # PokerSessionDetailScreen + components
│   │       ├── statistics/ # StatisticsScreen
│   │       ├── profile/    # ProfileScreen, EditProfileScreen, ImagePicker (expect)
│   │       └── components/ # BannerAd (expect), shared dialogs, bottom sheets
│   └── App.kt              # Root composable, DI setup
├── androidMain/
│   ├── MainActivity.kt
│   ├── GoogleSignInManager.android.kt  # actual — Google Sign-In via GMS
│   ├── BannerAd.android.kt             # actual — AdMob
│   └── ImagePicker.android.kt          # actual — ActivityResultContracts
└── iosMain/
    ├── MainViewController.kt
    ├── GoogleSignInManager.ios.kt      # actual — delegate pattern → Swift
    ├── BannerAd.ios.kt                 # actual — placeholder
    └── ImagePicker.ios.kt              # actual — placeholder
```

## Tech Stack

| Concern | Library |
|---|---|
| UI | Compose Multiplatform |
| Navigation | androidx.navigation.compose |
| State | ViewModel (từ shared) + StateFlow |
| Image loading | Coil + Ktor engine |
| Charts | Charty |
| DI | kotlin-inject (wired từ shared) |

## expect/actual Pattern — 3 components cần platform impl

### 1. GoogleSignInManager
```kotlin
// commonMain — expect
expect fun rememberGoogleSignInLauncher(
    onTokenReceived: (idToken: String, accessToken: String?) -> Unit,
    onError: (String) -> Unit,
): GoogleSignInLauncher

// Android actual — dùng GMS GoogleSignInOptions
// iOS actual — dùng GoogleSignInProvider.delegate (bridge sang Swift)
```

**iOS bridge flow:**
```
Swift (ContentView.swift)
  GIDSignIn → idToken + accessToken
    ↓ GoogleSignInProvider.shared.delegate
Kotlin (GoogleSignInManager.ios.kt)
  onTokenReceived(idToken, accessToken)
```

### 2. BannerAd
```kotlin
// Android actual — AdMob BannerAd
// iOS actual — placeholder Box (AdMob iOS chưa implement)
```

### 3. ImagePicker
```kotlin
// Android actual — ActivityResultContracts.GetContent → ByteArray
// iOS actual — placeholder (TODO: implement PHPickerViewController)
```

## Navigation Structure

```
SplashGraph
  └── SplashScreen → decides Auth or Main

AuthGraph
  ├── LoginScreen
  ├── PrivacyScreen
  └── TermsScreen

MainGraph
  ├── DashboardScreen (bottom nav)
  ├── StatisticsScreen (bottom nav)
  ├── ProfileScreen (bottom nav)
  ├── PokerSessionDetailScreen
  ├── EditProfileScreen
  └── SettingsScreen
```

## State Pattern (mỗi Screen)

```kotlin
// ViewModel expose StateFlow (từ shared module)
val state: StateFlow<UiState> = viewModel.stateFlow

// Screen collect
val state by viewModel.state.collectAsStateWithLifecycle()
```

## Source Set Rules

| Code | Đặt ở |
|---|---|
| UI dùng được trên cả Android + iOS | `commonMain` |
| Google Sign-In, AdMob, Android permissions | `androidMain` |
| iOS UIKit bridge, iOS-specific APIs | `iosMain` |
| ViewModel, use case calls | `shared` module — KHÔNG viết lại ở đây |

## iosMain Source Set — Quan trọng

`iosMain` phải được khai báo **explicit** trong `build.gradle.kts`:
```kotlin
val iosMain by creating { dependsOn(commonMain.get()) }
val iosArm64Main by getting { dependsOn(iosMain) }
val iosSimulatorArm64Main by getting { dependsOn(iosMain) }
```
Nếu thiếu → `expect` declarations báo "no actual for Native".

## What NOT to do
- Không call Firestore/Firebase trực tiếp từ composable hay UI layer
- Không tạo ViewModel trong `composeApp` — tất cả ViewModel nằm trong `shared`
- Không dùng `remember { mutableStateOf(...) }` cho app state — chỉ dùng cho UI-local state
- Không hardcode string — dùng resource hoặc constant
