# CLAUDE.md — iosApp module

## Role: KMP iOS Developer (Swift + SwiftUI bridge)

Bạn là iOS developer chuyên tích hợp KMP framework vào native iOS. Bạn hiểu rõ:
- Cách KMP Shared.framework được build và embed vào Xcode
- Swift ↔ Kotlin bridge pattern (delegate, callback)
- SPM (Swift Package Manager) dependencies trong Xcode project
- iOS simulator vs device build differences

## Module Overview

`iosApp/` là entry point iOS — rất thin, chỉ chứa:
1. App entry point (`iOSApp.swift`)
2. Root view + KMP bridge (`ContentView.swift`)
3. Xcode project config (`iosApp.xcodeproj`)

```
iosApp/
├── iosApp.xcodeproj/
│   └── project.pbxproj          # SPM deps, build phases, signing config
├── Configuration/
│   └── Config.xcconfig          # TEAM_ID, PRODUCT_NAME, bundle ID, version
└── iosApp/
    ├── iOSApp.swift             # @main entry
    ├── ContentView.swift        # ComposeView + GoogleSignIn bridge
    ├── GoogleService-Info.plist # Firebase config
    └── Info.plist
```

## KMP Framework Integration

Xcode build phase chạy Gradle task:
```sh
cd "$SRCROOT/.."
./gradlew :composeApp:embedAndSignAppleFrameworkForXcode
```
Task này build `Shared.framework` (static) và embed vào app bundle.

**Framework được import trong Swift:**
```swift
import Shared  // → KMP Shared.framework
```

## Google Sign-In Bridge

iOS không thể gọi `GIDSignIn` từ Kotlin — bridge qua delegate:

```swift
// ContentView.swift
GoogleSignInProvider.shared.delegate = { onTokenReceived, onError in
    GIDSignIn.sharedInstance.signIn(withPresenting: controller) { result, error in
        if let error = error {
            onError(error.localizedDescription)
        } else if let idToken = result?.user.idToken?.tokenString {
            let accessToken = result?.user.accessToken.tokenString
            onTokenReceived(idToken, accessToken)  // accessToken MUST NOT be nil
        } else {
            onError("Failed to get ID Token")
        }
    }
}
```

**Quan trọng:** Firebase iOS SDK yêu cầu `accessToken` không null trong `GoogleAuthProvider.credential`.
Luôn lấy `result?.user.accessToken.tokenString` — đây là `GIDToken`, không optional.

## SPM Dependencies (project.pbxproj)

Firebase packages được add qua SPM. Hiện tại đã có:
- `FirebaseAuth`
- `FirebaseCore`
- `FirebaseFirestore`
- `FirebaseStorage`
- `FirebaseRemoteConfig` ← thêm vào vì KMP cinterop cần
- `GoogleSignIn`
- `GoogleSignInSwift`

**Khi KMP firebase-kotlin-sdk thêm module mới** → phải add corresponding SPM product vào Xcode.
Cách add vào `project.pbxproj` (3 chỗ):
1. `PBXBuildFile` section
2. `PBXFrameworksBuildPhase > files`
3. `packageProductDependencies` trong target
4. `XCSwiftPackageProductDependency` section

## Config.xcconfig — Quy tắc

```xcconfig
TEAM_ID=                                          # để trống cho local build
PRODUCT_NAME=Poker Manager                        # PHẢI có dấu = và value
PRODUCT_BUNDLE_IDENTIFIER=com.duyts.pokerhost$(TEAM_ID)
CURRENT_PROJECT_VERSION=1
MARKETING_VERSION=1.0
```
Thiếu `= value` → Xcode parse error khi build.

## Build Commands

```bash
# KMP shared compile (iOS simulator)
./gradlew :composeApp:compileKotlinIosSimulatorArm64

# Full Xcode build (simulator)
xcodebuild -project iosApp/iosApp.xcodeproj \
           -scheme iosApp \
           -destination 'platform=iOS Simulator,name=iPhone 17 Pro' \
           build

# Resolve SPM packages
xcodebuild -project iosApp/iosApp.xcodeproj \
           -scheme iosApp \
           -resolvePackageDependencies
```

## Common iOS Build Errors & Fixes

| Error | Cause | Fix |
|---|---|---|
| `expected has no actual for Native` | `iosMain` sourceset không được wire trong `composeApp/build.gradle.kts` | Thêm explicit `dependsOn` cho `iosArm64Main`/`iosSimulatorArm64Main` |
| `Undefined symbols: _FIRRemoteConfig*` | Firebase SPM product bị thiếu | Add `FirebaseRemoteConfig` vào `project.pbxproj` |
| `expected a '='` in xcconfig | `PRODUCT_NAME` thiếu `= value` | Fix `Config.xcconfig` |
| `AccessToken must not be null` | iOS bridge chỉ pass `idToken`, thiếu `accessToken` | Pass cả `result?.user.accessToken.tokenString` |

## What NOT to do
- Không viết business logic trong `ContentView.swift` hay `iOSApp.swift`
- Không tạo thêm Swift file trừ khi cần bridge native iOS capability
- Không edit `project.pbxproj` thủ công cho signing — dùng Xcode GUI hoặc `Config.xcconfig`
- Không commit `GoogleService-Info.plist` vào public repo
