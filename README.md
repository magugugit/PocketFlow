# PocketFlow – Android App (Jetpack Compose)

Your Smart Budget Companion, built with **100% Kotlin + Jetpack Compose**.

---

## Screens

| Screen | File |
|--------|------|
| Login / Sign Up | `LoginScreen.kt` |
| Dashboard (Home) | `DashboardScreen.kt` |
| Add Transaction | `AddTransactionScreen.kt` |
| Reports & Analytics | `ReportsScreen.kt` |
| Budget Tracker | `BudgetScreen.kt` |
| Financial Goals | `GoalsScreen.kt` |

---

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Navigation**: Navigation Compose (`2.8.4`)
- **Charts**: Custom Canvas drawing (no third-party chart library needed)
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 35 (Android 15)

---

## Project Structure

```
app/src/main/java/com/pocketflow/app/
├── MainActivity.kt
├── data/
│   └── Models.kt           ← data classes + mock data
├── navigation/
│   └── NavGraph.kt         ← NavHost + bottom navigation bar
├── screens/
│   ├── LoginScreen.kt
│   ├── DashboardScreen.kt
│   ├── AddTransactionScreen.kt
│   ├── ReportsScreen.kt
│   ├── BudgetScreen.kt
│   └── GoalsScreen.kt
└── ui/
    ├── components/
    │   └── Components.kt   ← shared composables
    └── theme/
        ├── Color.kt
        └── Theme.kt
```

---

## Getting Started in Android Studio

### Step 1 – Open the project
1. Open **Android Studio** (Ladybug / 2024.2+ recommended).
2. Click **File → Open** and select the `PocketFlow` folder.
3. Wait for Gradle sync to complete (downloads ~400 MB on first run).

### Step 2 – Run the app
- Connect a physical device **or** start an AVD (API 26+).
- Click ▶ **Run** or press `Shift+F10`.

### Step 3 – Log in
- Tap **Login** (any email/password works — auth is mocked).

---

## Connecting a Real Backend (Supabase)

To persist data, add these dependencies to `app/build.gradle.kts`:

```kotlin
implementation("io.github.jan-tennert.supabase:postgrest-kt:2.6.1")
implementation("io.github.jan-tennert.supabase:auth-kt:2.6.1")
implementation("io.ktor:ktor-client-android:2.3.12")
```

Then create a `SupabaseClient` singleton and replace the `MockData` calls
with Supabase queries in each ViewModel.

---

## Color Palette

| Token | Hex | Usage |
|-------|-----|-------|
| `PrimaryGreen` | `#10B981` | Buttons, selected state, income |
| `PrimaryTeal` | `#0891B2` | Links, secondary actions |
| `GradientStart` | `#0EA5E9` | Dashboard header |
| `GradientEnd` | `#10B981` | Dashboard header |
| `ExpenseOrange` | `#EA580C` | Expenses, warnings |
| `XpGold` | `#F59E0B` | XP bar, achievements |

---

## Roadmap / TODOs

- [ ] Room database for local persistence
- [ ] Supabase integration for cloud sync + real auth
- [ ] Recurring transactions
- [ ] Export to CSV / PDF
- [ ] Push notifications for budget alerts
- [ ] Dark mode
- [ ] Widgets (Glance API)
