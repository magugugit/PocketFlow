# PocketFlow

A budget-tracker Android app built with **Kotlin** and **Jetpack Compose** for the
**Part 3 — Final App Development** assignment.

The interface is inspired by Uber: a monochrome black-and-white palette with a single
green accent, and Inter typography for a clean, modern look.

> **Built by Team PocketFlow — Abonga Magugu (ST10298002) & Percy Dube (ST10383359)** for the App Development module.

---

## 📺 Demonstration Video

🔗 **Watch the demo on YouTube:** https://youtu.be/uEE5Bs3ljx0

The video runs on a **physical Android phone** and walks through **every feature** with a
voice-over explaining what is being shown.

---

## ✨ What's new in Part 3

The final build adds the features that were **not** required for the Part 2 prototype, and
acts on the prototype feedback (especially the user-interface notes):

| Part 3 requirement | Where it lives |
|---|---|
| **Graph: amount spent _per category_ over a user-selectable period, with the minimum and maximum goals drawn on it** | `ReportsScreen.kt → CategoryGoalChart` ("Spend vs Goals" tab) |
| **Visual display of how well the user stays between their min/max goals over the past month** | `ReportsScreen.kt → GoalBandGauge` + `BudgetScreen.kt → MonthlyGoalCard` |
| **Gamification** integrated into the app | `GoalsScreen.kt` — XP, levels, achievements, level-up on goal completion |
| **Two own features** (see below) | Financial Goals + Reports/Analytics |
| **App icon & final image assets** | `res/mipmap-*`, `res/drawable/ic_launcher_*` |
| **Automated testing + GitHub Actions** | `app/src/test/...` + `.github/workflows/build.yml` |
| **Runs on a real mobile phone** (not the emulator) | shown in the demo video |

---

## 🌟 My two own features

These are the two custom features built on top of the brief's required functionality.
Both are documented here so the lecturer knows exactly what to look for.

### 1. Financial Goals (gamified savings)
`screens/GoalsScreen.kt`

- Create, edit and delete savings **goals**, each with a target amount and due date.
- **Contribute money** toward a goal and watch an animated progress bar fill up.
- Completing a goal awards **XP**; accumulating XP increases your **Level**
  (`Analytics.levelForXp`) and unlocks **achievement badges** — this is the app's
  **gamification** layer.

### 2. Reports & Analytics
`screens/ReportsScreen.kt`

- **Spend vs Goals** — a bar chart of spending **per category** over a user-selectable
  period, with the **min and max monthly goals overlaid as reference lines**.
- **Goal-band gauge** — a colour-coded gauge showing whether this month's spending is
  **under / within / over** the goal band.
- **Breakdown** — a donut chart of spend share per category.
- **Weekly** — a 7-day bar chart of daily expense totals.

All charts are computed **live** from RoomDB data and respond to the period filter.

---

## ✨ Full feature list

- **Login** with username and password
- **Categories** — built-in expense & income categories, plus custom budget categories at runtime
- **Add Expense** with amount, category, date picker, start/end time pickers, description,
  and an optional **photograph** (camera intent + FileProvider)
