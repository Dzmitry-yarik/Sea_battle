package dev.yarashevich.dzmitry.model;

import dev.yarashevich.dzmitry.Help.Helper;

import java.io.PrintStream;

public class Board {
    private Cell[][] board = createEmptyBoard(16, 16);
    private Ship[] ships = new Ship[Config.getNbShips()];

    public Board() {
    }

    public Cell getCell(int x, int y) {
        return board[x][y];
    }

    //    Показывает личное поле
    public void showPersonalBoard() {
        System.out.println("                               Ваше поле");
        System.out.println();
        System.out.println("     1   2   3   4   5   6   7   8   9  10  11  12  13  14  15  16 ");

        for (int j = 0; j < board.length; ++j) {
            if (j == 0) {
                System.out.println("  +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+");
            }

            for (int i = 0; i < board[j].length; ++i) {
                if (i == 0) {
                    System.out.print(Helper.convertNumberToLetter(j) + " |");
                } else {
                    System.out.print("|");
                }

                if (board[i][j].getId() > 0) {
                    PrintStream pr = System.out;
                    pr.print(" # ");
                } else if (board[i][j].getId() == -1) {
                    System.out.print(" X ");
                } else {
                    System.out.print("   ");
                }

                if (i == board[j].length - 1) {
                    System.out.print("|");
                }
            }
            System.out.println();
            System.out.println("  +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+");
        }

        System.out.println();
    }

    // Показывает игровое поле обоих игроков
    public void showPlayBoard(Player player, Player enemy) {
        Helper.cleanConsole();
        Cell[][] enemyBoard = enemy.getGameBoard().getCells();
        System.out.print("                        Поле " + player.playerName);
        System.out.print("                                                           Поле " + enemy.getPlayerName());
        System.out.println(" ");
        System.out.println("    1   2   3   4   5   6   7   8   9  10  11  12  13  14  15  16      " +
                "      1   2   3   4   5   6   7   8   9  10  11  12  13  14  15  16");

        for (int j = 0; j < board.length; j++) {
            if (j == 0) {
                System.out.println("  +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+    " +
                        "    +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+");
            }

            // Мы проходим по столбцам первого поля
            for (int i = 0; i < board[j].length; i++) {
                if (i == 0) {
                    System.out.print(Helper.convertNumberToLetter(j) + " |");
                } else {
                    System.out.print("|");
                }

                if (board[i][j].getId() > 0 && board[i][j].isShot()) {
                    System.out.print(" X ");
                } else if (board[i][j].getId() == 0 && board[i][j].isShot()) {
                    System.out.print(" O ");
                } else if (board[i][j].getId() > 0 && !board[i][j].isShot()) {
                    System.out.print(" # ");
                } else {
                    System.out.print("   ");
                }

                if (i == board[j].length - 1) {
                    System.out.print("|");
                }
            }
            System.out.print("      ");

            // Мы проходим по столбцам второй сетки (сетка противника, на которой отмечены предыдущие выстрелы игроков)
            for (int i = 0; i < enemyBoard[j].length; i++) {
                if (i == 0) {
                    System.out.print(Helper.convertNumberToLetter(j) + " |");
                } else {
                    System.out.print("|");
                }

                if (enemyBoard[i][j].getId() > 0 && enemyBoard[i][j].isShot()) {
                    System.out.print(" X ");
                } else if (enemyBoard[i][j].getId() == 0 && enemyBoard[i][j].isShot()) {
                    System.out.print(" O ");
                } else {
                    System.out.print("   ");
                }

                if (i == enemyBoard[j].length - 1) {
                    System.out.print("|");
                }
            }
            System.out.println();
            System.out.println("  +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+    " +
                    "    +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+");
        }
    }

    //    Добавляет корабль в существующий массив кораблей
    public void addShip(Ship ship) {
        int ind = 0;
        while (ships[ind] != null) {
            ind++;
        }
        ships[ind] = ship;
        for (int i = 0; i < ship.getCells().length; i++) {
            board[ship.getCells()[i].getX()][ship.getCells()[i].getY()].addShip(ship.getId());
        }
    }

    //    Проверяет, имеет ли корабль "соседей"
    public boolean existsNeighbors(Cell[] cells) {

    // Берем в качестве параметра потенциальные ячейки, занятые кораблем. Смотрим на ячейки двух крайних точек, которые мы сохраняем в 4 переменных.
        int minX = cells[0].getX();
        int minY = cells[0].getY();
        int maxX = cells[cells.length - 1].getX();
        int maxY = cells[cells.length - 1].getY();

        int[][] cellsThatNeedTesting = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int[] c : cellsThatNeedTesting) {
                    int X = x + c[0];
                    int Y = y + c[1];

                    if (Helper.isValid(X, Y) && board[X][Y].getId() > 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //    Проверяет, не выходят ли (смоделированные) координаты корабля за пределы поля
    public boolean isInBoard(Cell[] cells) {
        for (Cell c : cells) {
            if (!Helper.isValid(c.getX(), c.getY())) {
                return false;
            }
        }
        return true;
    }

    //    Проверяет, перекрываются ли координаты корабля с координатами другого корабля
    public boolean existsOverlap(Cell[] cells) {
        for (Cell cell : cells) {
            for (int i = cell.getX() - 1; i <= cell.getX() + 1; i++) {
                for (int j = cell.getY() - 1; j <= cell.getY() + 1; j++) {
                    if (i >= 0 && i < 16 && j >= 0 && j < 16) {
                        if (board[i][j].getId() != 0) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    //    Принимает направление и позицию и генерирует список координат ячеек, к которым относится корабль
    public Cell[] generateShipCoordinates(int x, int y, String direction, int shipSize, int shipId) {
        Cell[] response = new Cell[shipSize];
        for (int i = 0; i < shipSize; i++) {
            if (direction.equals("H")) {
                response[i] = new Cell(x + i, y);
            } else if (direction.equals("V")) {
                response[i] = new Cell(x, y + i);
            }
            response[i].addShip(shipId);
        }
        return response;
    }

    //    Получает массив кораблей игрока
    public Ship[] getShips() {
        return ships;
    }

    //    Получает корабль игрока
    public Ship getShips(int shipId) {
        for (Ship ship : ships) {
            if (ship.getId() == shipId) {
                return ship;
            }
        }
        return ships[0];
    }

    //    Получает массив ячеек поля
    public Cell[][] getCells() {
        return board;
    }

    //    Заполняет поле пустыми ячейками
    private Cell[][] createEmptyBoard(int v, int h) {
        Cell[][] result = new Cell[v][h];
        for (int i = 0; i < v; i++) {
            for (int j = 0; j < h; j++) {
                result[i][j] = new Cell(i, j);
            }
        }
        return result;
    }
}