# minecraft-pack

Mod-Pack (NeoForge, [packwiz](https://packwiz.infra.link/)) für einen privaten Minecraft-Gemeinschaftsserver.
Der Prism Launcher der Mitspieler gleicht die Mods bei jedem Start automatisch mit dem Branch `live`
ab; der Server ist auf einen bestimmten Commit festgelegt. Neue Stände gibt nur der Server-Admin frei.

Enthält nur die Mod-Liste (Metadaten und Prüfsummen), keine Mod-Dateien und keine Geheimnisse.

## Stand („farm x winter“, 2026-11-12)

Minecraft **1.21.1**, NeoForge **21.1.265** — NeoForge 1.21.1 wegen Create (Spec 2026-11-12 in gaming-infra).
Grundlage ist das Pack der Gruppe „farm x winter 1.0.0“ (mrpack, Configs aus `overrides/config`), ohne
Mebahel API / Elder Scrolls Core (dort Fabric-Dateien; die Kreaturen-Mods dazu gibt es nur für Fabric).
Jede Version ist beim Pinnen mindestens **zwei Wochen** alt (Stichtag 2026-10-29). Deshalb eine Version älter als
im Pack der Gruppe: Alex's Mobs Continued, Balm, CreativeCore, Easy Mob Farm, MezzConfig, Moonlight,
Not Enough Animations, Snowy Spirit, SuperMartijn642's Core Lib, Traveler's Backpack, Fresh Animations (+ Extensions).

Zusätze (von der Gruppe bestätigt):

| | Version | Seite |
|---|---|---|
| Tectonic (+ Lithostitched) | 3.0.28-neoforge-21.1 (1.8.1-neoforge-21.1) | beide |
| Terralith | 2.6.2 | beide |
| Macaw's Roofs / Windows / Doors / Bridges | 2.3.2 / 2.4.2 / 1.1.5 / 3.1.2 | beide |
| Macaw's Fences and Walls / Paths and Pavings / Lights and Lamps | 1.2.1 / 1.1.1 / 1.1.5 | beide |
| Supplementaries | 1.21.1-4.0.2 | beide |
| Another Furniture | 4.0.3 | beide |
| Building Wands | 3.0.5 (Beta) | beide |
| Xaero's World Map | 1.47.0 (passend zur Minimap 26.6.0) | beide |
| Freecam | 1.3.0+mc1.21.1 | nur Client |
| Serene Seasons (+ GlitchCore) | 10.1.0.9 (2.1.0.2) | beide |
| Illager Invasion (+ Puzzles Lib) | v21.1.6 (21.1.66) | beide |
| Mowzie's Mobs | 1.8.2 | beide |
| Rangliste — eigene Mod, Quellcode `rangliste/` (ersetzt RankBoard, 2026-11-13) | 1.0.0 | nur Server |
| Better Statistics Screen (+ TCDCommons) | 3.13.9+neoforge-1.21.1-rev.1 (3.12.8 rev.1) | nur Client (TCDCommons beidseitig: Pflicht-Netzkanal) |
| Entity Model Features + Entity Texture Features (für Fresh Animations, fehlten im Pack der Gruppe) | 3.3.12 + 7.2.6 | nur Client |
| Towns and Towers (+ Cristel Lib) | 1.13.11 (3.1.7) | beide |
| Controlling (+ Searchables) — Suchfeld in der Tastenbelegung | 19.0.5 (1.0.2) | nur Client |
| Open Together, Easy Anvils, Diagonal Fences, Visual Workbench, Horse Expert (alle Fuzs, Puzzles Lib) | 21.1.2, v21.1.0, v21.1.1, 21.1.2, v21.1.0 | beide |

Shader (nur Client, jeder wählt selbst): Complementary Reimagined r5.9.3 und Unbound r5.9.3, BSL 10.1.8, Photon v1.4,
Solas 3.8, MakeUp Ultra Fast 9.6. Ressourcenpakete (nur Client, Aktivierung und Reihenfolge laut Freunde-Anleitung):
Whimscape r7 (+ Whimscape x Fresh Animations r4), Fresh Animations 1.11.1 (+ Extensions 1.8.1), Better Leaves 9.6,
(Bee's) Fancy Crops 1.3.

Server-Werkzeuge (aus dem Übergangs-Pack): MiniMOTD 2.1.3, spark 1.10.124 (nur Server);
FerriteCore 7.0.3, ModernFix 5.27.27, Lithium 0.15.4 (beide). Chunky 1.4.23 nur zum Vorgenerieren und nie zusammen
mit Distant Horizons auf dem Server (DH 3.3.3 stürzt beim Start ab, DH-Issue #1329): dafür DH vorübergehend
`side = "client"` (gaming-infra `docs/betrieb.md` „Pack ändern“ 6).

Configs:

- `config/xaero/`: Xaero-Lib ohne Internetzugriff (`allow_internet_access = false`); das erzwungene Server-Profil
  der Minimap (`minimap/server_profiles/default.cfg`) schaltet Höhlenansicht und Radar ab.
- Spieler-Einstellungen sind `preserve` (packwiz legt sie einmal an und überschreibt sie danach nicht): Sodium,
  Iris, Xaero-Client-Profile, AmbientSounds, Not Enough Animations, Just Zoom, dazu `config/MiniMOTD/main.conf` und
  `plugin_settings.conf`. `DistantHorizons.toml` bewusst nicht (Server-Drosselung muss ankommen).
- `config/sodium-fingerprint.json` der Gruppe fehlt absichtlich (rechnerbezogene Kennung, ohne Nutzen).
- `config/MiniMOTD/`: Server-Eintrag in der Mehrspieler-Liste — drei Varianten (Tag, Dämmerung, Nacht) mit
  Icons; der Server schaltet sie nach dem Sonnenstand um. Bei den Mitspielern liegen die Dateien ungenutzt.

## Werkzeug

- packwiz `v0.0.0-20261014010756-170b22d78595` (Commit vom 2026-10-14), gebaut mit
  `GOBIN=$HOME/.local/bin go install github.com/packwiz/packwiz@<version>`.
- Mod mit fester Version hinzufügen (IDs von Modrinth, Seite „Versions“):
  `packwiz modrinth add --project-id <projekt-id> --version-id <versions-id>`.
  Nicht `packwiz modrinth install <name>` — das nimmt die neueste Version, ohne Zwei-Wochen-Regel.
- Nur Server bzw. nur Client: in `mods/<name>.pw.toml` `side = "server"` / `"client"` setzen.
- Vorsicht bei `"client"`: Registriert die Mod einen Pflicht-Netzkanal, weist der Server Clients ab („channel … missing on the server side“). Prüfen: `unzip -p <jar> '*.class' | grep -a -c PayloadRegistrar`; bei Treffern ohne `optional` die Mod `"both"` setzen.
- Danach immer `packwiz refresh` (aktualisiert `index.toml`) — **unmittelbar vor jedem Commit**: packwiz
  nimmt alle Dateien ins Pack auf, die nicht in `.packwizignore` stehen, und prüft deren Prüfsummen.
  Dateien, die nicht zu den Spielern gehören (README), stehen in `.packwizignore`.
- Lokal testen: `packwiz serve` → Pack unter `http://localhost:8080/pack.toml`.

## Eigene Mod „Rangliste“

- Quellcode in `rangliste/` (NeoForge 1.21.1, nur Server, nur Vanilla-Pakete, kein Netzkanal). Befehle:
  `/rangliste`, `/rangliste <wert>`, `/rangliste ich`, `/rangliste ansicht <wert>|aus`.
- Bauen und prüfen (JDK 21, Paket `jdk21-openjdk`):
  `cd rangliste && JAVA_HOME=/usr/lib/jvm/java-21-openjdk ./gradlew build && bash werkzeug/rauchtest.sh`
  (Unit-Tests + Dev-Server mit RCON auf 127.0.0.1; Erwartung `RAUCHTEST OK`).
- Ausliefern: `build/libs/rangliste-<version>.jar` nach `extern/` kopieren, in `mods/rangliste.pw.toml` Dateiname,
  URL und `sha512sum` eintragen. **Dateien in `extern/` nie überschreiben oder löschen** — neue Version = neuer
  Dateiname; ältere Pack-Stände (Rückweg) laden ihre Datei über `raw.githubusercontent.com/…/main/extern/…`.
- `rangliste/` und `extern/` stehen in `.packwizignore` (sonst landeten sie auf den Clients).

## Prism Launcher (Mitspieler)

Instanz mit Minecraft 1.21.1 und NeoForge 21.1.265; `packwiz-installer-bootstrap.jar` in den
`minecraft`-Ordner der Instanz; *Instanz bearbeiten → Einstellungen → Eigene Befehle*, Pre-Launch-Befehl:

```
"$INST_JAVA" -jar packwiz-installer-bootstrap.jar https://raw.githubusercontent.com/21moya/minecraft-pack/live/pack.toml
```
