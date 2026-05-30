# CLAUDE.md — DevOps / CI/CD

## Role: Senior DevOps Engineer (KMP + GitHub Actions)

Bạn là DevOps engineer chuyên KMP pipeline với kinh nghiệm sâu về:

- GitHub Actions workflows cho Kotlin Multiplatform projects
- Android signing, APK/AAB build và Firebase App Distribution
- Firebase Hosting deploy cho Compose for Web (wasmJs)
- Google Play Store publish via service account
- Gradle task orchestration trong môi trường CI

---

## Pipeline Overview — `github-cicd.yml`

```
Trigger
  ├── push → main
  ├── pull_request → main
  └── workflow_dispatch (manual, chọn: app_tester | google_play)

Stage 1: test (ubuntu-latest)
  └── ./gradlew test || true        ← luôn pass dù test fail (xem Issues)

Stage 2 (song song sau test):
  ├── publish-android-to-tester     ← APK → Firebase App Distribution
  │     environment: manual-release (cần approval)
  └── deploy-web                    ← wasmJs → Firebase Hosting (auto)

Stage 3 (sau publish-android-to-tester):
  └── publish-to-play-store         ← AAB → Google Play internal track
        environment: google-play-release (cần approval)
```

---

## Jobs Chi Tiết

### `test`

- Chạy `./gradlew test || true` — **`|| true` làm CI không bao giờ fail ở bước test**
- Mục đích hiện tại: chạy cho có, không block pipeline

### `publish-android-to-tester`

- **Requires:** `environment: manual-release` — phải có người approve trên GitHub
- Build: `assembleRelease` → APK
- Upload: `appDistributionUploadRelease` → Firebase App Distribution group `mobile-developer`
- `VERSION_CODE` = `${{ github.run_number }}` — auto-increment theo run number
- Keystore decode từ base64 secret → `release.jks` tại root project

### `deploy-web`

- Build: `wasmJsBrowserDistribution`
- Output dir: `composeApp/build/dist/wasmJs/productionExecutable` (theo `firebase.json`)
- Deploy: `FirebaseExtended/action-hosting-deploy@v0` → channel `live`
- **Không cần approval** — auto deploy mỗi lần push main

### `publish-to-play-store`

- **Requires:** `environment: google-play-release` — approval riêng
- Build: `bundleRelease` → AAB
- Publish: `r0adkll/upload-google-play@v1` → track `internal`
- Package: `com.duyts.pokerhost`

---

## Secrets Required

| Secret                                      | Dùng ở                    | Mô tả                                     |
|---------------------------------------------|---------------------------|-------------------------------------------|
| `SIGNING_KEY_STORE_BASE64`                  | android jobs              | Keystore file encode base64               |
| `SIGNING_KEY_ALIAS`                         | android jobs              | Key alias trong keystore                  |
| `SIGNING_KEY_PASSWORD`                      | android jobs              | Password của key                          |
| `SIGNING_STORE_PASSWORD`                    | android jobs              | Password của keystore                     |
| `FIREBASE_TOKEN`                            | publish-android-to-tester | Firebase CLI token                        |
| `FIREBASE_SERVICE_ACCOUNT_POKER_HOST_550CA` | deploy-web                | Service account JSON cho Firebase Hosting |
| `SERVICE_ACCOUNT_JSON`                      | publish-to-play-store     | Google Play service account JSON          |
| `GITHUB_TOKEN`                              | deploy-web                | Auto-provided bởi GitHub                  |

---

## Signing Config — `build.gradle.kts`

```kotlin
// Priority: Gradle property → Env var → local.properties
fun getProp(key: String): String? =
    project.findProperty(key)?.toString()
        ?: System.getenv(key)
        ?: localProperties.getProperty(key)
```

CI truyền signing props qua `-P` flags:

```
./gradlew assembleRelease -PVERSION_CODE=123 -PSIGNING_STORE_FILE=release.jks
```

Env vars `SIGNING_KEY_ALIAS`, `SIGNING_KEY_PASSWORD`, `SIGNING_STORE_PASSWORD` được set qua `env:`
block.

---

## Firebase Hosting Config — `firebase.json`

```json
"public": "composeApp/build/dist/wasmJs/productionExecutable"
"rewrites": [
  { "source": "/privacy", "destination": "/privacy/index.html" },
  { "source": "/terms",   "destination": "/terms/index.html"   },
  { "source": "**",       "destination": "/index.html"         }
]
```

SPA routing — mọi path đều về `index.html` trừ `/privacy` và `/terms`.

---

## Known Issues & Improvements Cần Làm

### 🔴 Critical

**1. `./gradlew test || true` — test failures bị ignore hoàn toàn**

```yaml
run: ./gradlew test || true  # ❌ CI không bao giờ fail dù test fail
```

Nên bỏ `|| true`. Nếu chưa có test ổn định thì continue-on-error đúng cách:

```yaml
- name: Run Tests
  run: ./gradlew test
  continue-on-error: true
```

Hoặc tốt hơn: fix tests và bỏ workaround.

**2. `publish-android-to-tester` chạy mọi push → main, không chỉ manual**
`workflow_dispatch` có input `deployment_target` nhưng jobs không check input đó. Mọi push vào
`main` đều trigger build + distribute Android, không phân biệt có muốn release hay không.

### 🟡 Architecture

**3. Gradle cache không được tối ưu**
Mỗi job dùng `cache: 'gradle'` nhưng đây là setup-java cache — không cache Gradle wrapper,
`.gradle/caches/`, hay KMP artifacts. Với KMP, build cold start mất 10-15 phút. Nên thêm:

```yaml
- uses: actions/cache@v4
  with:
    path: |
      ~/.gradle/caches
      ~/.gradle/wrapper
      ~/.konan
    key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle.kts') }}
```

**4. JDK setup lặp lại ở mọi job (4 lần)**
Mỗi job đều có 3 bước giống nhau: checkout + setup-java + chmod. Không thể dùng composite action
trong repo này hiện tại, nhưng nên extract thành reusable workflow.

**5. `deploy-web` không có environment protection**
Web deploy auto chạy không cần approval. Nếu có bug lọt qua test, web sẽ tự động deploy lên
production.

### 🟢 Quality

**6. `VERSION_CODE` dùng `github.run_number` — reset về 1 nếu chuyển repo**
`run_number` bắt đầu từ 1 và tăng theo pipeline. Nếu migrate repo hoặc reset, version code có thể
conflict với Play Store (Play Store yêu cầu version code phải tăng đơn điệu).

**7. Không có job iOS**
Pipeline không build/test iOS. iOS deploy phải làm thủ công qua Xcode / Fastlane.

**8. `FIREBASE_TOKEN` deprecated**
Firebase CLI đang chuyển sang service account. `FIREBASE_TOKEN` (legacy CI token) sẽ bị deprecate.
Nên migrate sang `FIREBASE_SERVICE_ACCOUNT` giống `deploy-web` job.

---

## Environment Protection Setup (GitHub)

```
Settings → Environments:
  manual-release:
    Required reviewers: [team members]
  google-play-release:
    Required reviewers: [team members]
```

---

## Local Testing Commands

```bash
# Simulate CI Android build
./gradlew :composeApp:assembleRelease \
  -PVERSION_CODE=999 \
  -PSIGNING_STORE_FILE=release.jks

# Simulate CI web build
./gradlew :composeApp:wasmJsBrowserDistribution

# Simulate CI test
./gradlew test
```
