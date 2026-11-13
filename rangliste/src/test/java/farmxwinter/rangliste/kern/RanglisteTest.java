package farmxwinter.rangliste.kern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class RanglisteTest {

    private static final UUID A = new UUID(0, 1);
    private static final UUID B = new UUID(0, 2);
    private static final UUID C = new UUID(0, 3);
    private static final UUID D = new UUID(0, 4);
    private static final Map<UUID, String> NAMEN = Map.of(A, "bert", B, "Anna", C, "cem", D, "dora");

    @Test
    void absteigendGleichstandNachNameOhneGrossKlein() {
        List<Platz> r = Rangliste.berechne(Map.of(A, 5L, B, 5L, C, 9L, D, 0L), NAMEN::get);
        assertEquals(List.of(new Platz(1, C, "cem", 9), new Platz(2, B, "Anna", 5), new Platz(3, A, "bert", 5)), r);
    }

    @Test
    void nullenWeg() {
        assertTrue(Rangliste.berechne(Map.of(A, 0L), NAMEN::get).isEmpty());
        assertTrue(Rangliste.berechne(Map.of(), NAMEN::get).isEmpty());
    }
}
