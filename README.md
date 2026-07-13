# Supreme Drake

Supreme Drake es el port mantenido de Supreme para el stack de Slimefun de
DrakesCraft Labs. Añade recursos de alto nivel, maquinaria, generación de
energía, canteras y progresión de equipo sin cambiar los IDs ni las recetas que
ya existen en mundos activos.

## Runtime compatible

| Componente | Objetivo |
|---|---|
| Minecraft / Paper / Purpur | **1.21.11** |
| Java | **21** |
| Slimefun | **Slimefun Drake 11** |
| API de compilación | `paper-api 1.21.1-R0.1-SNAPSHOT` |

La coordenada Maven de Paper conserva la línea `1.21.1`; el runtime objetivo
es Paper/Purpur 1.21.11 con el core Drake. No sustituir Slimefun Drake por el
upstream: este addon depende de namespaces y contratos relocalizados.

## Contenido

- Recursos y aleaciones de progresión: aurum, titanio, adamantium y thornium.
- Máquinas de fabricación, fundición, mutación y automatización.
- Generadores, capacitores, canteras y sistemas virtuales de recolección.
- Herramientas, armas y armaduras configurables para el tramo avanzado.

Las familias de generación, canteras y equipo pueden regularse desde
`plugins/Supreme/config.yml`. Revisa economía y balance antes de habilitar una
familia de producción nueva en un mundo activo.

## Trabajo Drake

- Port y compilación reproducible para Java 21 y Paper 1.21.11.
- Dependencias explícitas al core Slimefun Drake.
- Sin autoactualizador remoto ni descarga de artefactos en caliente.
- Conservación de IDs, recetas, claves de datos y configuración existente.
- Validación de artefacto previa a un despliegue; nunca se reemplaza el JAR en
  producción sin rollback verificable.

## Actualización segura

1. Respalda `plugins/Supreme-drake.jar` y `plugins/Supreme/`.
2. Compila y conserva el checksum del JAR candidato.
3. En staging, abre una máquina colocada, una cantera y un ítem legacy.
4. Instala **un solo** JAR de Supreme durante una ventana de reinicio.
5. Conserva el JAR anterior hasta validar carga, recetas y datos de bloque.

No instales a la vez el addon upstream y este port: comparten identidad y
contenido lógico.

## Desarrollo

```bash
mvn -B -ntp clean verify
```

Artefacto: `target/Supreme-drake.jar`.

## Procedencia

Proyecto original de RelativoBR. Este fork preserva la autoría upstream y añade
mantenimiento, compatibilidad y operación para DrakesCraft Labs.
