# minecraft-pack

Mod-Pack (NeoForge, [packwiz](https://packwiz.infra.link/)) für einen privaten Minecraft-Gemeinschaftsserver.
Der Prism Launcher der Mitspieler gleicht die Mods bei jedem Start automatisch mit dem Branch `live`
ab; der Server ist auf einen bestimmten Commit festgelegt. Neue Stände gibt nur der Server-Admin frei.

Enthält nur die Mod-Liste (Metadaten und Prüfsummen), keine Mod-Dateien und keine Geheimnisse.

## Stand (Übergangs-Pack, 2026-11-12)

| | Version | Seite |
|---|---|---|
| Minecraft | 1.21.1 | |
| NeoForge | 21.1.265 | |
| MiniMOTD | 2.1.3 | nur Server |
| Chunky | 1.4.23 | nur Server |
| spark | 1.10.124-neoforge-1.21.1 | nur Server |
| FerriteCore | 7.0.3-neoforge | beide |
| ModernFix | 5.27.27+mc1.21.1 | beide |
| Lithium | mc1.21.1-0.15.4-neoforge | beide |

NeoForge 1.21.1 wegen Create (Spec 2026-11-12 in gaming-infra). Dieses Übergangs-Pack enthält nur Server-Werkzeuge
und MiniMOTD; das Pack der Gruppe („farm x winter“) folgt als normaler Abgleich, dann mit neuer Welt. Jede Version
ist beim Pinnen mindestens **zwei Wochen** alt (Stichtag 2026-10-29; Krypton gibt es nicht für NeoForge).

`config/MiniMOTD/`: Server-Eintrag in der Mehrspieler-Liste — drei Varianten (Tag, Dämmerung, Nacht) mit Icons;
der Server schaltet sie nach dem Sonnenstand um. `main.conf` und `plugin_settings.conf` sind `preserve` (packwiz
legt sie einmal an und überschreibt sie danach nicht). Bei den Mitspielern liegen die Dateien ungenutzt im
`config`-Ordner.

## Werkzeug

- packwiz `v0.0.0-20261014010756-170b22d78595` (Commit vom 2026-10-14), gebaut mit
  `GOBIN=$HOME/.local/bin go install github.com/packwiz/packwiz@<version>`.
- Mod mit fester Version hinzufügen (IDs von Modrinth, Seite „Versions“):
  `packwiz modrinth add --project-id <projekt-id> --version-id <versions-id>`.
  Nicht `packwiz modrinth install <name>` — das nimmt die neueste Version, ohne Zwei-Wochen-Regel.
- Nur Server bzw. nur Client: in `mods/<name>.pw.toml` `side = "server"` / `"client"` setzen.
- Danach immer `packwiz refresh` (aktualisiert `index.toml`) — **unmittelbar vor jedem Commit**: packwiz
  nimmt alle Dateien ins Pack auf, die nicht in `.packwizignore` stehen, und prüft deren Prüfsummen.
  Dateien, die nicht zu den Spielern gehören (README), stehen in `.packwizignore`.
- Lokal testen: `packwiz serve` → Pack unter `http://localhost:8080/pack.toml`.

## Prism Launcher (Mitspieler)

Instanz mit Minecraft 1.21.1 und NeoForge 21.1.265; `packwiz-installer-bootstrap.jar` in den
`minecraft`-Ordner der Instanz; *Instanz bearbeiten → Einstellungen → Eigene Befehle*, Pre-Launch-Befehl:

```
"$INST_JAVA" -jar packwiz-installer-bootstrap.jar https://raw.githubusercontent.com/21moya/minecraft-pack/live/pack.toml
```
