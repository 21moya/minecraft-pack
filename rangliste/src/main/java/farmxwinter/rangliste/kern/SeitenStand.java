package farmxwinter.rangliste.kern;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Was ein Client von der persönlichen Seitenleiste weiß. Verhindert doppeltes Anlegen: das wirft auf dem
 * Client einen Fehler und trennt die Verbindung.
 */
public final class SeitenStand {
    public static final int MAX_EINTRAEGE = 10;
    /**
     * Name des Objectives auf dem Client. Enthält ein Zeichen, das {@code /scoreboard objectives add} nicht erlaubt:
     * so kann kein per Befehl angelegtes Objective gleich heißen (doppeltes Anlegen = Rauswurf).
     */
    public static final String OBJECTIVE = "rangliste:ansicht";

    private boolean angelegt;
    private Set<String> besitzer = new HashSet<>();

    /** Auf ausdrücklichen Wunsch zeigen: holt den Platz rechts zurück, falls etwas anderes ihn belegt hat. */
    public List<SeitenOp> zeige(Wert w, List<Platz> top) {
        return baue(w, top, true);
    }

    /** Regelmäßige Aktualisierung: verdrängt keine fremde Anzeige rechts. */
    public List<SeitenOp> aktualisiere(Wert w, List<Platz> top) {
        return baue(w, top, false);
    }

    private List<SeitenOp> baue(Wert w, List<Platz> top, boolean anzeigen) {
        List<SeitenOp> ops = new ArrayList<>();
        String titel = Texte.titel(w);
        if (angelegt) {
            ops.add(new SeitenOp.Aendern(titel));
            if (anzeigen) {
                ops.add(new SeitenOp.Anzeigen());
            }
        } else {
            ops.add(new SeitenOp.Anlegen(titel));
            ops.add(new SeitenOp.Anzeigen());
            angelegt = true;
            besitzer.clear();
        }
        List<Platz> sichtbar = top.subList(0, Math.min(MAX_EINTRAEGE, top.size()));
        Set<String> neu = new LinkedHashSet<>();
        for (Platz p : sichtbar) {
            neu.add(p.name());
        }
        for (String alt : besitzer) {
            if (!neu.contains(alt)) {
                ops.add(new SeitenOp.Loeschen(alt));
            }
        }
        for (Platz p : sichtbar) {
            ops.add(new SeitenOp.Setzen(p.name(), punkte(p.wert()), p.name(), w.einheit().formatiere(p.wert())));
        }
        besitzer = neu;
        return ops;
    }

    public List<SeitenOp> verberge() {
        if (!angelegt) {
            return List.of();
        }
        angelegt = false;
        besitzer.clear();
        return List.of(new SeitenOp.Entfernen());
    }

    /** Client ist weg: beim nächsten Zeigen neu anlegen. */
    public void abgemeldet() {
        angelegt = false;
        besitzer.clear();
    }

    /** Sortierwert für die Seitenleiste (int), begrenzt auf 0 … Integer.MAX_VALUE. */
    public static int punkte(long wert) {
        return (int) Math.max(0, Math.min(Integer.MAX_VALUE, wert));
    }
}
