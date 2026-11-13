#!/usr/bin/env bash
# Rauchtest: Dev-Server nur mit dieser Mod starten, Befehle per RCON (nur 127.0.0.1) prüfen, sauber stoppen.
# Aufruf in rangliste/: bash werkzeug/rauchtest.sh
set -euo pipefail
cd "$(dirname "$0")/.."
export JAVA_HOME="${JAVA_HOME:-/usr/lib/jvm/java-21-openjdk}"

RUN=run
LOG=$RUN/logs/latest.log
# RCON-Passwort pro Lauf zufällig (Dev-Server nur auf 127.0.0.1; kein fester Wert im Repo).
PASSWORT=$(python3 -c 'import secrets; print(secrets.token_hex(12))')
RCON=(python3 werkzeug/rcon.py 127.0.0.1 25576 "$PASSWORT")

mkdir -p "$RUN/world/stats" "$RUN/logs"
echo 'eula=true' > "$RUN/eula.txt"
cat > "$RUN/server.properties" <<'PROPS'
server-ip=127.0.0.1
server-port=25570
enable-rcon=true
rcon.port=25576
level-type=minecraft\:flat
generate-structures=false
online-mode=false
view-distance=4
simulation-distance=4
max-players=1
PROPS
echo "rcon.password=$PASSWORT" >> "$RUN/server.properties"
# Testdaten: ein Spieler, den der Server nicht kennt (Name = UUID-Anfang), plus eine kaputte Datei.
cat > "$RUN/world/stats/00000000-0000-0000-0000-000000000001.json" <<'JSON'
{"stats":{"minecraft:custom":{"minecraft:play_time":885600,"minecraft:animals_bred":0}},"DataVersion":3955}
JSON
printf '{' > "$RUN/world/stats/kaputt.json"
# Kaputte Datei mit gültigem UUID-Namen: muss gelesen, als ungültig gemeldet und übersprungen werden.
printf '{' > "$RUN/world/stats/00000000-0000-0000-0000-00000000000f.json"
: > "$LOG"

./gradlew runServer > "$RUN/rauchtest-gradle.log" 2>&1 &
PID=$!
trap 'kill "$PID" 2>/dev/null || true' EXIT

for _ in $(seq 1 300); do
  grep -q 'Done (' "$LOG" 2>/dev/null && break
  if ! kill -0 "$PID" 2>/dev/null; then
    echo 'ABBRUCH: Server hat sich vorzeitig beendet'; tail -30 "$RUN/rauchtest-gradle.log"; exit 1
  fi
  sleep 1
done
grep -q 'Done (' "$LOG" || { echo 'ABBRUCH: Server nach 300 s nicht bereit'; exit 1; }

fehlerzeilen() {
  grep -c -E '^[[:space:]]+at farmxwinter\.rangliste|ERROR.*[Rr]angliste' "$1" || true
}

pruefe() {
  local befehl=$1; shift
  local antwort
  antwort=$("${RCON[@]}" "$befehl")
  for erwartet in "$@"; do
    if ! grep -qF -- "$erwartet" <<<"$antwort"; then
      echo "ABBRUCH: '$befehl' enthält nicht '$erwartet'"; echo "Antwort: $antwort"; exit 1
    fi
  done
  echo "ok: $befehl"
}

pruefe 'rangliste spielzeit' 'Rangliste: Spielzeit' ' 1. 00000000 – 12,3 h'
pruefe 'rangliste gezuechtet' 'Noch keine Werte für Gezüchtet.'
pruefe 'rangliste ich' 'Das geht nur als Spieler im Spiel.'
pruefe 'rangliste' 'Rangliste' 'Spielzeit'
pruefe 'rangliste ansicht spielzeit' 'Das geht nur als Spieler im Spiel.'
pruefe 'rangliste ansicht aus' 'Das geht nur als Spieler im Spiel.'

"${RCON[@]}" stop >/dev/null 2>&1 || true
wait "$PID" || true
trap - EXIT

# Stacktrace-Zeilen der Mod („at farmxwinter.rangliste…“) oder ERROR-Zeilen, die sie nennen.
fehler=$(fehlerzeilen "$LOG")
if [ "$fehler" != 0 ]; then
  echo "ABBRUCH: $fehler Fehlerzeilen der Mod im Log"; grep -E '^[[:space:]]+at farmxwinter\.rangliste|ERROR.*[Rr]angliste' "$LOG" | head; exit 1
fi
grep -q 'Statistik-Datei 00000000-0000-0000-0000-00000000000f.json übersprungen' "$LOG" \
  || { echo 'ABBRUCH: kaputte Statistik-Datei nicht gemeldet'; exit 1; }
echo 'RAUCHTEST OK'
