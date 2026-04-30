# InsightApp 

A health tracking Android app built with **Jetpack Compose** that provides visual insights into menstrual cycle patterns, symptoms, weight trends, and lifestyle habits.

---
[![Watch Demo](https://img.shields.io/badge/Watch-Demo-blue?style=for-the-badge)](https://drive.google.com/file/d/1cmrKF5zO2F6j1OIxsZuWThxOQqSvX8QQ/view)

##  Features

### Insights Screen (Main)
A fully scrollable dashboard of health analytics, composed of five distinct sections:

| Section | Description |
|---|---|
| **Stability Summary** | A multi-layer area chart showing hormonal/cycle stability score over months, with an improving/declining indicator |
| **Cycle Trends** | Paginated bar charts visualising menstrual cycle length, menstrual fraction, and ovulation position per month |
| **Body & Metabolic Trends** | A line chart with gradient fill tracking weight fluctuations over time |
| **Symptom Trends** | A donut chart breaking down symptom distribution (Bloating, Mood, Fatigue, Acne) with percentage labels |
| **Lifestyle Impact** | A colour-coded heatmap grid showing intensity of Sleep, Hydration, Caffeine, and Exercise habits |


---

##  Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI Framework | Jetpack Compose |
| Navigation | Navigation Compose |
| Design System | Material 3 |
| Charts | Custom Canvas-drawn (no third-party chart library) |
| Architecture | Single-module, screen-based organisation |
| Build System | Gradle (Kotlin DSL) with Version Catalog |

---

##  Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- JDK 11
- Android SDK with `compileSdk = 36`, `minSdk = 24`

### Clone & Run

```bash
git clone https://github.com/pratish444/HireMyIdea
cd InsightApp
```

Open the project in Android Studio, let Gradle sync, then run on an emulator or physical device (Android 7.0+).

### Build via CLI

```bash
./gradlew assembleDebug
```

The debug APK will be output to `app/build/outputs/apk/debug/`.

---



##  License

This project is for personal/educational use.
