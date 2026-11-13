package farmxwinter.rangliste.kern;

import java.util.UUID;

/** Ein Eintrag einer Rangliste: Platz (ab 1), Spieler, Rohwert. */
public record Platz(int platz, UUID uuid, String name, long wert) {
}
