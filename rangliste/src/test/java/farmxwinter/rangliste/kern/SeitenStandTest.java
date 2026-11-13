package farmxwinter.rangliste.kern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class SeitenStandTest {

    private static final UUID A = new UUID(0, 1);
    private static final UUID B = new UUID(0, 2);

    @Test
    void erstesZeigenLegtAnUndZeigt() {
        SeitenStand st = new SeitenStand();
        List<SeitenOp> ops = st.zeige(Wert.SPIELZEIT, List.of(new Platz(1, A, "Anna", 885600)));
        assertEquals(new SeitenOp.Anlegen("Rangliste: Spielzeit"), ops.get(0));
        assertEquals(new SeitenOp.Anzeigen(), ops.get(1));
        assertEquals(new SeitenOp.Setzen("Anna", 885600, "Anna", "12,3 h"), ops.get(2));
        assertEquals(3, ops.size());
    }

    @Test
    void zweimalEinschaltenLegtNichtNochmalAn() {
        SeitenStand st = new SeitenStand();
        st.zeige(Wert.SPIELZEIT, List.of(new Platz(1, A, "Anna", 885600)));
        List<SeitenOp> ops = st.zeige(Wert.TODE, List.of(new Platz(1, B, "Bob", 2)));
        assertFalse(ops.stream().anyMatch(o -> o instanceof SeitenOp.Anlegen));
        assertTrue(ops.contains(new SeitenOp.Aendern("Rangliste: Tode")));
        assertTrue(ops.contains(new SeitenOp.Loeschen("Anna")));
        assertTrue(ops.contains(new SeitenOp.Setzen("Bob", 2, "Bob", "2")));
    }

    @Test
    void hoechstensZehnEintraege() {
        SeitenStand st = new SeitenStand();
        List<Platz> zwoelf = new java.util.ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            zwoelf.add(new Platz(i, new UUID(0, i), "s" + i, 100 - i));
        }
        long gesetzt = st.zeige(Wert.TODE, zwoelf).stream().filter(o -> o instanceof SeitenOp.Setzen).count();
        assertEquals(10, gesetzt);
    }

    @Test
    void verbergenNurWennAngelegt() {
        SeitenStand st = new SeitenStand();
        assertEquals(List.of(), st.verberge());
        st.zeige(Wert.TODE, List.of());
        assertEquals(List.of(new SeitenOp.Entfernen()), st.verberge());
        assertEquals(List.of(), st.verberge());
    }

    @Test
    void nachVerbergenOderAbmeldenNeuAnlegen() {
        SeitenStand st = new SeitenStand();
        st.zeige(Wert.TODE, List.of(new Platz(1, A, "Anna", 1)));
        st.verberge();
        assertEquals(new SeitenOp.Anlegen("Rangliste: Tode"), st.zeige(Wert.TODE, List.of()).get(0));
        st.abgemeldet();
        List<SeitenOp> ops = st.zeige(Wert.TODE, List.of());
        assertEquals(new SeitenOp.Anlegen("Rangliste: Tode"), ops.get(0));
        assertFalse(ops.contains(new SeitenOp.Loeschen("Anna")));
    }

    @Test
    void erneutesEinschaltenZeigtWiederRechts() {
        SeitenStand st = new SeitenStand();
        st.zeige(Wert.SPIELZEIT, List.of());
        List<SeitenOp> ops = st.zeige(Wert.TODE, List.of());
        assertTrue(ops.contains(new SeitenOp.Anzeigen()));          // holt den Platz rechts zurück
        assertFalse(ops.stream().anyMatch(o -> o instanceof SeitenOp.Anlegen));
    }

    @Test
    void aktualisierenVerdraengtNichts() {
        SeitenStand st = new SeitenStand();
        st.zeige(Wert.TODE, List.of());
        List<SeitenOp> ops = st.aktualisiere(Wert.TODE, List.of(new Platz(1, A, "Anna", 3)));
        assertFalse(ops.contains(new SeitenOp.Anzeigen()));
        assertTrue(ops.contains(new SeitenOp.Aendern("Rangliste: Tode")));
        assertTrue(ops.contains(new SeitenOp.Setzen("Anna", 3, "Anna", "3")));
    }

    @Test
    void aktualisierenOhneAnlageLegtAn() {
        List<SeitenOp> ops = new SeitenStand().aktualisiere(Wert.TODE, List.of());
        assertEquals(new SeitenOp.Anlegen("Rangliste: Tode"), ops.get(0));
        assertEquals(new SeitenOp.Anzeigen(), ops.get(1));
    }

    @Test
    void objectiveNameNichtPerBefehlAnlegbar() {
        // /scoreboard objectives add erlaubt nur diese Zeichen; ein anderes schließt eine Kollision aus.
        assertFalse(SeitenStand.OBJECTIVE.matches("[0-9A-Za-z_.+-]+"), SeitenStand.OBJECTIVE);
    }

    @Test
    void punkteBegrenzt() {
        assertEquals(Integer.MAX_VALUE, SeitenStand.punkte(5_000_000_000L));
        assertEquals(0, SeitenStand.punkte(-3));
        assertEquals(42, SeitenStand.punkte(42));
    }
}
