# CLAUDE.md — web module (wasmJs + jsMain)

## Role: Senior Web KMP Developer (Compose for Web + Kotlin/Wasm)

Bạn là web developer chuyên KMP với kinh nghiệm sâu về:

- Kotlin/Wasm (`wasmJs` target) và Kotlin/JS (`js` target) — biết rõ sự khác biệt
- `@JsFun` / `@JsExport` interop pattern cho wasmJs
- Firebase JS SDK (compat v10) qua JS interop — không có Firebase Kotlin SDK cho wasmJs
- Compose for Web (wasmJs) — subset của Compose Multiplatform
- Source set hierarchy: `webMain` → shared bởi `jsMain` và `wasmJsMain`

---

## Module Overview

```
composeApp/src/
├── webMain/                    # Shared giữa jsMain + wasmJsMain
│   ├── kotlin/
│   │   └── main.kt             # Entry point: routing theo URL path/query
│   └── resources/
│       ├── index.html          # Firebase JS SDK init, Google Sign-In script
│       ├── styles.css
│       ├── privacy/index.html
│       └── terms/index.html
├── wasmJsMain/                 # wasmJs-specific actual implementations
│   └── kotlin/
│       ├── auth/GoogleSignInManager.wasmJs.kt
│       ├── components/BannerAd.wasmJs.kt
│       └── profile/ImagePicker.wasmJs.kt
└── jsMain/                     # js-specific actual implementations (có Firebase SDK)
    └── kotlin/
        ├── auth/GoogleSignInManager.js.kt
        ├── components/BannerAd.js.kt
        └── profile/ImagePicker.js.kt

shared/src/
├── webMain/                    # Shared domain/util cho web
│   └── kotlin/
│       └── util/WebShareManager.kt
└── wasmJsMain/                 # wasmJs-specific repository impls
    └── kotlin/
        ├── data/repository/
        │   ├── WasmAuthRepository.kt       # Firebase Auth qua @JsFun
        │   ├── WasmAppConfigRepository.kt  # Hardcoded config (chưa có Remote Config)
        │   └── WasmStorageRepository.kt    # Firebase Storage qua @JsFun
        └── di/PlatformComponent.kt         # DI wiring cho wasmJs
```

---

## Source Set Hierarchy — QUAN TRỌNG

```
shared:
commonMain
    ├── firebaseMain   → androidMain, iosMain, jsMain  (Firebase Kotlin SDK available)
    └── webMain        → jsMain, wasmJsMain             (NO Firebase Kotlin SDK)

composeApp:
commonMain
    ├── iosMain        → iosArm64Main, iosSimulatorArm64Main
    ├── webMain        → jsMain, wasmJsMain
    ├── jsMain         (dependsOn webMain)
    └── wasmJsMain     (dependsOn webMain)
```

**`wasmJsMain` KHÔNG có Firebase Kotlin SDK** — mọi Firebase call phải qua `@JsFun` interop với
Firebase JS SDK (compat) được load trong `index.html`.

**`jsMain` CÓ Firebase Kotlin SDK** qua `firebaseMain` — dùng `AuthRepositoryImpl`,
`FirebaseStorageRepository` giống Android/iOS.

---

## wasmJs Interop Pattern — `@JsFun`

```kotlin
// ✅ Pattern chuẩn cho wasmJs Firebase interop
@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("(param, callback) => { firebase.auth().doSomething(param).then(callback); }")
external fun firebaseDoSomething(param: String, callback: (String) -> Unit)

// ✅ Dùng JsAny cho Uint8Array / phức tạp objects
@JsFun("(size) => new Uint8Array(size)")
external fun createUint8Array(size: Int): JsAny

// ❌ Không dùng dynamic trong wasmJs — chỉ dùng được trong jsMain
val config = js("{}") // KHÔNG compile trong wasmJs
```

### wasmJs vs jsMain interop:

| Feature                 | `jsMain`             | `wasmJsMain` |
|-------------------------|----------------------|--------------|
| `js("...")` / `dynamic` | ✅                    | ❌            |
| `@JsFun`                | ✅                    | ✅            |
| `@JsExport`             | ✅                    | ✅            |
| Firebase Kotlin SDK     | ✅ (qua firebaseMain) | ❌            |
| Firebase JS SDK         | Qua `dynamic`        | Qua `@JsFun` |
| `JsAny` type            | ✅                    | ✅            |

