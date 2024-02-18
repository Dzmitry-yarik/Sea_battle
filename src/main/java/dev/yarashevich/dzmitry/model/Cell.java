package dev.yarashevich.dzmitry.model;


public class Cell {

    private int x;
    private int y;
    private int id;             // равен идентификатору корабля, к которому принадлежит (по умолчанию 0)
    private boolean shot;       // указывает, была ли ячейка подстрелена противником или нет
    private boolean potential;  // указывает, является ячейка потенциальной для корабля или нет

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.id = 0;
        this.shot = false;
        this.potential = true;
    }

    //    Назначает ячейку кораблю при вызове
    public void addShip(int id) {
        this.id = id;
    }

    //    Обновляет значение potential в соответствии с указанным значением
    public void updatePotential(boolean value) {
        potential = value;
    }

    public boolean isPotential() {
        return potential;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void shot() {
        shot = true;
    }

    //    Возвращает, была ячейка подстреляна или нет
    public boolean isShot() {
        return shot;
    }

    public int getId() {
        return id;
    }
}