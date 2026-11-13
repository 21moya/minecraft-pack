package farmxwinter.rangliste.kern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;

class WertTest {

    private static final StatSheet BEISPIEL = StatSheet.builder()
            .put(StatSheet.CUSTOM, "minecraft:play_time", 885600)
            .put(StatSheet.CUSTOM, "minecraft:walk_one_cm", 100000)
            .put(StatSheet.CUSTOM, "minecraft:sprint_one_cm", 50000)
            .put(StatSheet.CUSTOM, "minecraft:aviate_one_cm", 999)
            .put(StatSheet.MINED, "minecraft:stone", 10)
            .put(StatSheet.MINED, "create:zinc_ore", 5)
            .put(StatSheet.USED, "minecraft:stone", 7)
            .put(StatSheet.USED, "minecraft:bread", 3)
            .build();

    private static final Predicate<String> NUR_STEIN_IST_BLOCK = k -> k.equals("minecraft:stone");

    @Test
    void spielzeitAusTicks() {
        assertEquals(885600, Wert.SPIELZEIT.berechne(BEISPIEL, NUR_STEIN_IST_BLOCK));
    }

    @Test
    void abgebautZaehltAuchModBloecke() {
        assertEquals(15, Wert.ABGEBAUT.berechne(BEISPIEL, NUR_STEIN_IST_BLOCK));
    }

    @Test
    void platziertNurBlockItems() {
        assertEquals(7, Wert.PLATZIERT.berechne(BEISPIEL, NUR_STEIN_IST_BLOCK));
    }

    @Test
    void streckeOhneElytra() {
        assertEquals(150000, Wert.STRECKE.berechne(BEISPIEL, NUR_STEIN_IST_BLOCK));
    }

    @Test
    void fehlenderWertIstNull() {
        assertEquals(0, Wert.TODE.berechne(BEISPIEL, NUR_STEIN_IST_BLOCK));
    }

    @Test
    void fuenfzehnWerteMitEinfachenKennungen() {
        assertEquals(15, Wert.values().length);
        for (Wert w : Wert.values()) {
            assertTrue(w.kennung().matches("[a-z]+"), w.kennung());
        }
    }

    @Test
    void kennungNachschlagen() {
        assertEquals(Optional.of(Wert.GEZUECHTET), Wert.zuKennung("gezuechtet"));
        assertTrue(Wert.zuKennung("Spielzeit").isEmpty());
    }
}
