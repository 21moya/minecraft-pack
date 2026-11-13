package farmxwinter.rangliste.kern;

import java.util.Optional;
import java.util.function.Predicate;

/** Die Werte, für die es Ranglisten gibt. Kennung = Befehlsargument. */
public enum Wert {
    SPIELZEIT("spielzeit", "Spielzeit", Einheit.STUNDEN, eigene("minecraft:play_time")),
    TODE("tode", "Tode", Einheit.ANZAHL, eigene("minecraft:deaths")),
    MOBKILLS("mobkills", "Mob-Kills", Einheit.ANZAHL, eigene("minecraft:mob_kills")),
    PVP("pvp", "PvP-Kills", Einheit.ANZAHL, eigene("minecraft:player_kills")),
    ABGEBAUT("abgebaut", "Abgebaut", Einheit.ANZAHL, (s, istBlockItem) -> s.summe(StatSheet.MINED)),
    PLATZIERT("platziert", "Platziert", Einheit.ANZAHL, (s, istBlockItem) -> s.summe(StatSheet.USED, istBlockItem)),
    HERGESTELLT("hergestellt", "Hergestellt", Einheit.ANZAHL, (s, istBlockItem) -> s.summe(StatSheet.CRAFTED)),
    STRECKE("strecke", "Strecke zu Fuß", Einheit.KILOMETER, eigene(
            "minecraft:walk_one_cm", "minecraft:sprint_one_cm", "minecraft:crouch_one_cm",
            "minecraft:swim_one_cm", "minecraft:walk_on_water_one_cm", "minecraft:walk_under_water_one_cm",
            "minecraft:climb_one_cm")),
    ELYTRA("elytra", "Elytra-Flug", Einheit.KILOMETER, eigene("minecraft:aviate_one_cm")),
    FISCHE("fische", "Fische", Einheit.ANZAHL, eigene("minecraft:fish_caught")),
    HANDEL("handel", "Handel", Einheit.ANZAHL, eigene("minecraft:traded_with_villager")),
    GEZUECHTET("gezuechtet", "Gezüchtet", Einheit.ANZAHL, eigene("minecraft:animals_bred")),
    SCHADEN("schaden", "Schaden ausgeteilt", Einheit.HERZEN, eigene("minecraft:damage_dealt")),
    ERLITTEN("erlitten", "Schaden erlitten", Einheit.HERZEN, eigene("minecraft:damage_taken")),
    SPRUENGE("spruenge", "Sprünge", Einheit.ANZAHL, eigene("minecraft:jump"));

    private final String kennung;
    private final String anzeige;
    private final Einheit einheit;
    private final Quelle quelle;

    Wert(String kennung, String anzeige, Einheit einheit, Quelle quelle) {
        this.kennung = kennung;
        this.anzeige = anzeige;
        this.einheit = einheit;
        this.quelle = quelle;
    }

    public String kennung() {
        return kennung;
    }

    public String anzeige() {
        return anzeige;
    }

    public Einheit einheit() {
        return einheit;
    }

    /** Rohwert für einen Spieler; {@code istBlockItem} sagt, ob ein Item-Schlüssel ein Block ist. */
    public long berechne(StatSheet s, Predicate<String> istBlockItem) {
        return quelle.aus(s, istBlockItem);
    }

    public static Optional<Wert> zuKennung(String kennung) {
        for (Wert w : values()) {
            if (w.kennung.equals(kennung)) {
                return Optional.of(w);
            }
        }
        return Optional.empty();
    }

    @FunctionalInterface
    private interface Quelle {
        long aus(StatSheet s, Predicate<String> istBlockItem);
    }

    /** Summe von Schlüsseln aus {@code minecraft:custom}. */
    private static Quelle eigene(String... schluessel) {
        return (s, istBlockItem) -> {
            long summe = 0;
            for (String k : schluessel) {
                summe += s.get(StatSheet.CUSTOM, k);
            }
            return summe;
        };
    }
}
