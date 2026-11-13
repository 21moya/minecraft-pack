package farmxwinter.rangliste;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import farmxwinter.rangliste.kern.StatDatei;
import farmxwinter.rangliste.kern.StatSheet;
import farmxwinter.rangliste.kern.Texte;
import farmxwinter.rangliste.kern.Wert;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;

/**
 * Holt die Statistiken aller Spieler: Offline-Spieler aus {@code <welt>/stats/<uuid>.json}, verbundene Spieler
 * aus den Live-Zählern (die Datei schreibt Minecraft nur beim Speichern). Nur Server-Thread.
 */
final class StatQuellen {
    private static final Logger LOG = LogUtils.getLogger();

    private record Gelesen(long geaendert, Optional<StatSheet> sheet) {
    }

    private static final Map<Path, Gelesen> DATEIEN = new HashMap<>();
    private static final Set<Path> GEWARNT = new HashSet<>();

    private StatQuellen() {
    }

    static Map<UUID, StatSheet> sammle(MinecraftServer server) {
        Map<UUID, StatSheet> alle = new HashMap<>();
        Path ordner = server.getWorldPath(LevelResource.PLAYER_STATS_DIR);
        if (Files.isDirectory(ordner)) {
            try (DirectoryStream<Path> dateien = Files.newDirectoryStream(ordner, "*.json")) {
                for (Path datei : dateien) {
                    Optional<UUID> id = uuidAus(datei);
                    if (id.isPresent()) {
                        lies(datei).ifPresent(sheet -> alle.put(id.get(), sheet));
                    }
                }
            } catch (IOException | DirectoryIteratorException e) {
                LOG.warn("Rangliste: Statistik-Ordner {} nicht lesbar: {}", ordner, e.toString());
            }
        }
        for (ServerPlayer spieler : server.getPlayerList().getPlayers()) {
            alle.put(spieler.getUUID(), live(server.getPlayerList().getPlayerStats(spieler)));
        }
        return alle;
    }

    static Map<UUID, Long> werte(MinecraftServer server, Wert w) {
        return werte(sammle(server), w);
    }

    /** Wie oben, für mehrere Werte aus einmal gesammelten Daten. */
    static Map<UUID, Long> werte(Map<UUID, StatSheet> alle, Wert w) {
        Map<UUID, Long> werte = new HashMap<>();
        alle.forEach((id, sheet) -> werte.put(id, w.berechne(sheet, StatQuellen::istBlockItem)));
        return werte;
    }

    /** Name nur aus lokalen Quellen (verbundener Spieler, Profil-Cache per UUID), nie übers Netz. */
    static String name(MinecraftServer server, UUID id) {
        ServerPlayer spieler = server.getPlayerList().getPlayer(id);
        if (spieler != null) {
            return spieler.getGameProfile().getName();
        }
        GameProfileCache cache = server.getProfileCache();
        if (cache != null) {
            Optional<String> name = cache.get(id).map(GameProfile::getName);
            if (name.isPresent()) {
                return name.get();
            }
        }
        return Texte.namensErsatz(id);
    }

    static boolean istBlockItem(String schluessel) {
        ResourceLocation id = ResourceLocation.tryParse(schluessel);
        return id != null && BuiltInRegistries.ITEM.get(id) instanceof BlockItem;
    }

    private static Optional<UUID> uuidAus(Path datei) {
        String name = datei.getFileName().toString();
        String ohneEndung = name.substring(0, name.length() - ".json".length());
        if (ohneEndung.length() != 36) {
            return Optional.empty();
        }
        try {
            return Optional.of(UUID.fromString(ohneEndung));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private static Optional<StatSheet> lies(Path datei) {
        try {
            long geaendert = Files.getLastModifiedTime(datei).toMillis();
            Gelesen alt = DATEIEN.get(datei);
            if (alt != null && alt.geaendert() == geaendert) {
                return alt.sheet();
            }
            Optional<StatSheet> sheet = StatDatei.lies(Files.readString(datei, StandardCharsets.UTF_8));
            if (sheet.isEmpty()) {
                warneEinmal(datei, "ungültiges Format");
            }
            DATEIEN.put(datei, new Gelesen(geaendert, sheet));
            return sheet;
        } catch (IOException e) {
            warneEinmal(datei, e.toString());
            return Optional.empty();
        }
    }

    private static void warneEinmal(Path datei, String grund) {
        if (GEWARNT.add(datei)) {
            LOG.warn("Rangliste: Statistik-Datei {} übersprungen: {}", datei.getFileName(), grund);
        }
    }

    private static StatSheet live(ServerStatsCounter zaehler) {
        StatSheet.Builder builder = StatSheet.builder();
        uebernimm(Stats.CUSTOM, zaehler, builder);
        uebernimm(Stats.BLOCK_MINED, zaehler, builder);
        uebernimm(Stats.ITEM_CRAFTED, zaehler, builder);
        uebernimm(Stats.ITEM_USED, zaehler, builder);
        return builder.build();
    }

    private static <T> void uebernimm(StatType<T> typ, ServerStatsCounter zaehler, StatSheet.Builder builder) {
        ResourceLocation typSchluessel = BuiltInRegistries.STAT_TYPE.getKey(typ);
        if (typSchluessel == null) {
            return;
        }
        Registry<T> register = typ.getRegistry();
        for (T eintrag : register) {
            int wert = zaehler.getValue(typ, eintrag);
            ResourceLocation schluessel = register.getKey(eintrag);
            if (wert != 0 && schluessel != null) {
                builder.put(typSchluessel.toString(), schluessel.toString(), wert);
            }
        }
    }
}
