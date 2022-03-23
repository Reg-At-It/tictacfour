package de.uni_bremen.pi2;

import static de.uni_bremen.pi2.Player.*;
import static de.uni_bremen.pi2.Result.*;

/**
 * author: Artur Wisniewski
 */
class FourInARow {
    /**
     * Das Spielfeld.
     */
    private final Player[][] currentField;

    /**
     * Die Suchtiefe
     */
    private final int depth;

    /**
     * Konstruktor.
     *
     * @param field Das Spielfeld. Muss quadratisch sein.
     * @param depth Die maximale Suchtiefe.
     */
    FourInARow(final Player[][] field, final int depth) {
        this.currentField = field;
        this.depth = depth;
    }

    /**
     * Führt den menschlichen Zug aus. Es wird erwartet, dass die übergebenen
     * Koordinaten gültig sind und das bezeichnete Feld noch frei ist. Dies
     * wird nicht überprüft.
     *
     * @param row    Die Zeile, in der ein Stein platziert wird.
     * @param column Die Spalte, in der ein Stein platziert wird.
     * @return Das Ergebnis des Zugs.
     */
    Result humanMove(final int row, final int column) {

        //Zunächst wird hier der Spielzug des HUMAN auf dem Spielfeld gesetzt

        currentField[row][column] = HUMAN;

        //Anschließend wird geprüft zu welchem Ergebnis der Spielzug führt


        //prüfen ob Spielfeld voll
        if (hasHumanWon() == true) {
            return HUMAN_WON;
        }

        //prüfen ob der menschliche Spieler gewonnen hat
        else if (checkIfFull() == true) {
        return DRAW;


            //Wenn der menschliche Spieler keine vier in einer Reihe hat und
            //das Spielfeld noch freie Felder hat, geht das Spiel weiter
        } else {

            return CONTINUE;

        }

    }


    /**
     * Diese Methode bestimmt das Ergebnis eines Zugs des Computers.
     * Zunächst wird dafür die minimax-Methode aufgerufen um den bestmöglichen Zug für den Computer
     * zu bestimmen. Dieser Zug wird dann auf dem Spielfeld ausgeführt,
     * um auf dessen Grundlagen dann das Ergebnis des Zugs zu bestimmen.
     *
     * @return Das Ergebnis des Zugs.
     */
    Result computerMove() {

        //prüfe, ob das Spielfeld nach einem Zug des menschlichen Spielers voll ist
        if (checkIfFull() == true) {
            return DRAW;
        }

        //als Parameter an die minimax-Methode wird hier:
        //1.) bestimmt das der Computer am Zug ist
        //2.) die vorm Spielbeginn festgelegte maximale Suchtiefe
        //3.) die 'schlechtmöglichste' Bewertung eines Zugs des Computers
        //4.) die 'schlechtmöglichste' Bewertung eines Zugs des menschlichen Spielers
        Move besterZug = minimaxMethode(true, depth, Integer.MAX_VALUE, Integer.MIN_VALUE);

        //besten Zug machen, danach Spielfeld prüfen
        currentField[besterZug.getRow()][besterZug.getColumn()] = COMPUTER;

        //prüfen ob der Computer gewonnen hat
        if (hasComputerWon() == true) {
            return COMPUTER_WON;


        }
        //prüfe, ob das Spielfeld nach dem Computer-Zug voll ist
        else if (checkIfFull() == true) {
            return DRAW;
        }

        //Wenn der Computer keine vier in einer Reihe hat und
        //das Spielfeld noch freie Felder hat, geht das Spiel weiter
        else {
            return CONTINUE;
        }
    }

