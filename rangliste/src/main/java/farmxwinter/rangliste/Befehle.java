package farmxwinter.rangliste;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import farmxwinter.rangliste.kern.Platz;
import farmxwinter.rangliste.kern.Rangliste;
import farmxwinter.rangliste.kern.StatSheet;
import farmxwinter.rangliste.kern.Texte;
import farmxwinter.rangliste.kern.Wert;
import java.util.List;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

/**
 * {@code /rangliste} (Menü), {@code /rangliste <wert>}, {@code /rangliste ich},
 * {@code /rangliste ansicht <wert>|aus}. Nur feste Wörter, keine Rechte nötig.
 */
final class Befehle {
    private static final int TOP = 10;

    private Befehle() {
    }

    static void registriere(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> wurzel = Commands.literal("rangliste")
                .executes(ctx -> menue(ctx.getSource()));
        LiteralArgumentBuilder<CommandSourceStack> ansicht = Commands.literal("ansicht");
        for (Wert w : Wert.values()) {
            wurzel.then(Commands.literal(w.kennung()).executes(ctx -> rangliste(ctx.getSource(), w)));
            ansicht.then(Commands.literal(w.kennung()).executes(ctx -> ansichtAn(ctx.getSource(), w)));
        }
        ansicht.then(Commands.literal("aus").executes(ctx -> ansichtAus(ctx.getSource())));
        wurzel.then(Commands.literal("ich").executes(ctx -> meine(ctx.getSource())));
        wurzel.then(ansicht);
        dispatcher.register(wurzel);
    }

    private static int rangliste(CommandSourceStack source, Wert w) {
        MinecraftServer server = source.getServer();
        List<Platz> plaetze = Rangliste.berechne(StatQuellen.werte(server, w), id -> StatQuellen.name(server, id));
        if (plaetze.isEmpty()) {
            source.sendSuccess(() -> Component.literal(Texte.keineWerte(w)), false);
            return Command.SINGLE_SUCCESS;
        }
        ServerPlayer ich = source.getPlayer();
        UUID meineId = ich == null ? null : ich.getUUID();
        MutableComponent text = Component.literal(Texte.kopf(w)).withStyle(ChatFormatting.GOLD);
        for (Platz p : plaetze.subList(0, Math.min(TOP, plaetze.size()))) {
            text.append("\n").append(zeile(p, w, meineId));
        }
        if (meineId != null) {
            plaetze.stream()
                    .filter(p -> p.platz() > TOP && p.uuid().equals(meineId))
                    .findFirst()
                    .ifPresent(p -> text.append("\n").append(zeile(p, w, meineId)));
        }
        source.sendSuccess(() -> text, false);
        return Command.SINGLE_SUCCESS;
    }

    private static Component zeile(Platz p, Wert w, UUID meineId) {
        MutableComponent zeile = Component.literal(Texte.zeile(p, w));
        return p.uuid().equals(meineId) ? zeile.withStyle(ChatFormatting.YELLOW) : zeile;
    }

    private static int menue(CommandSourceStack source) {
        MutableComponent text = Component.literal(Texte.menueKopf()).withStyle(ChatFormatting.GOLD);
        for (Wert w : Wert.values()) {
            text.append("\n")
                    .append(knopf(w.anzeige(), "/rangliste " + w.kennung(), Texte.hinweisChat(w), ChatFormatting.AQUA))
                    .append(" ")
                    .append(knopf(Texte.knopfRechts(), "/rangliste ansicht " + w.kennung(), Texte.hinweisRechts(w),
                            ChatFormatting.GREEN));
        }
        text.append("\n")
                .append(knopf(Texte.knopfMeine(), "/rangliste ich", null, ChatFormatting.YELLOW))
                .append(" ")
                .append(knopf(Texte.knopfAus(), "/rangliste ansicht aus", null, ChatFormatting.RED));
        source.sendSuccess(() -> text, false);
        return Command.SINGLE_SUCCESS;
    }

    private static Component knopf(String text, String befehl, String hinweis, ChatFormatting farbe) {
        Style stil = Style.EMPTY.withColor(farbe).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, befehl));
        if (hinweis != null) {
            stil = stil.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(hinweis)));
        }
        return Component.literal(text).withStyle(stil);
    }

    private static int meine(CommandSourceStack source) {
        ServerPlayer ich = source.getPlayer();
        if (ich == null) {
            source.sendFailure(Component.literal(Texte.nurSpieler()));
            return 0;
        }
        StatSheet sheet = StatQuellen.sammle(source.getServer())
                .getOrDefault(ich.getUUID(), StatSheet.builder().build());
        MutableComponent text = Component.literal(Texte.meineKopf()).withStyle(ChatFormatting.GOLD);
        for (Wert w : Wert.values()) {
            text.append("\n").append(Component.literal(Texte.meineZeile(w, w.berechne(sheet, StatQuellen::istBlockItem))));
        }
        source.sendSuccess(() -> text, false);
        return Command.SINGLE_SUCCESS;
    }

    private static int ansichtAn(CommandSourceStack source, Wert w) {
        ServerPlayer ich = source.getPlayer();
        if (ich == null) {
            source.sendFailure(Component.literal(Texte.nurSpieler()));
            return 0;
        }
        Seitenleiste.zeige(ich, w);
        SeitenWahl.von(source.getServer()).setze(ich.getUUID(), w);
        source.sendSuccess(() -> Component.literal(Texte.seiteAn(w)), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int ansichtAus(CommandSourceStack source) {
        ServerPlayer ich = source.getPlayer();
        if (ich == null) {
            source.sendFailure(Component.literal(Texte.nurSpieler()));
            return 0;
        }
        boolean warAn = Seitenleiste.verberge(ich);
        SeitenWahl.von(source.getServer()).entferne(ich.getUUID());
        source.sendSuccess(() -> Component.literal(warAn ? Texte.seiteAus() : Texte.seiteWarAus()), false);
        return Command.SINGLE_SUCCESS;
    }
}
