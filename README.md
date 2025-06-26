# Days Remaining

**Days Remaining** is a modern Android app for tracking important events and counting down the days
until (or since) they occur. Built with Jetpack Compose, Hilt, Room, and the latest AndroidX
libraries, it offers a clean, responsive UI and robust event management features.

## Features

- **Event Tracking:** Add, view, and manage events with titles, descriptions, and target dates.
- **Days Counter:** See at a glance how many days remain until each event, or how many days have
  passed.
- **Search & Filter:** Quickly find events using the animated search bar and filter chips (Active,
  Archived, Deleted).
- **Event Details:** Tap any event to view its details.
- **Archive & Delete:** Archive or delete events with a single tap.
- **Material You Design:** Uses Material 3 and theming for a beautiful, adaptive UI.
- **Navigation:** Modern navigation using Navigation3.
- **Debug & Settings Screens:** Access debug and settings screens for advanced options.
- **Widget Support:** Add a home screen widget to see your events at a glance.
- **Dark Mode:** Full support for light and dark themes.

## Screenshots

<!-- Add screenshots here if available -->

## Getting Started

### Prerequisites

- Android Studio Giraffe or newer
- Android SDK 29+
- Kotlin 1.9+
- Gradle 8+

### Build & Run

1. Clone the repository:
   ```sh
   git clone https://github.com/yourusername/days-remaining.git
   cd days-remaining
   ```
2. Open in Android Studio.
3. Sync Gradle and run the app on an emulator or device.

## Project Structure

```
app/
  src/
    main/
      java/com/ossalali/daysremaining/
        businesslogic/      # Use cases and business logic
        di/                 # Dependency injection modules (Hilt)
        infrastructure/     # Data layer (Room, DAOs, Repos)
        model/              # Data models (EventItem, etc.)
        navigation/         # Navigation keys/routes
        presentation/
          ui/               # Jetpack Compose UI screens & components
          viewmodel/        # ViewModels (MVVM)
        widget/             # App widget code
      res/                  # Resources (drawables, layouts, values)
```

## Tech Stack

- **Kotlin** & **Jetpack Compose** for UI
- **Hilt** for dependency injection
- **Room** for local data storage
- **Navigation3** for navigation
- **Material 3** for theming
- **Accompanist** for system UI control
- **DataStore** for preferences
- **JUnit, Mockito** for testing

## Architecture

The app follows Clean Architecture principles with MVVM pattern:

- **Presentation Layer:** Jetpack Compose UI with ViewModels
- **Domain Layer:** Use cases and business logic
- **Data Layer:** Room database, repositories, and data sources

## Key Components

### EventItem Model

The core data model representing events with:

- Title and description
- Target date
- Archive status
- Automatic days calculation

### Navigation

Uses Navigation3 for type-safe navigation between:

- Event List Screen
- Event Details Screen
- Archive Screen
- Settings Screen
- Debug Screen

### Widget

Home screen widget for quick event viewing without opening the app.

## Contributing

Contributions are welcome! Please open issues or pull requests for new features, bug fixes, or
suggestions.

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details. 

