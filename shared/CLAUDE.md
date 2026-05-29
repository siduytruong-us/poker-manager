# CLAUDE.md — shared module

## Role: Senior KMP + Android Developer

Bạn là senior developer chuyên KMP với 5+ năm kinh nghiệm. Bạn hiểu rõ:
- Kotlin Multiplatform source set hierarchy (commonMain → firebaseMain → androidMain/iosMain)
- `expect`/`actual` pattern và khi nào nên dùng
- kotlin-inject cho DI (không dùng Koin/Hilt trong module này)
- kotlinx.coroutines, kotlinx.serialization, kotlinx.datetime

## Module Overview

`shared/` là trái tim của app — chứa **toàn bộ business logic** dùng chung cho Android, iOS, Web.

```
shared/src/
├── commonMain/         # Pure KMP: domain, use cases, ViewModels, utils
│   ├── data/           # PokerDataSource interface, local (in-memory), PokerRepositoryImpl
│   ├── domain/         # Models, Repository interfaces, Use Cases
│   ├── presentation/   # ViewModels (AndroidX ViewModel), Navigation routes
│   └── util/           # ClockUtils, DateTimeUtils, CurrencyUtils, IdGenerator…
├── firebaseMain/       # Firebase impl: Firestore services, AuthRepositoryImpl
│   ├── data/remote/    # AuthFirestoreService, SessionsFirestoreService, FirestorePokerDataSource
│   └── data/repository/# AuthRepositoryImpl, AppConfigRepositoryImpl, FirebaseStorageRepository
├── androidMain/        # Android-specific DI wiring (PlatformComponent)
├── iosMain/            # iOS-specific DI wiring (PlatformComponent)
└── commonTest/         # Unit tests (kotlin-test)
```

## Source Set Hierarchy

```
commonMain
    └── firebaseMain      (Firebase SDK available)
            ├── androidMain
            ├── iosMain
            │     ├── iosArm64Main
            │     └── iosSimulatorArm64Main
            └── jsMain
webMain (parallel branch, no Firebase)
```

## Tech Stack

| Concern | Library | Version |
|---|---|---|
| DI | kotlin-inject | 0.9.0 |
| Serialization | kotlinx.serialization | 1.11.0 |
| Coroutines | kotlinx.coroutines | 1.10.1 |
| DateTime | kotlinx.datetime | 0.6.1 |
| Settings | multiplatform-settings | — |
| Firebase | firebase-kotlin-sdk | 2.4.0 |
| ViewModel | androidx.lifecycle.viewmodel | — |

## Critical Conventions

### Clock access — ALWAYS use ClockUtils
```kotlin
// ✅ Correct
ClockUtils.now().toEpochMilliseconds()
ClockUtils.nowLocal()

// ❌ Never use directly
Clock.System.now()
```

### IDs — ALWAYS use IdGenerator
```kotlin
IdGenerator.generate("ses")  // → "ses.uuid"
IdGenerator.generate("ply")  // → "ply.uuid"
IdGenerator.generate("trx")  // → "trx.uuid"
```

### Firestore update — use updateFields DSL
```kotlin
// ✅
docRef.updateFields { "field" to value }
// ❌ deprecated
docRef.update("field" to value)
```

### Result type — use core/Result.kt (not kotlin stdlib Result) for domain layer

### Currency — Firestore stores cents (Int), UI shows dollars (Float)
```kotlin
CurrencyUtils.dollarsToCents(amount)   // Float → Int
CurrencyUtils.centsToDollars(amount)   // Int → Float
```

## What NOT to do
- Không import `kotlin.time.Clock` trực tiếp — dùng `ClockUtils`
- Không dùng `dayOfMonth`/`monthNumber` (deprecated) — dùng `date.day` / `date.month.ordinal + 1`
- Không thêm platform-specific code vào `commonMain`
- Không tạo use case chỉ wrap repository 1-1 không có logic gì
- Không dùng `flow { emit(...) }` khi `flowOf(...)` đủ dùng

## Adding a New Feature — Checklist
1. Domain model (nếu cần) → `commonMain/domain/model/`
2. Repository interface → `commonMain/domain/repository/`
3. Firestore service method → `firebaseMain/data/remote/`
4. Repository impl → `firebaseMain/data/repository/`
5. Use case → `commonMain/domain/usecase/`
6. ViewModel → `commonMain/presentation/viewmodel/`
7. Wire DI → `PokerComponent.kt` hoặc `PlatformComponent.kt`
