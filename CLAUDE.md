# CLAUDE.md — Poker Manager

## Team Structure
3 roles only: **PO** · **Android Dev** · **iOS Dev**
Every response follows: [PO] → [Android] → [iOS] → [Sync & Next]

## Per-Module CLAUDE.md (Role-based)

| Module | File | Developer Role |
|---|---|---|
| `shared/` | [shared/CLAUDE.md](shared/CLAUDE.md) | Senior KMP + Android Developer |
| `composeApp/` | [composeApp/CLAUDE.md](composeApp/CLAUDE.md) | KMP Android Developer (Compose Multiplatform) |
| `iosApp/` | [iosApp/CLAUDE.md](iosApp/CLAUDE.md) | KMP iOS Developer (Swift + SwiftUI bridge) |

**Khi làm việc với một module cụ thể, đọc CLAUDE.md của module đó trước.**

## Project Overview
Poker session manager — host creates sessions, tracks buy-in/cash-out/transfers, calculates net P&L per player. Targets Android + iOS (shared KMP business logic).

## Architecture Principles
- **KMP shared logic**: domain models, use cases, repositories live in `shared/`
- **Platform UI**: `composeApp/` for Android/Web, `iosApp/` for iOS — each platform owns its own UI
- **MVVM light**: ViewModel + StateFlow on Android, @Observable on iOS — no Clean Architecture overkill
- **No over-engineering**: add a layer only when it solves a real problem

## Stack
| Layer | Android | iOS | Shared |
|---|---|---|---|
| UI | Compose Multiplatform | SwiftUI | — |
| State | ViewModel + StateFlow | @Observable | — |
| DI | kotlin-inject | — | kotlin-inject |
| Network | Firebase SDK | Firebase SDK | Firestore abstraction |
| Local | In-memory + Settings | In-memory + Settings | InMemoryDatabase |

## Workflow (per feature)
1. PO writes `SPEC.md` (User Story + AC + scope)
2. Android & iOS implement in parallel
3. PO reviews → approve or request change
4. Merge, update SPEC.md status → next feature

## File Conventions
- Specs: `docs/specs/FEATURE_NAME.md`
- One feature = one small PR-equivalent change set
- No dead code, no TODOs left in files

## What NOT to do
- No Redux / MVI / multi-module unless absolutely needed
- No mock frameworks in prod code
- No AI Engineer / QA / Designer roles
- No placeholder screens — every delivered screen must be functional

## Current Status
- ClockUtils extraction in progress (testable clock wrapper)
- Firebase Auth + Firestore connected
- Core session flow (create/buy-in/cash-out/transfer/complete) implemented
