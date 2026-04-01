# BurnerOS


<p align="center">
  <b>BurnerOS</b> is an Android-based secure workspace built for privacy-sensitive workflows.<br/>
  It provides an isolated environment for encrypted notes, private browsing, burner contacts, and fast local data purge controls.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android%20API%2023%2B-brightgreen?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Security-AES--256-red?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Storage-Hardware%20Backed%20Keys-blue?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Status-Research%20Prototype-orange?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Focus-Mobile%20Privacy%20%26%20Security-black?style=for-the-badge" />
</p>

---

## Overview

Modern smartphones are convenient, but they leave traces:

- synced contacts
- browser history
- cookies and cache
- plaintext notes
- app telemetry
- accidental cloud backups
- data leakage on public Wi-Fi

**BurnerOS** is designed as a **secure mini-workspace inside Android** for situations where you want stronger privacy separation from your normal device activity.

It is not a replacement operating system.  
It is a **contained, encrypted, privacy-focused layer** built for short-lived sensitive workflows.

---

## Why BurnerOS matters

BurnerOS is useful when you need:

- temporary secure note storage
- isolated browsing sessions
- private contacts that never sync
- reduced local traces after use
- quick emergency local wipe
- a mobile privacy research platform

### Example use cases

- Security conferences on public Wi-Fi
- Handling temporary credentials or OTP backup notes
- Privacy-sensitive travel workflows
- Mobile security demonstrations / research
- Field testing for secure local-only data handling

---

## Core Features

| Feature | Description | Security Value |
|---|---|---|
| **Biometric + PIN Access** | Uses device auth plus secondary in-app PIN | Adds an extra trust boundary |
| **Secure Note Vault** | Encrypted local note storage for sensitive text | Protects notes, passwords, tokens |
| **Stealth Browser** | Private browsing session with cleanup on exit | Reduces local browsing residue |
| **Burner Contacts** | Local-only contacts that do not sync to the device or cloud | Prevents contact leakage |
| **Secure Maps** | Privacy-oriented map usage using OpenStreetMap-style integration | Avoids mainstream tracking-heavy map workflows |
| **Emergency Purge** | One-tap local wipe of BurnerOS workspace data | Fast response for device loss or end-of-session cleanup |
| **Emergency PIN Flow (Prototype)** | Alternate PIN can trigger a silent local workspace reset | Research concept for privacy-preserving emergency access flows |

---

## How BurnerOS Works

BurnerOS is built around **four core layers**:

### 1. Secondary Authentication Layer
BurnerOS does not rely only on the device lock screen.

It can require:

- **Device unlock** (fingerprint / system PIN / pattern)
- **Secondary app PIN**

This means someone with casual access to your unlocked phone still should not directly access the BurnerOS workspace.

---

### 2. Encrypted Local Storage
Sensitive data is stored using Android’s secure storage stack:

- `EncryptedSharedPreferences`
- `MasterKey`
- Android Keystore
- hardware-backed key storage (when supported by the device)

This means data stored by BurnerOS is intended to remain unreadable if someone simply pulls app files from storage.

---

### 3. Ephemeral Session Hygiene
BurnerOS is designed to leave fewer traces than normal app usage.

Examples:

- browser cache cleanup
- cookie cleanup
- history cleanup
- local-only storage model
- no cloud sync by design
- minimal persistent operational data

This makes it suitable for **temporary sensitive workflows** rather than permanent storage.

---

### 4. Emergency Local Purge
BurnerOS includes a fast local wipe/reset concept.

This allows:

- clearing sensitive notes
- clearing burner contacts
- resetting local app state
- ending the session with minimal residue

> **Important:** This feature is intended for privacy, personal data minimization, and safe handling of temporary sensitive data. It is not intended for evading lawful obligations or misuse.

---

## How to Use BurnerOS

### Normal usage flow

1. **Unlock your phone**
2. **Open BurnerOS**
3. **Authenticate**
   - Use fingerprint / device credential if prompted
   - Enter your secondary BurnerOS PIN
4. **Enter the secure workspace**
5. Use the modules:
   - **Notes** → store temporary secrets, credentials, or private notes
   - **Stealth Browser** → browse with session cleanup
   - **Burner Contacts** → save local-only contacts
   - **Maps** → use private navigation workflow
6. When done:
   - Exit normally for session cleanup
   - Or use **Emergency Purge** if you want a full local reset

---

### Example workflow

#### At a conference
- Join untrusted Wi-Fi
- Open BurnerOS
- Store temporary conference credentials in **Secure Notes**
- Use **Stealth Browser** for private access
- Save temporary meetup contacts in **Burner Contacts**
- End the session
- Trigger **Emergency Purge** before leaving

This reduces the chance of sensitive conference-related data lingering on your main device profile.

---

## UI Concept

BurnerOS is designed to feel like a **contained secure console** inside Android — simple, direct, and fast.

### UI goals

- minimal distractions
- high-contrast security-focused interface
- obvious access state
- fast panic / purge action
- quick navigation between secure modules

---

### Dashboard concept

```text
    __________________________________________
   | [🔓] BurnerOS Secure Workspace [00:01:45] |
   |__________________________________________|
   |                                          |
   |   [ 🔍 Stealth Web ]   [ 📝 Notes ]       |
   |   [ 📇 Contacts   ]   [ 🗺️ Maps  ]       |
   |                                          |
   |__________________________________________|
   |        [ EMERGENCY PURGE / RESET ]       |
   |__________________________________________|



graph TD
    classDef main fill:#f9f,stroke:#333,stroke-width:2px;
    classDef network fill:#e1f5fe,stroke:#0277bd,stroke-width:1px;
    classDef component fill:#fffde7,stroke:#fbc02d,stroke-width:1px;
    classDef physical fill:#eceff1,stroke:#546e7a,stroke-width:1px;

    subgraph PHYS["Physical Device"]
        HostOS["Android Host OS"]:::physical
        Sandbox["App Sandbox or Work Profile"]:::physical
    end

    subgraph BOS["BurnerOS Workspace"]
        direction TB
        MasterKey["Hardware Backed MasterKey"]:::main

        subgraph MODULES["Secure Modules"]
            direction LR
            Browser["Stealth Web"]:::component
            Notes["Encrypted Notes"]:::component
            Contacts["Burner Contacts"]:::component
            Maps["Secure Maps"]:::component
        end

        subgraph SEC["Privacy and Control Layer"]
            direction TB
            Storage["Encrypted Local Storage"]:::network
            Cleanup["Session Cleanup and Purge"]:::network
            Tunnel["VPN or Tor Roadmap"]:::network
        end
    end

    subgraph EXT["Untrusted Networks"]
        direction LR
        Wifi["Conference or Public WiFi"]:::network
        Internet["Public Internet"]:::network
    end

    HostOS --> Sandbox
    Sandbox --> MasterKey

    MasterKey --> Browser
    MasterKey --> Notes
    MasterKey --> Contacts
    MasterKey --> Maps

    Browser --> Cleanup
    Browser --> Tunnel
    Notes --> Storage
    Contacts --> Storage
    Maps --> Tunnel

    Tunnel --> Wifi
    Wifi --> Internet
