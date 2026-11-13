package farmxwinter.rangliste.kern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StatDateiTest {

    @Test
    void liestVanillaFormat() {
        String json = "{\"stats\":{\"minecraft:custom\":{\"minecraft:deaths\":3},"
                + "\"minecraft:mined\":{\"minecraft:dirt\":4}},\"DataVersion\":3955}";
        StatSheet s = StatDatei.lies(json).orElseThrow();
        assertEquals(3, s.get(StatSheet.CUSTOM, "minecraft:deaths"));
        assertEquals(4, s.summe(StatSheet.MINED));
    }

    @Test
    void kaputtesJson() {
        assertTrue(StatDatei.lies("{\"stats\":{\"minecraft:cus").isEmpty());
        assertTrue(StatDatei.lies("").isEmpty());
        assertTrue(StatDatei.lies("[]").isEmpty());
    }

    @Test
    void nichtZahlenWerdenIgnoriert() {
        StatSheet s = StatDatei.lies("{\"stats\":{\"minecraft:custom\":{\"minecraft:jump\":\"x\"}}}").orElseThrow();
        assertEquals(0, s.get(StatSheet.CUSTOM, "minecraft:jump"));
    }
}
