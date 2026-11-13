package farmxwinter.rangliste;

import farmxwinter.rangliste.kern.Wert;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

/** Welche Seitenleiste jeder Spieler gewählt hat; liegt in der Welt unter {@code data/rangliste.dat}. */
final class SeitenWahl extends SavedData {
    private static final String DATEI = "rangliste";
    private static final String SCHLUESSEL = "ansicht";

    private final Map<UUID, Wert> wahl = new HashMap<>();

    static SeitenWahl von(MinecraftServer server) {
        return server.overworld().getDataStorage()
                .computeIfAbsent(new SavedData.Factory<>(SeitenWahl::new, SeitenWahl::lade), DATEI);
    }

    private static SeitenWahl lade(CompoundTag tag, HolderLookup.Provider register) {
        SeitenWahl geladen = new SeitenWahl();
        CompoundTag spieler = tag.getCompound(SCHLUESSEL);
        for (String schluessel : spieler.getAllKeys()) {
            try {
                UUID id = UUID.fromString(schluessel);
                Wert.zuKennung(spieler.getString(schluessel)).ifPresent(w -> geladen.wahl.put(id, w));
            } catch (IllegalArgumentException e) {
                // Kein UUID-Schlüssel: ignorieren.
            }
        }
        return geladen;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider register) {
        CompoundTag spieler = new CompoundTag();
        wahl.forEach((id, w) -> spieler.putString(id.toString(), w.kennung()));
        tag.put(SCHLUESSEL, spieler);
        return tag;
    }

    Optional<Wert> wahl(UUID id) {
        return Optional.ofNullable(wahl.get(id));
    }

    void setze(UUID id, Wert w) {
        if (wahl.put(id, w) != w) {
            setDirty();
        }
    }

    void entferne(UUID id) {
        if (wahl.remove(id) != null) {
            setDirty();
        }
    }
}
