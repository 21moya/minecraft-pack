package farmxwinter.rangliste;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/** Einstieg der Mod. Sie läuft nur auf dem Server und nutzt nur Vanilla-Pakete. */
@Mod(RanglisteMod.MOD_ID)
public final class RanglisteMod {
    public static final String MOD_ID = "rangliste";

    public RanglisteMod(IEventBus modBus) {
        NeoForge.EVENT_BUS.addListener(RanglisteMod::befehle);
        NeoForge.EVENT_BUS.addListener(RanglisteMod::angemeldet);
        NeoForge.EVENT_BUS.addListener(RanglisteMod::abgemeldet);
        NeoForge.EVENT_BUS.addListener(RanglisteMod::tick);
        NeoForge.EVENT_BUS.addListener(RanglisteMod::gestoppt);
    }

    private static void befehle(RegisterCommandsEvent event) {
        Befehle.registriere(event.getDispatcher());
    }

    private static void angemeldet(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer spieler) {
            Seitenleiste.angemeldet(spieler);
        }
    }

    private static void abgemeldet(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer spieler) {
            Seitenleiste.abgemeldet(spieler);
        }
    }

    private static void tick(ServerTickEvent.Post event) {
        Seitenleiste.tick(event.getServer());
    }

    private static void gestoppt(ServerStoppedEvent event) {
        Seitenleiste.vergiss();
    }
}
