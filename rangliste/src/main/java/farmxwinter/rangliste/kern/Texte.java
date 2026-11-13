package farmxwinter.rangliste.kern;

import java.util.Locale;
import java.util.UUID;

/** Alle Ausgabetexte der Mod, nur Deutsch. */
public final class Texte {

    private Texte() {
    }

    public static String kopf(Wert w) {
        return "── Rangliste: " + w.anzeige() + " ──";
    }

    public static String zeile(Platz p, Wert w) {
        return String.format(Locale.ROOT, "%2d. %s – %s", p.platz(), p.name(), w.einheit().formatiere(p.wert()));
    }

    public static String keineWerte(Wert w) {
        return "Noch keine Werte für " + w.anzeige() + ".";
    }

    public static String nurSpieler() {
        return "Das geht nur als Spieler im Spiel.";
    }

    public static String titel(Wert w) {
        return "Rangliste: " + w.anzeige();
    }

    public static String seiteAn(Wert w) {
        return "Seitenleiste zeigt jetzt: " + w.anzeige() + ".";
    }

    public static String seiteAus() {
        return "Seitenleiste ausgeblendet.";
    }

    public static String seiteWarAus() {
        return "Die Seitenleiste war schon aus.";
    }

    public static String menueKopf() {
        return "── Rangliste ──";
    }

    public static String knopfRechts() {
        return "[rechts]";
    }

    public static String knopfMeine() {
        return "[Meine Werte]";
    }

    public static String knopfAus() {
        return "[Seitenleiste aus]";
    }

    public static String hinweisChat(Wert w) {
        return "Rangliste " + w.anzeige() + " im Chat zeigen";
    }

    public static String hinweisRechts(Wert w) {
        return w.anzeige() + " rechts einblenden";
    }

    public static String meineKopf() {
        return "── Deine Werte ──";
    }

    public static String meineZeile(Wert w, long roh) {
        return w.anzeige() + ": " + w.einheit().formatiere(roh);
    }

    /** Ersatz, wenn der Server den Namen nicht kennt: die ersten 8 Zeichen der UUID. */
    public static String namensErsatz(UUID id) {
        return id.toString().substring(0, 8);
    }
}