    /**
     * Diese Methode wird vom Computer aufgerufen um die maximale Gewinnchance für ihn zu
     * berechnen. Um dies zu tun wird jeder mögliche Spielzug bis zum Erreichen der zu Beginn
     * des Spiels festgelegten Tiefe (depth) bewertet. (Sollte das Spielfeld voll sein, wird die
     * Suche auch beendet). Dafür wird abwechselnd ein Zug simuliert. Je geringer die Bewertung,
     * desto besser ist der Zug für den Computer. Umgekehrt sind numerisch höher bewertete Züge, schlechter
     * für den Computer (und besser für den menschlichen Spieler). Damit werden alle möglichen Züge berechnet.
     * Es wird davon ausgegangen, dass der menschliche Spieler immer den bestmöglichen Zug macht
     * (genauer: einen der bestmöglichen Züge).
     * <p>
     * Erweiterung Alpha-Beta-Pruning:
     * Wir aktualisieren die Werte alpha und beta während der Suche des besten Zuges.
     * Die Werte geben an welcher Zug der jeweils Beste ist. Damit lässt sich dann bestimmen
     * welche Züge nicht mehr überprüft werden müssen, da sie das Ergebnis nicht beeinflussen können.
     * <p>
     * Abstrakt lässt sich das Prinzip schwer erklären. Am verständlichsten lässt sich die Verfahrensweise
     * anhand eines konkreten Suchbaumes mit dazugehörigen Werten, welcher die möglichen Spielzüge inklusive
     * Bewertungen abbildet, erklären. Da dies nicht Teil der Aufgabenstellung ist, verzichten wir an
     * dieser Stelle darauf.
     *
     * @param computerIstAmZug Gibt an welcher Spieler am Zug ist
     * @param suchTiefe        Gibt an wie Tief gesucht werden soll
     * @param alpha
     * @param beta
     * @return Den bestbewertesten Zug des jeweiligen Spielers
     */
    Move minimaxMethode(boolean computerIstAmZug, int suchTiefe, int alpha, int beta) {

        //falls die maximale Tiefe der Suche erreicht worden ist oder
        //das Spielfeld voll ist
        if (suchTiefe == 0 || checkIfFull() == true) {
            //dann soll der aktuelle Zug bewertet werden
            return new Move(bewerten());

        }

        //falls der Computer an der Reihe ist
        if (computerIstAmZug == true) {

            Move besterZugComputer = new Move(Integer.MAX_VALUE);

            for (int zeile = 0; zeile < currentField.length; zeile++) {

                for (int spalte = 0; spalte < currentField.length; spalte++) {
                    //alle Züge die noch möglich sind, sind mit EMPTY markiert
                    //nur die möglichen Züge sollen überprüft werden
                    if (currentField[zeile][spalte] == EMPTY) {

                        //die möglichen Züge werden iterativ simuliert
                        simuliereComputerZug(zeile, spalte);

                        //WENN: einer der simulierten Züge dazu führt, dass der Computer gewonnen hat
                        //DANN: ist die Bewertung des Zuges abhängig von der Anzahl der Schritte die
                        // zum Sieg benötigt wurden
                        //SONST: wird weiterhin nach einer Bewertung gesucht(bis die maximale Suchtiefe
                        // erreicht wird oder einer der Züge zu einem Sieg eines Spielers führt)
                        int bewertung = hasComputerWon()
                                ? computerSiegesZugBewertung(suchTiefe)
                                : minimaxMethode(false, suchTiefe - 1, alpha, beta).getScore();

                        //falls der derzeitige Zug besser(aus Sicht des Computers) ist
                        if (bewertung < besterZugComputer.getScore()) {
                            //DANN: setzte diesen Zug als Besten fest
                            besterZugComputer = new Move(zeile, spalte, bewertung);

                        }
                        //setze das Spielfeld zurück, um den nächsten Zug prüfen zu können
                        rueckgaengig(zeile, spalte);

                        //in alpha wird die Bewertung des (derzeitig) besten Zuges des Computers gespeichert
                        //jener Wert dient nun zum Vergleich mit dem Wert beta
                        alpha = Math.min(alpha, bewertung);
                        //WENN: andere Züge nicht mehr geprüft werden müssen
                        if (beta >= alpha) {

                            break;
                        }
                    }
                }
                //DANN: beide Schleifen abbrechen -> keine Züge mehr prüfen(Pruning)
                if (beta >= alpha) {
                    break;
                }
            }
            return besterZugComputer;
        }
        //falls der menschliche Spieler dran ist
        else {

            Move besterZugHuman = new Move(Integer.MIN_VALUE);

            for (int zeile = 0; zeile < currentField.length; zeile++) {

                for (int spalte = 0; spalte < currentField.length; spalte++) {
                    //alle Züge die noch möglich sind, sind mit EMPTY markiert
                    //nur die möglichen Züge sollen überprüft werden
                    if (currentField[zeile][spalte] == EMPTY) {

                        //die möglichen Züge werden iterativ simuliert
                        simuliereHumanZug(zeile, spalte);

                        //WENN: einer der simulierten Züge dazu führt, dass der Mensch gewonnen hat
                        //DANN: ist die Bewertung des Zuges abhängig von der Anzahl der Schritte die
                        // zum Sieg benötigt wurden
                        //SONST: wird weiterhin nach einer Bewertung gesucht(bis die maximale Suchtiefe
                        //erreicht wird oder einer der Züge zu einem Sieg eines Spielers führt)
                        int bewertung = hasHumanWon()
                                ? humanSiegesZugBewertung(suchTiefe)
                                : minimaxMethode(true, suchTiefe - 1, alpha, beta).getScore();

                        //falls der derzeitige Zug besser(aus Sicht des Human) ist
                        if (bewertung > besterZugHuman.getScore()) {
                            //DANN: setzte diesen Zug als Besten fest
                            besterZugHuman = new Move(zeile, spalte, bewertung);

                        }
                        //setze das Spielfeld zurück, um den nächsten Zug prüfen zu können
                        rueckgaengig(zeile, spalte);
                        //in beta wird die Bewertung des (derzeitig) besten Zuges des Menschen gespeichert
                        //jener Wert dient nun zum Vergleich mit dem Wert von alpha
                        beta = Math.max(beta, bewertung);
                        //WENN: andere Züge nicht mehr geprüft werden müssen
                        if (beta >= alpha) {
                            break;
                        }
                    }
                }
                //DANN: beide Schleifen abbrechen -> keine Züge mehr prüfen
                if (beta >= alpha) {
                    break;
                }
            }
            return besterZugHuman;
        }


    }

