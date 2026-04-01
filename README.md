# 🔥 Burner OS: The Digital Ghost Protocol

[![Android API](https://img.shields.io/badge/API-23%2B-brightgreen.svg)](https://android-arsenal.com/api?level=23)
[![Security](https://img.shields.io/badge/Security-AES--256-red.svg)](#)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](#)

**Burner OS** is a high-security, encrypted "sub-operating system" designed to run inside Android. It provides a stealth environment for sensitive data, secure communications, and private browsing with a built-in "Self-Destruct" mechanism.

---

## 🛠 Features at a Glance

| Feature | Description |
| :--- | :--- |
| **🛡️ Biometric Login** | Secondary authentication layer using fingerprint or device PIN. |
| **📑 Secure Note Vault** | AES-256 encrypted notepad for passwords and sensitive text. |
| **🕵️ Stealth Web** | Private browser that purges cache, history, and cookies on exit. |
| **📇 Burner Contacts** | Isolated contact list that never syncs with the phone or cloud. |
| **📍 Secure Maps** | OpenStreetMap (OSM) integration for private, off-grid navigation. |
| **🧨 Duress PIN** | Entering `9999` triggers a silent wipe, making it look like the session never existed. |
| **🚨 Panic Wipe** | One-tap button to instantly purge all secure data and reset to normal UI. |

---

## 📸 Dashboard Preview

```text
    _________________________________
   | [🔓] Secure OS   [ 00:01:45 ]  |  <-- Live Session Timer
   |_________________________________|
   |                                 |
   |   [ 🔍 Stealth ]   [ 📝 Notes ]  |  <-- Encrypted App Grid
   |   [ 📇 Contacts]   [ 🗺️ Maps  ]  |
   |                                 |
   |_________________________________|
   |      [ PANIC WIPE (PURGE) ]     |  <-- Self-Destruct Button
   |_________________________________|
```

---

## 🚀 How It Works

### 1. Dual-Layer Entry
The app requires your device lock (Fingerprint/PIN) AND a secondary App PIN. 
*   **Normal PIN (`1234`)**: Grants access to the red Burner environment.
*   **Duress PIN (`9999`)**: Instantly wipes the vault and stays on the white "System Inactive" screen.

### 2. Encrypted Filesystem
All data is stored using `EncryptedSharedPreferences` with a hardware-backed `MasterKey`. Even if your phone is rooted, your Burner data remains a scrambled mess without the keys.

### 3. Stealth Connectivity
The Burner environment is designed to minimize your digital footprint. No background syncing, no telemetry, and (coming soon) full **Tor Integration** for network anonymity.

---

## 🛠 Installation & Setup

1.  **Clone the Repo**:
    ```bash
    git clone https://github.com/srisowmya2000/BurnerOS.git
    ```
2.  **Open in Android Studio**:
    *   Ensure you have JDK 11+ and Android SDK 33.
3.  **Build**:
    ```bash
    ./gradlew assembleDebug
    ```
4.  **Device Setup**:
    *   Go to **Settings > Security** on your phone and set a PIN/Pattern.
    *   Open the app and use default PIN `1234`.

---

## ⚠️ Disclaimer
*This project is intended for privacy research and personal security education. Always follow local laws regarding encryption and data management.*

---

## 🤝 Contributing
Want to make the Ghost Protocol even stronger? 
*   Add **Tor Proxy** support.
*   Implement a **Secret Photo Vault**.
*   Build a **Mock UI** for the duress mode.

**[MIT License](LICENSE)** | **Made for Privacy**
