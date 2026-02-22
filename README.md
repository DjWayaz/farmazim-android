# FarmaZim 🌱

Offline-first farm management app for smallholder farmers in Zimbabwe.

## Features
- **Crop Management** — Log planting dates, harvest records, yields, and sales per plot
- **Input Tracking** — Record fertiliser, pesticide, seed, and labour costs
- **Livestock** *(Premium)* — Track animals, species counts, and health events
- **Finance** *(Premium)* — Profit & loss summary per season

## Freemium
- **Free:** Up to 2 plots, crop & input logging
- **Premium:** Unlimited plots, livestock, financial reports

## Build
This project uses GitHub Actions to auto-build a debug APK on every push to `main`.  
Download the APK from the **Actions** tab → latest run → **farmazim-debug-apk** artifact.

## Tech Stack
- Kotlin + Jetpack Compose
- Room (offline-first SQLite)
- Hilt (dependency injection)
- Google Play Billing
- MVVM + Clean Architecture

## Languages
- English (default)
- Shona placeholder: `res/values-sn/strings.xml`
- Ndebele placeholder: `res/values-nd/strings.xml`