    /**
     * Diese Methode bewertet einen Siegeszug des Computers umgekehrt proportional zur Anzahl der
     * benötigten Schritte die bis zum Siegeszug gebraucht werden. Spielzüge des Computers sind, nach
     * dem hier angewendeten Bewertungsschema, besser je kleiner sie sind.
     * Diese Methode wird aufgerufen wenn der Computer während der
     * Simulation der Spielzüge vier in einer Reihe hat.
     *
     * @param suchTiefe Die Anzahl der Schritte bis zum Siegeszug des Computers
     * @return Die Bewertung des Siegeszug(je kleiner desto besser)
     */
    int computerSiegesZugBewertung(int suchTiefe) {
        //Die Klassenvariable depth, ist nur durch die für einen Integer annehmbaren Werte beschränkt
        //Auch wenn eine Suchtiefe und Spielfeldgröße = Integer.MAX_VALUE nicht sinnvoll ist (hohe Laufzeit),
        //ist dies jedoch die hier gegebene Beschränkung
        //Ausgangspunkt ist das bestmögliche Ergebnis für den Computer
        int bewertung = Integer.MIN_VALUE;

        //Die Differenz des Parameters suchtiefe und der Klassenvariable depth entspricht der Anzahl
        //der Schritte die benötigt wird, damit diese Methode aufgerufen wird
        //(/ein Zug zum Sieg des Computers führt /hasComputerWon == true ist)
        while (suchTiefe < depth) {
            //Die Bewertung wird schlechter
            bewertung++;
            //in Abhängigkeit von der Anzahl der benötigten Schritte die bis zum
            //Erreichen des Siegeszugs benötigt werden
            suchTiefe++;
        }

        return bewertung;
    }

    /**
     * Diese Methode bewertet einen Siegeszug des Computers umgekehrt proportional zur Anzahl der
     * benötigten Schritte die bis zum Siegeszug gebraucht werden. Spielzüge des Menschen sind, nach
     * dem hier angewendeten Bewertungsschema, besser je größer sie sind. Diese Methode wird aufgerufen
     * wenn der menschliche Spieler während der Simulation der Spielzüge vier in einer Reihe hat.
     *
     * @param suchTiefe Die Anzahl der Schritte bis zum Siegeszug des Computers
     * @return Die Bewertung des Siegeszug(je kleiner desto besser)
     */
    int humanSiegesZugBewertung(int suchTiefe) {

        //Die Klassenvariable depth, ist nur durch die für einen Integer annehmbaren Werte beschränkt
        //Auch wenn eine Suchtiefe und Spielfeldgröße = Integer.MAX_VALUE nicht sinnvoll ist (hohe Laufzeit),
        //ist dies jedoch die hier gegebene Beschränkung
        //Ausgangspunkt ist das bestmögliche Ergebnis für den menschlichen Spieler
        int bewertung = Integer.MAX_VALUE;

        //Die Differenz des Parameters suchtiefe und der Klassenvariable depth entspricht der Anzahl
        //der Schritte die benötigt wird, damit diese Methode aufgerufen wird
        //(/ein Zug zum Sieg des Menschen führt /hasHumanWon == true ist)
        while (suchTiefe < depth) {
            //Die Bewertung wird schlechter
            bewertung--;
            //in Abhängigkeit von der Anzahl der benötigten Schritte die bis zum
            //Erreichen des Siegeszugs benötigt werden
            suchTiefe++;
        }

        return bewertung;
    }


