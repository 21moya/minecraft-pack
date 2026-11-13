package farmxwinter.rangliste.kern;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/** Statistiken eines Spielers wie in {@code stats/<uuid>.json}: Typ → Schlüssel → Wert. */
public final class StatSheet {
    public static final String CUSTOM = "minecraft:custom";
    public static final String MINED = "minecraft:mined";
    public static final String CRAFTED = "minecraft:crafted";
    public static final String USED = "minecraft:used";

    private final Map<String, Map<String, Long>> werte;

    private StatSheet(Map<String, Map<String, Long>> werte) {
        this.werte = werte;
    }

    public static Builder builder() {
        return new Builder();
    }

    /** Wert eines Schlüssels, 0 wenn er fehlt. */
    public long get(String typ, String schluessel) {
        return werte.getOrDefault(typ, Map.of()).getOrDefault(schluessel, 0L);
    }

    public long summe(String typ) {
        return summe(typ, schluessel -> true);
    }

    public long summe(String typ, Predicate<String> schluesselFilter) {
        long summe = 0;
        for (Map.Entry<String, Long> eintrag : werte.getOrDefault(typ, Map.of()).entrySet()) {
            if (schluesselFilter.test(eintrag.getKey())) {
                summe += eintrag.getValue();
            }
        }
        return summe;
    }

    public static final class Builder {
        private final Map<String, Map<String, Long>> werte = new HashMap<>();

        private Builder() {
        }

        public Builder put(String typ, String schluessel, long wert) {
            werte.computeIfAbsent(typ, t -> new HashMap<>()).put(schluessel, wert);
            return this;
        }

        public StatSheet build() {
            Map<String, Map<String, Long>> kopie = new HashMap<>();
            werte.forEach((typ, eintraege) -> kopie.put(typ, Map.copyOf(eintraege)));
            return new StatSheet(Map.copyOf(kopie));
        }
    }
}
