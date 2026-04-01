# BurnerOS
### A privacy-first secure workspace for Android

<p align="center">
  <b>BurnerOS</b> is an Android-based secure workspace designed for privacy-sensitive workflows.<br/>
  It creates an isolated, encrypted environment for notes, browsing, contacts, and temporary operational data — with fast emergency purge controls and minimal forensic footprint.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android%20API%2023%2B-brightgreen?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Security-AES--256-red?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Architecture-Hardware%20Backed%20Keys-blue?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Status-Research%20Prototype-orange?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Focus-Mobile%20Privacy%20%26%20Security-black?style=for-the-badge" />
</p>

---

## Why BurnerOS exists

Modern smartphones are convenient, but they are also noisy:
- background sync
- cloud backups
- shared app state
- telemetry
- persistent browser traces
- accidental exposure on untrusted networks

**BurnerOS** is built as a **secure, temporary, privacy-focused workspace** inside Android for situations where you want stronger separation between your normal device life and sensitive activity.

### Example use cases
- Security conferences on untrusted Wi-Fi
- Handling temporary credentials or one-time access tokens
- Private note-taking during travel
- Isolated browsing sessions
- Sensitive field operations where rapid local data purge may be required

---

## Core security model

BurnerOS is not a full operating system replacement.  
It is a **contained secure workspace** that applies:

- **App-level encrypted storage**
- **Hardware-backed key management**
- **Secondary authentication**
- **Ephemeral session handling**
- **No cloud sync by design**
- **Rapid local purge controls**

This makes it useful as a **privacy layer** for high-risk or temporary workflows on Android.

---

## Features at a glance

| Module | What it does | Security benefit |
|---|---|---|
| **Biometric / PIN Gate** | Requires device auth + secondary app auth | Adds a second access boundary |
| **Secure Note Vault** | Stores sensitive text in encrypted local storage | Protects credentials / notes from casual device compromise |
| **Stealth Web** | Privacy-focused browser session with local cleanup on exit | Reduces browsing residue |
| **Burner Contacts** | Local-only contacts stored inside the workspace | Prevents sync leakage to device/cloud contacts |
| **Secure Maps** | Private map workflows using OpenStreetMap-based integration | Limits dependency on mainstream tracking-heavy map flows |
| **Emergency Purge** | One-tap local wipe of Burner workspace data | Fast response for device loss / coercive situations |
| **Duress PIN (Prototype)** | Alternate PIN path that triggers silent workspace reset | Designed for emergency privacy protection scenarios |

---

## Threat model

BurnerOS is designed to reduce exposure against:

- Shared-device privacy leakage
- Casual device inspection
- App-to-app data bleed
- Local traces from browser history / cookies / cached sessions
- Credential exposure in plaintext notes
- Sensitive data persistence after short-lived workflows
- Untrusted network usage (future hardening with enforced tunnel controls)

### Out of scope / non-goals
BurnerOS is **not** intended to defend against:

- A fully compromised kernel
- Advanced hardware implants
- Baseband-level compromise
- Nation-state forensic extraction on a seized unlocked device
- Rooted devices with active runtime instrumentation
- Memory scraping during an active unlocked session

This project is best viewed as a **privacy-hardening layer**, not a guarantee of invisibility.

---

## Architecture & trust boundaries

Whenever you are on hostile Wi-Fi, traveling, or handling short-lived sensitive data, BurnerOS acts as a contained workspace with explicit trust boundaries.

```mermaid
graph TD
    classDef main fill:#f9f,stroke:#333,stroke-width:2px;
    classDef network fill:#e1f5fe,stroke:#0277bd,stroke-width:1px;
    classDef component fill:#fffde7,stroke:#fbc02d,stroke-width:1px;
    classDef physical fill:#eceff1,stroke:#546e7a,stroke-width:1px;

    subgraph Phys [PHYSICAL DEVICE]
        HostOS[Android Host OS]:::physical
        Sandbox[App Sandbox / Work Profile Boundary]:::physical
    end

    subgraph BOS [BURNEROS WORKSPACE]
        direction TB
        HardKernel[Hardware-Backed MasterKey]:::main
        
        subgraph Tools [SECURE MODULES]
            direction LR
            Browser[Stealth Web]:::component
            Notes[Encrypted Notes]:::component
            Maps[Secure Maps]:::component
            Contacts[Burner Contacts]:::component
        end

        subgraph SECURE_LAYER [PRIVACY & CONTROL LAYER]
            direction TB
            Storage[Encrypted Local Storage]:::network
            Cleanup[Session Cleanup / Purge Controls]:::network
            Tunnel[VPN / Tor (Roadmap)]:::network
        end
    end

    subgraph External [UNTRUSTED NETWORKS]
        direction LR
        WiFi[Conference / Public Wi-Fi]:::network
        Internet[Public Internet]:::network
    end

    HostOS --> Sandbox
    Sandbox --> BOS

    HardKernel --> Tools
    Tools --> Storage
    Tools --> Cleanup
    Tools --> Tunnel

    Tunnel --> WiFi
    WiFi --> Internet

    BOS -.->|No cloud sync by default| HostOS
    BOS -.->|Encrypted local-only data| HostOS
