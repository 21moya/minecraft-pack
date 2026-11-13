package farmxwinter.rangliste.kern;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/** Wie ein Rohwert aus den Statistiken angezeigt wird (deutsches Zahlenformat). */
public enum Einheit {
    /** Anzahl, mit Tausenderpunkt. */
    ANZAHL,
    /** Ticks → unter einer Stunde ganze Minuten (abgerundet), sonst Stunden mit einer Nachkommastelle. */
    STUNDEN,
    /** Zentimeter → Kilometer, eine Nachkommastelle. */
    KILOMETER,
    /** Zehntel-Lebenspunkte → Herzen (20 je Herz), ganzzahlig. */
    HERZEN;

    private static final long TICKS_JE_MINUTE = 1_200;
    private static final long TICKS_JE_STUNDE = 72_000;

    public String formatiere(long roh) {
        return switch (this) {
            case ANZAHL -> ganz(roh);
            case STUNDEN -> roh < TICKS_JE_STUNDE
                    ? ganz(roh / TICKS_JE_MINUTE) + " min"
                    : dezimal(roh / (double) TICKS_JE_STUNDE) + " h";
            case KILOMETER -> dezimal(roh / 100_000.0) + " km";
            case HERZEN -> {
                long herzen = Math.round(roh / 20.0);
                yield ganz(herzen) + (herzen == 1 ? " Herz" : " Herzen");
            }
        };
    }

    private static String ganz(long wert) {
        return new DecimalFormat("#,##0", DecimalFormatSymbols.getInstance(Locale.GERMANY)).format(wert);
    }

    private static String dezimal(double wert) {
        DecimalFormat format = new DecimalFormat("#,##0.0", DecimalFormatSymbols.getInstance(Locale.GERMANY));
        format.setRoundingMode(RoundingMode.HALF_UP);
        return format.format(wert);
    }
}
