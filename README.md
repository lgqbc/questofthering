# Quest of the Ring

An Android step counter app that lets you walk the path of the Fellowship of the Ring through Middle-earth!

## Overview

This app tracks your daily steps and maps them to the Fellowship's journey from the Shire to Mount Doom. As you walk in real life, you'll progress through iconic locations from The Lord of the Rings, with events and quests along the way.

## Features

- **Step Counting**: Uses your phone's built-in step counter sensor
- **Journey Tracking**: 15 iconic locations from Bag End to Mount Doom
- **Interactive Map**: Beautiful SVG map of Middle-earth showing your current location
- **Persistent Progress**: Your journey progress is saved locally
- **Scaled Journey**: The ~1,250 mile journey is scaled down 10x to be achievable (~250,000 steps total)

## The Journey

1. **The Shire - Bag End** (Start)
2. **Bucklebury Ferry** (8,000 steps)
3. **Bree** (16,000 steps)
4. **Weathertop** (24,000 steps)
5. **Rivendell** (40,000 steps)
6. **Moria** (80,000 steps)
7. **Lothlórien** (96,000 steps)
8. **Amon Hen** (116,000 steps)
9. **Emyn Muil** (130,000 steps)
10. **The Dead Marshes** (150,000 steps)
11. **The Black Gate** (170,000 steps)
12. **Ithilien** (190,000 steps)
13. **Cirith Ungol** (210,000 steps)
14. **Plains of Gorgoroth** (230,000 steps)
15. **Mount Doom** (250,000 steps) - Victory!

## Technical Stack

- **Language**: Kotlin
- **Min SDK**: API 26 (Android 8.0)
- **Target SDK**: API 34 (Android 14)
- **Key Libraries**:
  - AndroidSVG - For rendering the Middle-earth map
  - DataStore - For persistent storage
  - Material Components - For UI
  - AndroidX Core & AppCompat

## Project Structure

```
questofthering/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/questofthering/app/
│   │       │   ├── MainActivity.kt          # Main activity with step counter
│   │       │   ├── MapView.kt               # Custom view for SVG map
│   │       │   └── JourneyManager.kt        # Journey progress logic
│   │       ├── res/
│   │       │   ├── layout/
│   │       │   │   └── activity_main.xml    # Main UI layout
│   │       │   └── values/
│   │       │       ├── strings.xml
│   │       │       ├── colors.xml
│   │       │       └── themes.xml
│   │       ├── assets/
│   │       │   └── map.svg                  # Middle-earth map
│   │       └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/
└── build.gradle.kts
```

## How to Build

### Prerequisites

1. **Install Android Studio**: Download from https://developer.android.com/studio
2. **Install Java JDK**: Version 11 or higher

### Building the App

1. **Open the project in Android Studio**:
   ```bash
   # From Android Studio: File > Open > Select the questofthering folder
   ```

2. **Sync Gradle**: Android Studio will automatically sync dependencies
   - If not, click: File > Sync Project with Gradle Files

3. **Build the app**:
   - From menu: Build > Make Project
   - Or use command line:
     ```bash
     ./gradlew build
     ```

4. **Run on device or emulator**:
   - Click the green "Run" button in Android Studio
   - Or use command line:
     ```bash
     ./gradlew installDebug
     ```

### Command Line Build (without Android Studio)

```bash
# Install Android SDK command line tools first
# Then:

# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Build release APK (unsigned)
./gradlew assembleRelease
```

## Permissions

The app requires:
- **ACTIVITY_RECOGNITION** (Android 10+): To access the step counter sensor

Users will be prompted to grant this permission when they first open the app.

## How It Works

### Step Counting
- Uses `Sensor.TYPE_STEP_COUNTER` hardware sensor
- Counts cumulative steps since device boot
- Calculates daily steps by tracking the initial count
- Updates progress in real-time

### Progress Tracking
- Steps are mapped to the 15 journey locations
- Progress is calculated as: `(current_steps / 250,000) * 100%`
- Current location is determined by which milestone you've passed
- All progress is saved using DataStore (survives app restarts)

### Map Display
- SVG map is loaded from assets and rendered using AndroidSVG library
- A marker shows your current position (will be enhanced with actual route plotting)
- Map is zoomable and scrollable (to be implemented)

## Future Enhancements

This is a basic foundation. Here are ideas for expansion:

### Short Term
- [ ] Plot the actual Fellowship route on the map with waypoints
- [ ] Animate the position marker as you progress
- [ ] Add sound effects and music from the movies
- [ ] Create events/encounters at each location

### Medium Term
- [ ] Quest system with challenges at each location
- [ ] Achievement badges
- [ ] Weather effects on the map based on location
- [ ] Day/night cycle matching your local time
- [ ] Share progress on social media

### Long Term
- [ ] Multiple route options (Aragorn's path, etc.)
- [ ] Multiplayer - journey with friends
- [ ] AR mode to see Middle-earth around you
- [ ] Widget for home screen

## Development Notes

### Adding Events/Quests

To add events at locations, create a new `Event` data class in JourneyManager.kt:

```kotlin
data class Event(
    val title: String,
    val description: String,
    val location: Location,
    val type: EventType  // Story, Quest, Battle, etc.
)
```

### Customizing the Journey

Edit the `locations` list in `JourneyManager.kt` to:
- Add more waypoints
- Change step requirements
- Modify location descriptions

### Map Customization

The `MapView.kt` class can be enhanced to:
- Draw the actual route path
- Add location markers
- Implement zoom/pan gestures
- Highlight the current location

## Troubleshooting

**Build fails with "SDK not found"**:
- Create `local.properties` file with:
  ```
  sdk.dir=/path/to/your/Android/sdk
  ```

**Step counter not working**:
- Ensure your device has a step counter sensor (most phones since 2014 do)
- Check that permission was granted
- Try restarting the device

**Map not displaying**:
- Verify `map.svg` is in `app/src/main/assets/`
- Check logcat for SVG parsing errors
- The SVG must be valid XML

## Contributing

This is a personal project but feel free to fork and extend it!

## License

This is a fan project based on J.R.R. Tolkien's works. The map and location names are used for educational/fan purposes.

## Credits

- Map: Middle-earth SVG map (included in project)
- Inspired by The Lord of the Rings by J.R.R. Tolkien
- Built with love for the journey ⚔️🧙‍♂️🗡️