- **Min / Max monthly spending goal** — set with a `RangeSlider` (Compose's two-thumb SeekBar)
- **User-selectable period** filter (This Month / Last 7 Days / Last 30 Days / All Time)
- **Reports** — Spend-vs-Goals bar chart, goal-band gauge, donut chart and weekly bar chart
- **Goals & gamification** — XP, levels and achievement badges
- **Offline persistence** — all data saved locally to **RoomDB** (SQLite)

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.0.21 |
| UI | Jetpack Compose + Material 3 |
| Database | Room 2.6.1 (KSP) |
| Charts | Custom Compose `Canvas` drawing (no third-party chart lib) |
| Image loading | Coil 2.7 |
| Camera | `ActivityResultContracts.TakePicture` + AndroidX `FileProvider` |
| Navigation | Navigation Compose 2.8.4 |
| Build | AGP 8.7.3 / Gradle 8.9 |
| CI | GitHub Actions (JDK 17) |
| Min SDK | 26 (Android 8.0) · Target/Compile SDK | 35 |

---

## 📂 Project structure

```
app/src/main/java/com/pocketflow/app/
├── MainActivity.kt              # Single-activity entry point; bootstraps Room
├── data/
│   ├── Models.kt                # Domain models (Transaction, BudgetCategory, FinancialGoal, DateRange…)
│   ├── Analytics.kt             # Pure, unit-tested calc layer (goal band, XP/level, category totals)
│   ├── AppState.kt              # Compose state holder; mirrors Room flows into snapshot state
│   ├── Repository.kt            # Maps entities ↔ domain; transactional helpers
│   ├── IconRegistry.kt          # String-key ↔ Material ImageVector mapping
│   └── db/                      # Room @Database, @Entity and @Dao definitions
├── navigation/NavGraph.kt       # Routes + bottom nav
├── screens/
│   ├── LoginScreen.kt
│   ├── DashboardScreen.kt
│   ├── AddTransactionScreen.kt  # Date/time pickers, camera, validation
│   ├── BudgetScreen.kt          # Min/max range slider + budget categories
│   ├── GoalsScreen.kt           # 🌟 Own feature 1 — gamified goals
│   └── ReportsScreen.kt         # 🌟 Own feature 2 — charts incl. Spend-vs-Goals graph
└── ui/                          # theme/ + reusable components/

app/src/test/java/com/pocketflow/app/
├── AnalyticsTest.kt             # Goal-band, XP/level and range-fraction tests
└── DateRangeTest.kt             # User-selectable period preset tests

.github/workflows/build.yml      # GitHub Actions: run tests + build APK
```

---

## ✅ Automated testing

Unit tests cover the core calculation logic that powers the charts and the goal gauge:

- **`AnalyticsTest`** — `goalBand` classification (under/within/over), `levelForXp` /
  `xpToNextLevel` gamification maths, and `fractionOfRange` clamping.
- **`DateRangeTest`** — the user-selectable period presets and inclusive boundaries.

Run them locally:

```bash
./gradlew testDebugUnitTest
```

The HTML report lands in `app/build/reports/tests/testDebugUnitTest/index.html`.

---

## ⚙️ Continuous Integration (GitHub Actions)

`.github/workflows/build.yml` runs on every push and pull request to `main`:

1. Checks out the code and sets up **JDK 17**.
2. Runs the unit tests — `./gradlew testDebugUnitTest`.
3. Builds the debug APK — `./gradlew assembleDebug`.
4. Uploads the **APK** and the **test report** as downloadable build artifacts.

This guarantees the project compiles and passes tests on a clean machine, not just locally.

---

## 🚀 Build & Run

### Requirements
- Android Studio Ladybug (2024.2.1) or later
- JDK 17
- An Android **device** running API 26+ (the final app is meant to run on a real phone)

### Steps
```bash
git clone https://github.com/magugugit/PocketFlow.git
cd PocketFlow
# Open the folder in Android Studio → wait for Gradle sync → Run ▶ on your device
```

The first launch downloads the Inter font from the Google Play Services Font Provider; it
falls back to the system sans-serif if offline.

---

## 📦 Submission

- **Source code** → this GitHub repository (no zip)
- **README** → this file
- **Two own features** → documented above (Goals + Reports/Analytics)
- **Demo video** → https://youtu.be/uEE5Bs3ljx0
- **Built APK** → CI artifact (`pocketflow-debug-apk`) or `app/build/outputs/apk/debug/`
- **Research & design documents** → as submitted in Part 1

---

## 👤 Authors

Built as a team for the App Development module:

| Name | Student number | GitHub |
|---|---|---|
| **Abonga Magugu** | ST10298002 | [@magugugit](https://github.com/magugugit) |
| **Percy Dube** | ST10383359 | [@PSYCHOHITMAN](https://github.com/PSYCHOHITMAN) |
