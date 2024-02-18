package dev.yarashevich.dzmitry.model;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public abstract class Player {
    protected String playerName;
    protected Board gameBoard = new Board();
    protected BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private int statNbTotalShot;
    private int statNbSuccessfullyShot;
    private int statNbShipShot;
    private Cell lastCellShot = new Cell(-1, -1);

    protected abstract void placeShips();

    protected abstract String shot(Player enemy);

    protected abstract void setPlayerName();

    //    Увеличивает количество сделанных выстрелов игроком
    protected void incrementStatNbTotalShot() {
        statNbTotalShot++;
    }

    //    Увеличивает количество успешных выстрелов игрока
    protected void incrementStatNbSuccessfullyShot() {
        statNbSuccessfullyShot++;
    }

    //    Увеличивает количество потопленных кораблей игроком
    protected void incrementStatNbShipShot() {
        statNbShipShot++;
    }

    protected Board getGameBoard() {
        return gameBoard;
    }

    protected String getPlayerName() {
        return playerName;
    }

    //    Показывает статистику игрока
    protected void displayPlayerStats() {
        System.out.println();
        System.out.println("Статистика " + playerName + " :");
        System.out.println();
        System.out.println("Количество сделанных выстрелов : " + statNbTotalShot);
        System.out.println("Количество попаданий : " + statNbSuccessfullyShot);
        System.out.println("Точность : " + (double) Math.round(((double) (statNbSuccessfullyShot) / (double) (statNbTotalShot)) * 100.0) + "%");
        System.out.println("Количество потопленных кораблей : " + statNbShipShot + "/" + Config.getNbShips());
    }

    //    Получает последний сделанный выстрел игрока
    protected Cell getLastCellShot() {
        return lastCellShot;
    }

    //    Сохраняет последний сделанный выстрел игрока
    protected void setLastCellShot(int x, int y) {
        lastCellShot = new Cell(x, y);
    }
}