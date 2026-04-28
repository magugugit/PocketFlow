# PocketFlow

A budget tracker Android app built with **Kotlin** and **Jetpack Compose** for the Part 2 — App Prototype Development assignment.

The interface is inspired by Uber: a monochrome black-and-white palette with a single green accent, and Inter typography for a clean, modern look.

---

## 📺 Demo Video

🔗 **Watch the demo on YouTube:** https://youtu.be/uEE5Bs3ljx0

The video walks through every required feature with a voice-over.

---

## ✨ Features

- **Login** with username and password
- **Categories** — eight built-in expense and six income categories, plus the ability to create custom budget categories at runtime
- **Add Expense** with all required fields:
  - Amount
  - Category
  - Date (system date picker)
  - Start time and end time (system time picker)
  - Description
  - Optional photograph (camera intent + FileProvider)
- **Min / Max monthly spending goal** — adjustable with a range slider (Compose's `RangeSlider` is the equivalent of an Android `SeekBar`)
- **User-selectable date range** filter (This Month / Last 7 Days / Last 30 Days / All Time) on:
  - Recent transactions list (Dashboard)
  - Spending-per-category breakdown (Reports)
- **Reports** — donut chart of spending by category and a 7-day bar chart, both computed live from RoomDB data
- **Goals** — set financial savings goals, contribute money toward them, edit, or delete; completing a goal awards XP and levels you up
- **Offline persistence** — all data saved locally to an offline **RoomDB** (SQLite) database

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.0.21 |
| UI | Jetpack Compose + Material 3 |
| Database | Room 2.6.1 (KSP) |
| Image loading | Coil 2.7 |
| Camera | `ActivityResultContracts.TakePicture` + AndroidX `FileProvider` |
| Navigation | Navigation Compose 2.8.4 |
| Typography | Inter via Compose downloadable Google Fonts |
| Min SDK | 26 (Android 8.0) |
| Target / Compile SDK | 35 |

---

## 📂 Project structure

```
app/src/main/java/com/pocketflow/app/
├── MainActivity.kt              # Single activity entry point; bootstraps Room
├── data/
│   ├── Models.kt                # Domain models (Transaction, BudgetCategory, FinancialGoal, DateRange…)
│   ├── AppState.kt              # Compose state holder; mirrors Room flows into snapshot state
│   ├── Repository.kt            # Maps entities ↔ domain; transactional helpers
│   ├── IconRegistry.kt          # String-key ↔ Material ImageVector mapping
│   └── db/
│       ├── AppDatabase.kt       # @Database; version 1
│       ├── Entities.kt          # Room entities
│       └── Daos.kt              # @Dao interfaces
├── navigation/NavGraph.kt       # Routes + bottom nav
├── screens/
│   ├── LoginScreen.kt
│   ├── DashboardScreen.kt
│   ├── AddTransactionScreen.kt  # Date/time pickers, camera, validation
│   ├── BudgetScreen.kt          # Min/max range slider + budget categories
│   ├── GoalsScreen.kt
│   └── ReportsScreen.kt
└── ui/
    ├── theme/                   # Color.kt, Font.kt, Theme.kt
    └── components/Components.kt # Reusable UI primitives
```

---

## 🚀 Build & Run

### Requirements
- Android Studio Ladybug (2024.2.1) or later
- JDK 11+
- An Android device or emulator running API 26+

### Steps
```bash
# Clone
git clone https://github.com/magugugit/PocketFlow.git
cd PocketFlow

# Open the folder in Android Studio:
# 1. File → Open → select the project root
# 2. Wait for Gradle sync
# 3. Click Run ▶
```

The first launch will download the Inter font from Google Play Services Font Provider. It falls back to the system sans-serif if offline.

---

## ✅ Assignment requirements checklist

| Requirement | Where it's implemented |
|---|---|
| Login with username/password | `LoginScreen.kt` |
| Create categories | `BudgetScreen.kt` (Add Budget dialog) |
| Expense with date / start time / end time / description / category | `AddTransactionScreen.kt` |
| Optional photograph | `AddTransactionScreen.kt` — `ActivityResultContracts.TakePicture` |
| Min and max monthly spending goal | `BudgetScreen.kt` — `MonthlyGoalCard` |
| List for user-selectable period | `DashboardScreen.kt` — `PeriodFilterRow` |
| Total per category for user-selectable period | `ReportsScreen.kt` — `CategoriesTab` |
| Save data to RoomDB / SQLite | `data/db/*` |
| EditText (text input) | All `OutlinedTextField` usages |
| `NumberFormat` | `DashboardScreen.formatRand` |
| SeekBar | `BudgetScreen.MonthlyGoalCard` (`RangeSlider`) |
| Apply intent | Camera intent in `AddTransactionScreen.kt` |
| Reading & writing to RoomDB | `Repository.kt` |
| Comments | Inline KDoc / line comments throughout |
| Logging | `Log.d` / `Log.e` in `Repository`, `AppState`, `MainActivity`, `AddTransactionScreen` |
| Handles invalid input without crashing | `require()` + `runCatching` wrappers + amount sanitisation |

---

## 📦 Submission

- Source code → this repository
- README → this file
- Demo video → https://youtu.be/uEE5Bs3ljx0
- Built APK → in the `Releases` section / submitted alongside

---

## 👤 Author

Built by **Abonga Magugu** for the App Development module.