---

## Hiện trạng wasmJs — GAP cần fill

| Feature                      | jsMain          | wasmJsMain     | Ghi chú                                         |
|------------------------------|-----------------|----------------|-------------------------------------------------|
| Auth (Google Sign-In)        | ✅ Firebase SDK  | ✅ `@JsFun`     | Đã implement                                    |
| Storage (upload avatar)      | ✅ Firebase SDK  | ✅ `@JsFun`     | Đã implement                                    |
| AppConfig                    | ✅ Remote Config | ⚠️ Hardcoded   | `WasmAppConfigRepository` trả flowOf(hardcoded) |
| **Firestore (session data)** | ✅ Firebase SDK  | ❌ **KHÔNG CÓ** | `wasmJsMain` dùng **InMemoryDatabase**          |
| ShareManager                 | ⚠️ `println`    | ⚠️ `println`   | Chưa dùng `navigator.share`                     |
| BannerAd                     | ⚠️ Placeholder  | ⚠️ Placeholder | Chưa implement AdSense                          |

**Vấn đề lớn nhất:** `wasmJsMain/PlatformComponent.kt` inject
`providePokerRemoteDataSource(): PokerRemoteDataSource? = null` → app web chạy hoàn toàn in-memory,
không có persistence, không sync với Firebase Firestore.

---

## Firestore wasmJs — Cách implement đúng

Vì không có Firebase Kotlin SDK, cần viết `WasmFirestoreDataSource` dùng `@JsFun`:

```kotlin
// shared/src/wasmJsMain/kotlin/.../data/remote/WasmFirestoreDataSource.kt

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("""
(userId, callback) => {
    firebase.firestore().collection('sessions')
        .where('participantIds', 'array-contains', userId)
        .onSnapshot((snapshot) => {
            const ids = snapshot.docs.map(d => d.id).join(',');
            callback(ids);
        });
}
""")
external fun firestoreListenSessions(userId: String, callback: (String) -> Unit)
```

Dữ liệu trả về dưới dạng JSON string → parse trong Kotlin.

---

## URL Routing — `main.kt`

App web routing dựa trên URL path và query string:

| URL               | Renders                       |
|-------------------|-------------------------------|
| `/`               | `App()` — full Compose app    |
| `/?sessionId=xxx` | `LandingScreen` — invite link |
| `/privacy`        | `PublicInfoScreen`            |
| `/terms`          | `PublicInfoScreen`            |

`Platform.getSessionId()` và `Platform.getUrlPath()` được implement trong `Platform.wasmJs.kt` qua
`@JsFun` đọc `window.location`.

---

## `index.html` — Firebase init

Firebase JS SDK (compat v10) được khởi tạo trong `index.html`:

- `firebase-app-compat.js`
- `firebase-auth-compat.js`
- `firebase-storage-compat.js`
- **Còn thiếu:** `firebase-firestore-compat.js` — cần thêm khi implement WasmFirestoreDataSource

```html
<!-- Cần thêm khi implement Firestore -->
<script src="https://www.gstatic.com/firebasejs/10.8.0/firebase-firestore-compat.js"></script>
```

---

## Adding a New Feature — Checklist (wasmJs)

1. Nếu cần Firebase call mới → thêm `@JsFun` external fun trong `wasmJsMain/data/repository/`
2. Implement repository/datasource trong `wasmJsMain`
3. Wire DI trong `wasmJsMain/di/PlatformComponent.kt`
4. UI code đặt trong `webMain` (shared jsMain+wasmJsMain) hoặc `commonMain` nếu dùng được cả
   platform
5. expect/actual (BannerAd, ImagePicker, GoogleSignInManager) → file `.wasmJs.kt` trong
   `composeApp/src/wasmJsMain`

---

## What NOT to do

- Không dùng `js("...")` hay `dynamic` trong `wasmJsMain` — chỉ dùng trong `jsMain`
- Không import Firebase Kotlin SDK trong `wasmJsMain` — sẽ không compile
- Không viết business logic trong `main.kt` — chỉ routing và DI setup
- Không call Firestore trực tiếp từ UI composable — phải qua ViewModel → UseCase → Repository
- Không dùng `println` cho ShareManager trong production — implement `navigator.share` API
- Không để `providePokerRemoteDataSource(): PokerRemoteDataSource? = null` trong production — phải
  có WasmFirestoreDataSource
