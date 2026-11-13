package farmxwinter.rangliste.kern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class TexteTest {

    private static final UUID A = new UUID(0, 1);

    @Test
    void zeileMitPlatzNameUndWert() {
        assertEquals(" 1. Moya5k – 12,3 h", Texte.zeile(new Platz(1, A, "Moya5k", 885600), Wert.SPIELZEIT));
        assertEquals("11. x – 24 min", Texte.zeile(new Platz(11, A, "x", 28800), Wert.SPIELZEIT));
    }

    @Test
    void namensErsatz() {
        assertEquals("00000000", Texte.namensErsatz(A));
    }

    @Test
    void keineWerte() {
        assertEquals("Noch keine Werte für Gezüchtet.", Texte.keineWerte(Wert.GEZUECHTET));
    }

    @Test
    void festeTexte() {
        assertEquals("── Rangliste: Spielzeit ──", Texte.kopf(Wert.SPIELZEIT));
        assertEquals("Rangliste: Spielzeit", Texte.titel(Wert.SPIELZEIT));
        assertEquals("Das geht nur als Spieler im Spiel.", Texte.nurSpieler());
        assertEquals("Seitenleiste zeigt jetzt: Spielzeit.", Texte.seiteAn(Wert.SPIELZEIT));
        assertEquals("Seitenleiste ausgeblendet.", Texte.seiteAus());
        assertEquals("Die Seitenleiste war schon aus.", Texte.seiteWarAus());
        assertEquals("Spielzeit: 12,3 h", Texte.meineZeile(Wert.SPIELZEIT, 885600));
    }

    @Test
    void keinCjk() {
        Pattern cjk = Pattern.compile("[\\u2E80-\\u9FFF\\uF900-\\uFAFF\\uFF00-\\uFFEF]");
        List<String> texte = new ArrayList<>(List.of(
                Texte.nurSpieler(), Texte.seiteAus(), Texte.seiteWarAus(), Texte.menueKopf(),
                Texte.knopfRechts(), Texte.knopfMeine(), Texte.knopfAus(), Texte.meineKopf(),
                Texte.namensErsatz(A)));
        for (Wert w : Wert.values()) {
            texte.addAll(List.of(
                    Texte.kopf(w), Texte.keineWerte(w), Texte.titel(w), Texte.seiteAn(w),
                    Texte.hinweisChat(w), Texte.hinweisRechts(w), Texte.meineZeile(w, 123_456_789L),
                    Texte.zeile(new Platz(1, A, "x", 123_456_789L), w), w.anzeige(),
                    w.einheit().formatiere(123_456_789L)));
        }
        for (String t : texte) {
            assertFalse(cjk.matcher(t).find(), t);
        }
    }
}
