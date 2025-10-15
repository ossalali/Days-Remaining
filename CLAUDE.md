# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Days Remaining is an Android application that helps users track important events and see how many days remain until they occur (or have passed since they happened). Built with Kotlin, Jetpack Compose, and modern Android architecture patterns.

## Build Commands

### Building the App
```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install debug build on connected device
./gradlew installDebug
```

### Code Formatting
```bash
# Format all code with ktfmt
./gradlew ktfmtFormat

# Check code formatting
./gradlew ktfmtCheck
```

### Testing
```bash
# Run unit tests
./gradlew test

# Run unit tests for debug variant
./gradlew testDebugUnitTest

# Run instrumented tests on connected device
./gradlew connectedAndroidTest
```

### Cleaning
```bash
# Clean build artifacts
./gradlew clean
```

## Architecture

### Technology Stack
- **UI**: Jetpack Compose with Material 3 (using alpha version 1.4.0-rc01)
- **Navigation**: Navigation 3 with kotlinx-serialization for type-safe routes
- **DI**: Dagger Hilt for dependency injection
- **Database**: Room with LocalDate type converters
- **Widgets**: Glance for home screen widgets
- **Background Work**: WorkManager for auto-archiving events
- **State Management**: StateFlow and Compose state
- **Image Loading**: Coil for image handling

### Package Structure
- `model/`: Data models (EventItem is the core entity)
- `infrastructure/`: Data layer (Room database, DAO, repository, logger, image storage)
- `presentation/`: UI layer organized into:
  - `ui/`: Composable screens and components
  - `viewmodel/`: ViewModels extending BaseViewModel
- `navigation/`: Type-safe Navigation 3 routes defined as sealed interface
- `di/`: Hilt modules (AppModule, DispatcherModule, WidgetModule)
- `widget/`: Glance widget implementation with responsive sizing
- `settings/`: Settings repository (DataStore) and auto-archive feature (WorkManager)
- `businesslogic/`: Use cases and business logic

### Key Architectural Patterns

**Dependency Injection with Hilt**
- Application uses `@HiltAndroidApp` (App.kt)
- Activities use `@AndroidEntryPoint` (MainActivity.kt)
- ViewModels use `@HiltViewModel`
- Widget-specific dependencies accessed via `EntryPointAccessors` (WidgetRepositoryEntryPoint)

**Database Layer**
- Single Room database (MyDatabase) with EventDao
- LocalDate stored as Long using Converters.kt
- Repository pattern: EventRepository wraps DAO operations
- Migration support: Currently at version 2 (added imageUri field)

**Navigation**
- Type-safe routes using Navigation 3 with sealed interface DaysRoute
- Routes are kotlinx-serializable data objects/classes
- Main routes: EventListRoute, EventDetailsRoute, AddEventRoute, SettingsRoute, DebugRoute (debug builds only)

**Widget Architecture**
- EventWidget uses Glance with responsive SizeMode
- Widget preferences stored in WidgetDataStore (per widget instance)
- Widget displays selected events from EventRepository
- Supports 15+ different size configurations with adaptive layouts

**Custom Date Notation**
- EventItem.getDateNotation() provides human-readable format (e.g., "2y 3m 1w 5d")
- Configurable via settings to toggle between custom notation and day count
- Special handling for edge cases (rounding weeks, month+day display)

### State Management
- ViewModels use StateFlow for state
- BaseViewModel provides common functionality
- UI observes state as Compose State via collectAsStateWithLifecycle()

### Auto-Archive Feature
- AutoArchiver schedules WorkManager tasks to archive past events
- Runs at midnight daily when enabled
- Settings control enable/disable via SettingsRepository (DataStore)
- Uses both periodic and one-time work requests

## Build Configuration

- **Namespace**: com.ossalali.daysremaining
- **Min SDK**: 29 (Android 10)
- **Target/Compile SDK**: 36
- **Java Version**: 17
- **Debug Variant**: Adds ".debug" suffix and separate icon
- **Release Variant**: Enables minification and shrinking with ProGuard

## Important Implementation Details

### EventItem Model
- Primary entity stored in Room
- Contains: id, title, date (LocalDate), description, imageUri (optional), isArchived
- Includes date calculation logic with custom notation format
- Indexed on isArchived, date, title, description for query performance

### Image Storage
- Images stored internally via ImageStorage.kt
- FileProvider configuration in xml/file_paths.xml
- Images associated with events via imageUri field

### Widget Updates
- Widget refreshes via EventWidgetReceiver
- Responsive sizing with 15+ predefined DpSize configurations
- Uses getWidgetUiState() to determine layout based on available space
- Widget preferences stored per-widget instance using appWidgetId

### Settings
- SettingsRepository uses DataStore Preferences
- Key settings: customDateNotation, autoArchive
- Settings screen includes debug features in debug builds

## Debug Features
- Debug build includes DebugScreen with AddDebugEventsUseCase
- Separate debug app icon and name
- Debug menu accessible from settings in debug builds only
