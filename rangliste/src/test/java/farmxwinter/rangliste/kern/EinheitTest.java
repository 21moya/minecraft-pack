package farmxwinter.rangliste.kern;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class EinheitTest {

    @Test
    void anzahlMitTausenderpunkt() {
        assertEquals("0", Einheit.ANZAHL.formatiere(0));
        assertEquals("12.345", Einheit.ANZAHL.formatiere(12345));
        assertEquals("1.234.567", Einheit.ANZAHL.formatiere(1234567));
    }

    @Test
    void stundenAusTicksMitKomma() {
        assertEquals("12,3 h", Einheit.STUNDEN.formatiere(885600)); // 12,3 × 72000 Ticks
        assertEquals("1,0 h", Einheit.STUNDEN.formatiere(72000));
    }

    @Test
    void unterEinerStundeInMinuten() {
        assertEquals("0 min", Einheit.STUNDEN.formatiere(0));
        assertEquals("0 min", Einheit.STUNDEN.formatiere(1000)); // 0,8 min: abgerundet
        assertEquals("12 min", Einheit.STUNDEN.formatiere(14400)); // 1200 Ticks je Minute
        assertEquals("59 min", Einheit.STUNDEN.formatiere(71999)); // nie „60 min“
    }

    @Test
    void kilometerAusZentimetern() {
        assertEquals("4,2 km", Einheit.KILOMETER.formatiere(420000));
        assertEquals("1.234,5 km", Einheit.KILOMETER.formatiere(123450000));
    }

    @Test
    void herzenAusZehntelLebenspunkten() {
        assertEquals("50 Herzen", Einheit.HERZEN.formatiere(1000));
        assertEquals("1 Herz", Einheit.HERZEN.formatiere(20));
    }
}
