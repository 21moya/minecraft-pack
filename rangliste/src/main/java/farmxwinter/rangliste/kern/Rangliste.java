package farmxwinter.rangliste.kern;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

/** Bildet aus Rohwerten die Rangfolge. */
public final class Rangliste {

    private Rangliste() {
    }

    /**
     * Absteigend nach Wert, bei Gleichstand nach Name (ohne Groß/Klein). Wert 0 fällt weg.
     * Plätze laufen fortlaufend ab 1.
     */
    public static List<Platz> berechne(Map<UUID, Long> werte, Function<UUID, String> namen) {
        record Roh(UUID uuid, String name, long wert) {
        }
        List<Roh> sortiert = werte.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .map(e -> new Roh(e.getKey(),
                        Objects.requireNonNullElse(namen.apply(e.getKey()), Texte.namensErsatz(e.getKey())),
                        e.getValue()))
                .sorted(Comparator.<Roh>comparingLong(Roh::wert).reversed()
                        .thenComparing(Roh::name, String.CASE_INSENSITIVE_ORDER))
                .toList();
        List<Platz> plaetze = new ArrayList<>(sortiert.size());
        for (int i = 0; i < sortiert.size(); i++) {
            Roh r = sortiert.get(i);
            plaetze.add(new Platz(i + 1, r.uuid(), r.name(), r.wert()));
        }
        return List.copyOf(plaetze);
    }
}
