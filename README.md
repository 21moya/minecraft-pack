# minecraft-pack

Mod-Pack (Fabric, [packwiz](https://packwiz.infra.link/)) für einen privaten Minecraft-Gemeinschaftsserver.
Der Prism Launcher der Mitspieler gleicht die Mods bei jedem Start automatisch mit dem Branch `live`
ab; der Server ist auf einen bestimmten Commit festgelegt. Neue Stände gibt nur der Server-Admin frei.

Enthält nur die Mod-Liste (Metadaten und Prüfsummen), keine Mod-Dateien und keine Geheimnisse.

## Stand (Start-Pack, 2026-11-09)

| | Version | Seite |
|---|---|---|
| Minecraft | 26.3 | |
| Fabric Loader | 0.20.1 | |
| Fabric API | 0.165.0+26.3 | beide |
| Lithium | mc26.3-0.26.2-fabric | beide |
| FerriteCore | 9.0.0-fabric | beide |
| Krypton | 0.3.2 | beide |
| spark | 1.10.187-fabric | nur Server |
| Chunky | 1.5.3 | nur Server |

Auswahlregel: neueste Minecraft-Version, für die **alle** Mods eine Fabric-Version haben, die mindestens
**zwei Wochen** alt ist (Stichtag beim Start: 2026-10-26; Chunky deshalb 1.5.3 statt 1.5.66). Das
Start-Pack enthält nur Performance- und Server-Werkzeuge; die eigentlichen Mods wählt die Gruppe
gemeinsam. Ein Wechsel von Minecraft-Version oder Modloader bedeutet in der Regel eine neue Welt.

## Werkzeug

- packwiz `v0.0.0-20261014010756-170b22d78595` (Commit vom 2026-10-14), gebaut mit
  `GOBIN=$HOME/.local/bin go install github.com/packwiz/packwiz@<version>`.
- Mod mit fester Version hinzufügen (IDs von Modrinth, Seite „Versions“):
  `packwiz modrinth add --project-id <projekt-id> --version-id <versions-id>`.
  Nicht `packwiz modrinth install <name>` — das nimmt die neueste Version, ohne Zwei-Wochen-Regel.
- Nur Server bzw. nur Client: in `mods/<name>.pw.toml` `side = "server"` / `"client"` setzen.
- Danach immer `packwiz refresh` (aktualisiert `index.toml`).
- Lokal testen: `packwiz serve` → Pack unter `http://localhost:8080/pack.toml`.

## Prism Launcher (Mitspieler)

Instanz mit Minecraft und Fabric Loader wie oben; `packwiz-installer-bootstrap.jar` in den
`minecraft`-Ordner der Instanz; *Instanz bearbeiten → Einstellungen → Eigene Befehle*, Pre-Launch-Befehl:

```
"$INST_JAVA" -jar packwiz-installer-bootstrap.jar https://raw.githubusercontent.com/21moya/minecraft-pack/live/pack.toml
```
