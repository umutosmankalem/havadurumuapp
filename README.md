# Nimbus

Nimbus is an Android weather app. It shows the current conditions, the next hours, and a 7-day forecast for your location or any city you search for.

Forecasts come from [Open-Meteo](https://open-meteo.com/), which does not need an API key. City search uses the Open-Meteo geocoding API, so places are not limited to a fixed list. Names with Turkish characters, such as Çanakkale, İzmir, and Şanlıurfa, are sent to the geocoder as typed.

## Architecture

The project is split so the domain stays free of Android, Retrofit, and Room:

- `core` — dispatchers and clock
- `domain` — models, repository contracts, use cases
- `data` — Open-Meteo, Room cache, favorites, location, settings
- `presentation` — Compose UI, ViewModels, navigation
- `app` — application entry and dependency injection

Presentation depends on domain. Data depends on domain. The domain module does not depend on either.

## Features

- Current temperature, feels-like, humidity, wind, sunrise, sunset, UV, and rain chance
- Hourly and 7-day forecasts
- Weather-specific Compose animations
- Location permission handling, with city search when location is unavailable
- Favorites stored in Room
- Offline fallback that labels saved weather
- Light, dark, and system themes

## Run

Open the project in Android Studio (AGP 9.4, JDK 17+) and run the `app` configuration.

From the command line, with the Android SDK installed:

```bash
./gradlew :app:assembleDebug :domain:test :data:test :presentation:test
```

`minSdk` is 26. `compileSdk` is 37.
