<div align="center">

  <img src="https://raw.githubusercontent.com/DrakesCraft-Labs/Supreme-Drake/main/banner.svg" alt="Supreme-Drake Banner" width="920" />

# 👑 Supreme-Drake

**Endgame Slimefun4 Addon with Upgrade Cards, Quantum Generators, BeeTech, MobTech, and Native Rust Acceleration**

<p>
  <a href="https://github.com/DrakesCraft-Labs/Supreme-Drake"><img src="https://img.shields.io/badge/GitHub-Supreme--Drake-181717?style=for-the-badge&logo=github" alt="GitHub"/></a>
  <img src="https://img.shields.io/badge/Java-21_FFM_Panama-F89820?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21 FFM"/>
  <img src="https://img.shields.io/badge/Rust-FFM_Accelerated-FF4500?style=for-the-badge&logo=rust&logoColor=white" alt="Rust Native"/>
  <img src="https://img.shields.io/badge/Paper-1.21.11-FFD700?style=for-the-badge&logo=minecraft&logoColor=white" alt="Paper 1.21.11"/>
</p>

[🇬🇧 **English**](README.md) · [🇪🇸 **Español**](README_ES.md)

</div>

> ### 🏰 Join the Official DrakesCraft Community!
> 
> * 🎮 **Server IP**: `mc.drakescraft.cl` *(Java 1.21.11 & Bedrock Port 25565 / 19132)*
> * 💬 **Official Discord**: [discord.gg/drakescraft](https://discord.gg/rv3vtXZTk7) — *Check out `#general-english`!*
> * 🌐 **Website & Guides**: [web.drakescraft.cl](https://web.drakescraft.cl) — 🛒 **Store**: [web.drakescraft.cl/store](https://web.drakescraft.cl/store.html)
> 
> *Play with this addon alongside 80+ optimized expansions live on our technical survival network!*

---

## 👑 What is Supreme-Drake?

`Supreme-Drake` is an Endgame technology expansion for **Slimefun4**. It introduces over 100+ advanced resources, machine upgrade cards, supreme power generators, and dedicated industrial automation modules.

All items and mechanics are researched and crafted directly through the standard **Slimefun Guide (`/sf guide`)**.

---

## 🧰 Key Features & Modules

### 1. 💳 Machine Upgrade Card System
- **Speed Cards (Tiers I - V)**: Accelerates Slimefun machine processing speeds by up to 500%.
- **Energy Efficiency Cards**: Reduces power consumption per tick by up to 75%.
- **Overclock / Double Output Cards**: Multiplies output yields for processed ingots and refined materials.

### 2. ⚡ Quantum Generators & Supreme Power
- **Quantum Supreme Generator**: Generates 5,000+ J/t using stellar fuels or condensed quantum matter.
- **Supreme Charging Station**: Rapidly recharges Slimefun armor, tools, and jetpacks at maximum throughput.
- **Advanced Capacitors**: High-density energy buffers storing millions of Joules.

### 3. 🐝 BeeTech & 🧟 MobTech
- **BeeTech Module**: Genetic hive automation, specialized honeycomb processing, and refined royal jelly for reactors.
- **MobTech Module**: Mob essence extraction, automated drop duplication, and entity-free mob farming.

### 4. 🛡️ Supreme Gear & Modular Alloys
- **Supreme Armor & Tools**: Crafted from Titanium, Aurum, Adamantium, and Thornium with custom rarity tiers (Magical, Rare, Epic, Legendary, Supreme).
- Status effect immunities, passive health regeneration, and blast/lava resistance.

---

## ⚡ Zero-Risk Native Rust Acceleration

`Supreme-Drake` incorporates the Project Panama FFM component **`RustNativeBridge`** to offload speed card multiplier evaluation and generator tickers directly to the native `Slimefun-Rust` engine (`slimefun_ffi`):
- 🚀 **Nanosecond Multiplier Calculation**: Real CPU parallelism with zero Garbage Collector pauses.
- 🛡️ **Zero-Reset SQLite Safety**: Fully preserves existing block data and inventories in `stored-blocks.db`.

---

## 📋 Compatibility

| Parameter | Requirement |
|---|---|
| **Server Software** | Paper / Purpur **1.21.11** |
| **Java Runtime** | **Java 21** LTS |
| **Required Core** | [Slimefun4-Drake](https://github.com/DrakesCraft-Labs/Slimefun4-Drake) |
| **Architecture** | Server-Side Only — players join with vanilla Minecraft clients |

---

## 📥 Installation

1. Download the latest `.jar` from the [Releases](https://github.com/DrakesCraft-Labs/Supreme-Drake/releases) page.
2. Place it into your server's `plugins/` directory alongside `Slimefun4-Drake.jar`.
3. Restart the server. Items and recipes will automatically appear in `/sf guide`.

---

## 🛠️ Building from Source

```bash
git clone https://github.com/DrakesCraft-Labs/Supreme-Drake.git
cd Supreme-Drake
mvn clean package
```

The compiled JAR will be located at `target/Supreme-Drake-v2.1.0.jar`.

---

<div align="center">

**DrakesCraft Labs** · Maintained by [**JackStar6677-1**](https://github.com/JackStar6677-1)

</div>

## ⚖️ Upstream Attribution & License

- **Original Project / Upstream**: Slimefun4 Community Addon (originally authored by RelativoBR, Especttra, WilianSantosBR, and Mynothauro).
- **Port & Maintenance**: DrakesCraft Labs team (Modernization and compatibility for Paper / Purpur 1.21.11 & Java 21 FFM).
- **License**: GNU General Public License v3.0 (GPL-3.0-only).
- **Source Code**: [GitHub Repository](https://github.com/DrakesCraft-Labs/Supreme-Drake)
- **Support & Issues**: [GitHub Issues](https://github.com/DrakesCraft-Labs/Supreme-Drake/issues) | [Discord](https://discord.gg/rv3vtXZTk7)

*This project is an open-source derivative work maintained by DrakesCraft Labs under the terms of its original license.*
