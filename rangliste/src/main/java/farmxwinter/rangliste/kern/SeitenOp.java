package farmxwinter.rangliste.kern;

/** Was einem Client für die persönliche Seitenleiste geschickt werden muss. */
public sealed interface SeitenOp {

    /** Objective auf dem Client anlegen. */
    record Anlegen(String titel) implements SeitenOp {
    }

    /** Titel des vorhandenen Objectives ändern. */
    record Aendern(String titel) implements SeitenOp {
    }

    /** Objective rechts anzeigen. */
    record Anzeigen() implements SeitenOp {
    }

    /** Objective vom Client entfernen (blendet es aus). */
    record Entfernen() implements SeitenOp {
    }

    /** Eintrag setzen: Besitzer, Sortierwert, angezeigter Name, angezeigter Wert. */
    record Setzen(String besitzer, int punkte, String anzeige, String wertText) implements SeitenOp {
    }

    /** Eintrag löschen. */
    record Loeschen(String besitzer) implements SeitenOp {
    }
}
