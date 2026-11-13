package farmxwinter.rangliste.kern;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.util.Map;
import java.util.Optional;

/** Liest eine Statistik-Datei ({@code stats/<uuid>.json}). Kaputte Dateien ergeben nichts, nie eine Ausnahme. */
public final class StatDatei {

    private StatDatei() {
    }

    public static Optional<StatSheet> lies(String json) {
        try {
            JsonElement wurzel = JsonParser.parseString(json);
            if (!wurzel.isJsonObject()) {
                return Optional.empty();
            }
            StatSheet.Builder builder = StatSheet.builder();
            JsonElement stats = wurzel.getAsJsonObject().get("stats");
            if (stats != null && stats.isJsonObject()) {
                for (Map.Entry<String, JsonElement> typ : stats.getAsJsonObject().entrySet()) {
                    if (typ.getValue().isJsonObject()) {
                        uebernimm(typ.getKey(), typ.getValue().getAsJsonObject(), builder);
                    }
                }
            }
            return Optional.of(builder.build());
        } catch (JsonParseException | IllegalStateException e) {
            return Optional.empty();
        }
    }

    private static void uebernimm(String typ, JsonObject eintraege, StatSheet.Builder builder) {
        for (Map.Entry<String, JsonElement> eintrag : eintraege.entrySet()) {
            JsonElement wert = eintrag.getValue();
            if (wert.isJsonPrimitive() && wert.getAsJsonPrimitive().isNumber()) {
                try {
                    builder.put(typ, eintrag.getKey(), wert.getAsLong());
                } catch (NumberFormatException e) {
                    // Wert passt nicht in long: überspringen.
                }
            }
        }
    }
}
