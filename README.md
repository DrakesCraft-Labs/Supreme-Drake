<div align="center">

  <img src="https://raw.githubusercontent.com/DrakesCraft-Labs/Supreme-Drake/main/banner.svg" alt="Supreme-Drake Banner" width="920" />

# 👑 Supreme-Drake

**Addon Endgame de Slimefun4 con Tarjetas de Mejora, Generadores Cuánticos, BeeTech, MobTech y Aceleración Nativa en Rust**

<p>
  <a href="https://github.com/DrakesCraft-Labs/Supreme-Drake"><img src="https://img.shields.io/badge/GitHub-Supreme--Drake-181717?style=for-the-badge&logo=github" alt="GitHub"/></a>
  <img src="https://img.shields.io/badge/Java-21_FFM_Panama-F89820?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21 FFM"/>
  <img src="https://img.shields.io/badge/Rust-FFM_Accelerated-FF4500?style=for-the-badge&logo=rust&logoColor=white" alt="Rust Native"/>
  <img src="https://img.shields.io/badge/Paper-1.21.11-FFD700?style=for-the-badge&logo=minecraft&logoColor=white" alt="Paper 1.21.11"/>
</p>

</div>

> ### 🏰 ¡Únete a la Comunidad Oficial de DrakesCraft!
> 
> * 🎮 **IP del Servidor**: `play.drakescraft.cl` *(Java 1.21.11 & Bedrock)*
> * 💬 **Discord Oficial**: [discord.gg/drakescraft](https://discord.gg/rR7FbfCt9Y)
> * 🌐 **Web & Guía**: [web.drakescraft.cl](https://web.drakescraft.cl) — 🛒 **Tienda**: [web.drakescraft.cl/store](https://web.drakescraft.cl/store.html)
> 
> *¡Juega con este addon y más de 80 expansiones optimizadas en vivo en nuestra network de supervivencia técnica!*

---

---

## 👑 ¿Qué es Supreme-Drake?

`Supreme-Drake` es la expansión Endgame de Slimefun4 para DrakesCraft. Introduce un ecosistema masivo de tecnologías avanzadas, tarjetas de actualización para máquinas, generadores de energía supremos y módulos especializados de automatización.

---

## 🧰 Características y Módulos de Supreme-Drake

### 1. 💳 Sistema de Tarjetas de Mejora (Upgrade Cards)
- **Tarjetas de Velocidad (Speed Cards Tiers I - V)**: Incrementan la velocidad de procesamiento de las máquinas de Slimefun hasta un 500%.
- **Tarjetas de Eficiencia Energética (Energy Efficiency Cards)**: Reducen el consumo de energía en julios por tick hasta en un 75%.
- **Tarjetas de Producción Doble (Overclock Cards)**: Duplican el rendimiento de producción de lingotes y minerales procesados.

### 2. ⚡ Generadores Cuánticos & Energía Suprema
- **Generador Cuántico Supremo (Quantum Supreme Generator)**: Produce más de 5,000 J/t consumiendo combustible de estrellas o materia cuántica.
- **Estación de Carga Suprema (Supreme Charging Station)**: Recarga armaduras, herramientas y jetpacks de Slimefun a velocidad máxima.

### 3. 🐝 BeeTech & 🧟 MobTech
- **BeeTech Module**: Automatización de colmenas genéticas, producción de panales especiales y miel refinada para reactores.
- **MobTech Module**: Extracción de esencias de mobs, duplicadores de drops y granjas automatizadas de entidades.

### 4. 🛡️ Equipamiento Supremo
- **Armadura Suprema (Supreme Armor)**: Confiere inmunidad a efectos de estado, regeneración pasiva y resistencia a explosiones/laveo.

---

## ⚡ Aceleración Nativa en Rust (Modelo Híbrido Cero-Riesgo)

`Supreme-Drake` incluye el puente Panama FFM **`RustNativeBridge`** para delegar el cálculo de multiplicadores de tarjetas de velocidad y tickers de generadores cuánticos al motor nativo `Slimefun-Rust` (`slimefun_ffi`):
- 🚀 **Cálculo de Multiplicadores en Nanosegundos**: Sin sobrecarga de CPU ni pausas de Garbage Collector.
- 🛡️ **Preservación Total sin Reset (SQLite 0-Reset)**: Interfaz 1:1 con la base de datos `stored-blocks.db`.

---

## 🛠️ Compilación e Instalación

```bash
# Compilar paquete JAR con Maven
mvn clean package
```

Ubica el archivo compilado `Supreme-Drake-v2.1.0.jar` en la carpeta `plugins/` de tu servidor Minecraft Paper/Purpur 1.21.11.

---

<div align="center">

**DrakesCraft Labs** · Mantenido por [**JackStar6677-1**](https://github.com/JackStar6677-1)

</div>

## Qué añade al juego

Supreme is an addon for Slimefun which adds 100+ various new resources that will allow you to craft powerful new items, weapons, tools and armor. These can be made up from titanium, aurum, adamantium, thornium with some being magical, rare, epic, legendary or supreme! It also adds 12 new electric generators, 5 new capacitors and even more...
Registra alrededor de **219 objetos** en la guía de Slimefun.

Todo se fabrica y se investiga desde la guía normal (`/sf guide`), como cualquier otro contenido
de Slimefun: no hace falta ningún comando especial para empezar.

## Compatibilidad

| | |
|---|---|
| Servidor | Paper / Purpur **1.21.11** |
| Java | **21** |
| Requiere | [Slimefun4-Drake](https://github.com/DrakesCraft-Labs/Slimefun4-Drake) |
| Lado | Solo servidor — quien juega no instala nada |
| Versión | ${project.version} |

## Instalación

1. Descarga el `.jar` de la última versión.
2. Déjalo en la carpeta `plugins/` del servidor, junto a Slimefun.
3. Reinicia el servidor. Los objetos aparecen solos en la guía.

> Este addon está portado al fork de Slimefun de DrakesCraft. Con el Slimefun original puede no
> cargar, porque cambia el espacio de nombres de las clases.

## Créditos
- RelativoBR
- Especttra
- WilianSantosBR e Mynothauro

Port y mantenimiento por **DrakesCraft Labs**. La autoría original es de quien figura arriba; el detalle está en [docs/UPSTREAM_ATTRIBUTION.md](https://raw.githubusercontent.com/DrakesCraft-Labs/Supreme-Drake/main/docs/UPSTREAM_ATTRIBUTION.md).

Licencia **GPL-3.0-only**.

## ⚖️ Upstream Attribution & License / Licencia y Créditos

- **Original Project / Upstream**: Slimefun4 Community Addon.
- **Port & Maintenance**: DrakesCraft Labs team (Compatibility for Paper / Purpur 1.21.11).
- **License**: GPL-3.0 / MIT.
- **Source Code**: [GitHub Repository](https://github.com/DrakesCraft-Labs/Supreme-Drake)
- **Support & Issues**: [GitHub Issues](https://github.com/DrakesCraft-Labs/Supreme-Drake/issues) | [Discord](https://discord.gg/rR7FbfCt9Y)

*This project is an open-source derivative work maintained by DrakesCraft Labs under the terms of its original license. All original assets and concepts belong to their respective creators.*
