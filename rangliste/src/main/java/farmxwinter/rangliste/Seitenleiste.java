package farmxwinter.rangliste;

import farmxwinter.rangliste.kern.Platz;
import farmxwinter.rangliste.kern.Rangliste;
import farmxwinter.rangliste.kern.SeitenOp;
import farmxwinter.rangliste.kern.SeitenStand;
import farmxwinter.rangliste.kern.StatSheet;
import farmxwinter.rangliste.kern.Wert;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.FixedFormat;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundResetScorePacket;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

/**
 * Persönliche Seitenleiste: ein Objective, das nur der eine Client kennt. Der Server-Scoreboard bleibt unberührt.
 * Nur Server-Thread.
 */
final class Seitenleiste {
    /** Aktualisierung alle 10 Sekunden. */
    private static final int TAKT = 200;

    private static final Map<UUID, SeitenStand> STAENDE = new HashMap<>();
    private static int ticks;

    private Seitenleiste() {
    }

    static void zeige(ServerPlayer spieler, Wert w) {
        MinecraftServer server = spieler.getServer();
        if (server == null) {
            return;
        }
        List<Platz> top = Rangliste.berechne(StatQuellen.werte(server, w), id -> StatQuellen.name(server, id));
        sende(spieler, stand(spieler).zeige(w, top));
    }

    /** Blendet aus; {@code false}, wenn nichts eingeblendet war. */
    static boolean verberge(ServerPlayer spieler) {
        List<SeitenOp> ops = stand(spieler).verberge();
        sende(spieler, ops);
        return !ops.isEmpty();
    }

    static void angemeldet(ServerPlayer spieler) {
        MinecraftServer server = spieler.getServer();
        if (server != null) {
            SeitenWahl.von(server).wahl(spieler.getUUID()).ifPresent(w -> zeige(spieler, w));
        }
    }

    /** Client ist weg, sein Scoreboard auch. Die Wahl bleibt gespeichert. */
    static void abgemeldet(ServerPlayer spieler) {
        STAENDE.remove(spieler.getUUID());
    }

    /** Server stoppt (im Einzelspieler läuft die JVM weiter). */
    static void vergiss() {
        STAENDE.clear();
        ticks = 0;
    }

    static void tick(MinecraftServer server) {
        if (++ticks < TAKT) {
            return;
        }
        ticks = 0;
        SeitenWahl wahl = SeitenWahl.von(server);
        Map<UUID, StatSheet> alle = null;
        Map<Wert, List<Platz>> tops = new EnumMap<>(Wert.class);
        for (ServerPlayer spieler : server.getPlayerList().getPlayers()) {
            Optional<Wert> w = wahl.wahl(spieler.getUUID());
            if (w.isEmpty()) {
                continue;
            }
            if (alle == null) {
                alle = StatQuellen.sammle(server);
            }
            Map<UUID, StatSheet> daten = alle;
            List<Platz> top = tops.computeIfAbsent(w.get(),
                    x -> Rangliste.berechne(StatQuellen.werte(daten, x), id -> StatQuellen.name(server, id)));
            sende(spieler, stand(spieler).aktualisiere(w.get(), top));
        }
    }

    private static SeitenStand stand(ServerPlayer spieler) {
        return STAENDE.computeIfAbsent(spieler.getUUID(), id -> new SeitenStand());
    }

    private static void sende(ServerPlayer spieler, List<SeitenOp> ops) {
        for (SeitenOp op : ops) {
            spieler.connection.send(paket(op));
        }
    }

    private static Packet<?> paket(SeitenOp op) {
        return switch (op) {
            case SeitenOp.Anlegen a ->
                    new ClientboundSetObjectivePacket(objective(a.titel()), ClientboundSetObjectivePacket.METHOD_ADD);
            case SeitenOp.Aendern a ->
                    new ClientboundSetObjectivePacket(objective(a.titel()), ClientboundSetObjectivePacket.METHOD_CHANGE);
            case SeitenOp.Anzeigen a -> new ClientboundSetDisplayObjectivePacket(DisplaySlot.SIDEBAR, objective(""));
            case SeitenOp.Entfernen e ->
                    new ClientboundSetObjectivePacket(objective(""), ClientboundSetObjectivePacket.METHOD_REMOVE);
            case SeitenOp.Setzen s -> new ClientboundSetScorePacket(s.besitzer(), SeitenStand.OBJECTIVE, s.punkte(),
                    Optional.of(Component.literal(s.anzeige())),
                    Optional.of(new FixedFormat(Component.literal(s.wertText()))));
            case SeitenOp.Loeschen l -> new ClientboundResetScorePacket(l.besitzer(), SeitenStand.OBJECTIVE);
        };
    }

    /** Objective nur als Paketinhalt; es wird in keinem Scoreboard registriert. */
    private static Objective objective(String titel) {
        return new Objective(new Scoreboard(), SeitenStand.OBJECTIVE, ObjectiveCriteria.DUMMY,
                Component.literal(titel).withStyle(ChatFormatting.GOLD), ObjectiveCriteria.RenderType.INTEGER, false,
                null);
    }
}
