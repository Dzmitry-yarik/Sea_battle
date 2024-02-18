package dev.yarashevich.dzmitry.model;

public class Ship {
    private int id;
    private String name;
    private Cell[] cells;

    public Ship(Cell[] cells, int id, String name) {
        this.name = name;
        this.cells = cells;
        this.id = id;
    }

    //    Проверяет, потоплен ли корабль
    public boolean isSunk() {
        for (Cell cell : cells) {
            if (!cell.isShot()) {
                return false;
            }
        }
        return true;
    }

    public Cell getCells(int cellPosition) {
        return cells[cellPosition];
    }

    //    Возвращает определенную ячейку
    public Cell getCells(int x, int y) {
        for (int i = 0; i < cells.length; i++) {
            if (cells[i].getX() == x && cells[i].getY() == y) {
                return cells[i];
            }
        }
        return cells[0];
    }

    public Cell[] getCells() {
        return cells;
    }

    public int getId() {
        return id;
    }

    public int getSize() {
        return cells.length;
    }

    public String getName() {
        return name;
    }
}