    /**
     * Diese Methode liefert 3 mögliche Bewertungen des derzeitigen Spielstandes.
     * 1.) Der Computer hat gewonnen (-1)
     * 2.) Der menschliche Spieler hat gewonnen (1)
     * 3.) keiner hat zu diesem Zeitpunkt gewonnen (0)
     * Durch diese Bewertung kann die Wahl für den bestmöglichen Zug des Computers getroffen werden.
     * Nur wenn der Fall eintritt, dass das Spielfeld voll ist oder die maximale Suchtiefe
     * erreicht wurde, ohne dass einer der Spieler während der Simulation der Spielzüge in minimax() gewonnen hat,
     * wird diese Methode aufgerufen. Alle Züge, welche vor dem Erreichen der maximalen Suchtiefe
     * zum Gewinn eines Spielers führen sind besser bewertet als die Züge welche hier bewertet werden.
     *
     * @return Die Bewertung des derzeitigen Spielstandes
     */
    int bewerten() {

        if (hasComputerWon() == true) {
            return -1;
        } else if (hasHumanWon() == true) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Diese Methode dient als Hilfsmethode für die minimax-Methode.
     * Hier wird ein Spielzug des menschlichen Spielers durchgeführt, auf dessen Grundlage dann entweder:
     * 1.) Weitere Spielzüge durchgeführt werden
     * 2.) Eine Bewertung des, durch den Zug entstandenen, Spielfeldes stattfinden kann
     *
     * @param zeile  Zeilenposition auf dem Spielfeld
     * @param spalte Spaltenposition auf dem Spielfeld
     */
    void simuliereHumanZug(int zeile, int spalte) {
        currentField[zeile][spalte] = HUMAN;
    }

    /**
     * Diese Methode dient als Hilfsmethode für die minimax-Methode.
     * Hier wird ein Spielzug des Computers durchgeführt, auf dessen Grundlage dann entweder:
     * 1.) Weitere Spielzüge durchgeführt werden
     * 2.) Eine Bewertung des, durch den Zug entstandenen, Spielfeldes stattfinden kann
     *
     * @param zeile  Zeilenposition auf dem Spielfeld
     * @param spalte Spaltenposition auf dem Spielfeld
     */
    void simuliereComputerZug(int zeile, int spalte) {
        currentField[zeile][spalte] = COMPUTER;
    }

    /**
     * Diese Methode dient als Hilfsmethode für die minimax-Methode.
     * Spielzüge die in den Methoden simuliereHumanZug und simuliereComputerZug simuliert wurden,
     * werden hier wieder rückgängig gemacht, indem die entsprechenden Felder wieder
     * auf den Wert EMPTY gesetzt werden.
     * So lassen sich alle möglichen Spielzüge zunächst simulieren, anschließend bewerten, den
     * am Besten bewertesten speichern, und anschließend hiermit die simulierten Spielzüge wieder
     * rückgängig machen, um mit dem Spiel am ursprünglichen Stand fortzufahren.
     *
     * @param zeile  Zeilenposition auf dem Spielfeld
     * @param spalte Spaltenposition auf dem Spielfeld
     */
    void rueckgaengig(int zeile, int spalte) {
        currentField[zeile][spalte] = EMPTY;
    }


    /**
     * Diese Methode nutzt die entsprechenden Hilfsmethoden um zu prüfen ob vier Steine
     * in einer Reihe auf dem Spielfeld liegen.
     *
     * @return Die Antowort auf die Frage, ob der menschliche Spieler gewonnen hat
     */
    boolean hasHumanWon() {

        //vier mögliche 'Richtungen' prüfen
        //falls der menschliche Spieler vier in einer Reihe hat gewinnt dieser
        return checkVertical4win(HUMAN) ||
                checkHorizontal4win(HUMAN) ||
                checkDiagonalLeftToRight4win(HUMAN) ||
                checkDiagonalRightToLeft4win(HUMAN);
    }

    /**
     * Diese Methode nutzt die entsprechenden Hilfsmethoden um zu prüfen ob vier HUMAN-Elemente
     * in einer Reihe auf dem Spielfeld liegen.
     *
     * @return Die Antowort auf die Frage, ob der menschliche Spieler gewonnen hat.
     */
    boolean hasComputerWon() {

        //vier mögliche 'Richtungen' prüfen
        //falls der Computer vier in einer Reihe hat gewinnt dieser
        return checkVertical4win(COMPUTER) ||
                checkHorizontal4win(COMPUTER) ||
                checkDiagonalLeftToRight4win(COMPUTER) ||
                checkDiagonalRightToLeft4win(COMPUTER);
    }

    /**
     * Prüft ob das Spielfeld voll ist
     *
     * @return Die Antwort auf die Frage, ob das Spielfeld voll ist.
     */
    boolean checkIfFull() {

        for (Player[] players : currentField) {
            for (Player p : players
            ) {
                //falls noch ein Feld frei ist, ist das Spielfeld nicht voll
                if (p == EMPTY)
                    return false;
            }
        }
        return true;

    }

    /**
     * Diese Methode prüft ob eine Reihe von vier gleichen Player-Elementen
     * auf der Diagonalen von rechts nach links exisitiert.
     *
     * @return Existiert eine Reihe von vier gleichen Player-Elementen(diagonal, von rechts nach links) ?
     */
    boolean checkDiagonalRightToLeft4win(Player player) {

        //gesucht wird bis zur Zeile, bis zu der es noch möglich ist 4 in einer Reihe zu bekommen
        for (int row = 0; row < currentField.length - 3; row++) {
            //gestartet wird bei Spalte 3,
            //da offensichtlich keine 4 in einer Reihe außerhalb des Spielfeldes liegen
            for (int column = 3; column < currentField.length; column++) {
                //falls es ein Feld gibt das dem übergebenen Player-Element entspricht
                if (currentField[row][column] == player) {
                    //dann prüfe ob die 3 Felder diagonal von dem Player-Element, jenem entsprechen
                    if (player == currentField[row + 1][column - 1] &&
                            player == currentField[row + 2][column - 2] &&
                            player == currentField[row + 3][column - 3]
                    ) {
                        return true;
                    }

                }
            }
        }
        return false;
    }

    /**
     * Diese Methode prüft ob eine Reihe von vier gleichen Player-Elementen
     * auf der Diagonalen von links nach rechts exisitiert.
     *
     * @return Existiert eine Reihe von vier gleichen Player-Elementen(diagonal, von links nach rechts) ?
     */
    boolean checkDiagonalLeftToRight4win(Player player) {

        //gesucht wird bis zur Zeile, bis zu der es noch möglich ist 4 in einer Reihe zu bekommen
        for (int row = 0; row < currentField.length - 3; row++) {

            for (int column = 0; column < currentField.length - 3; column++) {
                //falls es ein Feld gibt das dem übergebenen Player-Element entspricht
                if (currentField[row][column] == player) {
                    //dann prüfe ob die 3 Felder diagonal von dem Player-Element, jenem entsprechen
                    if (player == currentField[row + 1][column + 1] &&
                            player == currentField[row + 2][column + 2] &&
                            player == currentField[row + 3][column + 3]
                    ) {
                        return true;
                    }

                }
            }
        }
        return false;
    }

    /**
     * Diese Methode prüft ob eine Reihe von vier gleichen Player-Elementen
     * auf einer Horizontalen liegt.
     *
     * @return Existiert eine Reihe von vier gleichen Player-Elementen(horizontal) ?
     */
    boolean checkHorizontal4win(Player player) {

        for (int row = 0; row < currentField.length; row++) {
            //gesucht wird bis zur Spalte, bis zu der es noch möglich ist 4 in einer Zeile zu bekommen
            for (int column = 0; column < currentField.length - 3; column++) {

                //falls es ein Feld gibt das dem übergebenen Player-Element entspricht
                if (currentField[row][column] == player) {
                    //dann prüfe ob die 3 Felder daneben dem Player-Element entsprechen
                    if (player == currentField[row][column + 1] &&
                            player == currentField[row][column + 2] &&
                            player == currentField[row][column + 3]
                    ) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Diese Methode prüft ob eine Reihe von vier gleichen Player-Elementen
     * auf einer Vertikalen liegt.
     *
     * @return Existiert eine Reihe von vier gleichen Player-Elementen(vertikal) ?
     */
    boolean checkVertical4win(Player player) {

        //gesucht wird bis zur Zeile, bis zu der es noch möglich ist 4 in einer Reihe zu bekommen
        for (int row = 0; row < currentField.length - 3; row++) {
            for (int column = 0; column < currentField.length; column++) {

                //falls es ein Feld gibt das dem übergebenen Player-Element entspricht
                if (currentField[row][column] == player) {
                    //dann prüfe ob die 3 Felder darunter dem Player-Element entsprechen
                    if (player == currentField[row + 1][column] &&
                            player == currentField[row + 2][column] &&
                            player == currentField[row + 3][column]
                    ) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Die Darstellung des Spielfelds.
     *
     * @return Die Darstellung als mehrzeilige Zeichenkette.
     */
    @Override
    public String toString() {
        final StringBuilder string = new StringBuilder();
        String separator = "";
        for (final Player[] row : this.currentField) {
            string.append(separator);
            separator = "\n";
            for (final Player player : row) {
                string.append(player);
            }
        }
        return string.toString();
    }
}

