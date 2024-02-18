package dev.yarashevich.dzmitry.model;

import dev.yarashevich.dzmitry.Help.Helper;

import java.util.Random;

public class ComputerPlayer extends Player {
    private Cell[] knownShipCells = new Cell[0];
    private Cell[] potentialCells = new Cell[0];

    public ComputerPlayer() {
        setPlayerName();
    }

    // Стреляет по вражескому полю
    protected String shot(Player enemy) {
        Helper.cleanConsole();
        System.out.println(playerName + " думает...");
        Helper.sleep(777);
        System.out.println();

        boolean error = true;
        boolean continueShooting = true;
        Cell targetCell;
        String input = "";

        while (error) {
            int[] coordinates = algorithm(enemy.getGameBoard());
            targetCell = enemy.getGameBoard().getCells()[coordinates[0]][coordinates[1]];
            input = coordinates[0] + String.valueOf(coordinates[1]);

            if (!targetCell.isShot() && targetCell.isPotential()) {
                if (targetCell.getId() > 0) {
                    enemy.getGameBoard().getShips(targetCell.getId()).getCells(targetCell.getX(), targetCell.getY())
                            .shot();
                    knownShipCells = addCellToArray(knownShipCells, targetCell);
                    potentialCells = updatePotentialCells(knownShipCells, enemy);
                    incrementStatNbSuccessfullyShot();
                    if (enemy.getGameBoard().getShips(targetCell.getId()).isSunk()) {
                        incrementStatNbShipShot();
                        potentialCells = updatePotentialCells(knownShipCells, enemy);
                        updateCellsStatus(potentialCells, enemy);
                        knownShipCells = new Cell[0];
                        potentialCells = new Cell[0];
                        System.out.println("Убил");
                        if (!new Game().over()) {
                            continueShooting = false;
                        }
                    } else {
                        System.out.println("Ранил");
                    }
                    System.out.println(playerName + " будет стрелять снова");
                    System.out.println();
                } else {
                    System.out.println("Мимо");
                    System.out.println();
                    continueShooting = false;
                }
                setLastCellShot(targetCell.getX(), targetCell.getY());
                targetCell.shot();
                error = false;
                incrementStatNbTotalShot();
            }
        }
        if (continueShooting) {
            shot(enemy);
        }

        return input;
    }

    // Добавляет корабли автоматически на сетку
    protected void placeShips() {
        Helper.cleanConsole();
        System.out.println(playerName + " размещает свои корабли...");
        for (int i = 0; i < Config.getNbShips(); i++) {
            Random rand = new Random();
            String direction;
            int x;
            int y;
            int shipSize = Integer.parseInt(Config.getShipsConfig(i)[2]);
            boolean error = true;
            do {
                int randomDirection = rand.nextInt(2);
                if (randomDirection == 1) {
                    direction = "H";
                    int xLimit = 16 - shipSize;
                    x = rand.nextInt(xLimit + 1);
                    y = rand.nextInt(17);
                } else {
                    direction = "V";
                    int yLimit = -shipSize + 16;
                    x = rand.nextInt(yLimit + 1);
                    y = rand.nextInt(17);
                }
                Cell[] shipCoordinates = gameBoard.generateShipCoordinates(x, y, direction, shipSize,
                        Integer.parseInt(Config.getShipsConfig(i)[0]));
                if (gameBoard.isInBoard(shipCoordinates) && !gameBoard.existsOverlap(shipCoordinates)
                        && !gameBoard.existsNeighbors(shipCoordinates)) {
                    gameBoard.addShip(new Ship(shipCoordinates, Integer.parseInt(Config.getShipsConfig(i)[0]),
                            Config.getShipsConfig(i)[1]));
                    error = false;
                }
            } while (error);
        }
        Helper.sleep(1500);
        System.out.println(playerName + " разместил все свои корабли.");
        Helper.sleep(700);
    }


    // Устанавливает имя компьютера
    protected void setPlayerName() {
        playerName = "Computer";
    }

    // Добавляет ячейку в массив ячеек
    private Cell[] addCellToArray(Cell[] array, Cell cell) {
        Cell[] result = new Cell[array.length + 1];
        System.arraycopy(array, 0, result, 0, result.length - 1);
        result[result.length - 1] = cell;
        return result;
    }

    //Обновляет все необходимые ячейки как непотенциальные
    private void updateCellsStatus(Cell[] array, Player player) {
        for (Cell c : array) {
            player.getGameBoard().getCell(c.getX(), c.getY()).updatePotential(false);
        }
    }


    //Определяет ячейки, которые могут быть частью корабля на основе известных ячеек корабля
    private Cell[] updatePotentialCells(Cell[] knownShipCells, Player player) {
        Cell[] result = new Cell[0];
        int[][] testingCoordinates = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
        System.out.println();
        for (Cell c : knownShipCells) {
            for (int[] coordinates : testingCoordinates) {
                if (Helper.isValid(c.getX() + coordinates[0], c.getY() + coordinates[1])) {
                    result = addCellToArray(result,
                            player.getGameBoard().getCell(c.getX() + coordinates[0], c.getY() + coordinates[1]));
                }
            }
        }
        return result;
    }

    //алгоритм для ходов бота
    private int[] algorithm(Board board) {
        int[][] simplifiedBoard = new int[16][16];

        // упрощаем поле до минимума, нам не нужны идентификаторы ячеек и другие данные
        for (int x = 0; x < simplifiedBoard.length; x++) {
            for (int y = 0; y < simplifiedBoard[0].length; y++) {
                if (!board.getCell(x, y).isShot()) {
                    simplifiedBoard[x][y] = 0; // если о ячейке ничего неизвестно, устанавливаем 0
                } else if (board.getCell(x, y).getId() == 0) {
                    simplifiedBoard[x][y] = 1; // если это промах, устанавливаем 1
                } else {
                    simplifiedBoard[x][y] = 2; // если это попадание, устанавливаем 2
                }
            }
        }

        // Если корабль потоплен, устанавливаем все окружающие ячейки в 1: там не может быть корабля.
        // Также вычисляем количество оставшихся кораблей
        int[][] testingCoordinates = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        int numberOfShipsLeft = 0;

        for (int i = 0; i < board.getShips().length; i++) {
            if (!board.getShips()[i].isSunk()) {
                numberOfShipsLeft++;
            } else {
                for (Cell c : board.getShips()[i].getCells()) {
                    for (int[] coordinates : testingCoordinates) {
                        if (Helper.isValid(c.getX() + coordinates[0], c.getY() + coordinates[1])) {
                            simplifiedBoard[c.getX() + coordinates[0]][c.getY() + coordinates[1]] = 1;
                        }
                    }
                }
            }
        }

        // если у нас есть две соседние ячейки, другой корабль не может касаться их, поэтому устанавливаем эти ячейки как 1
        // иначе корабль не может быть там
        Cell[] everyShotCells = new Cell[0];

        for (int x = 0; x < simplifiedBoard.length; x++) {
            for (int y = 0; y < simplifiedBoard[0].length; y++) {
                if (board.getCell(x, y).isShot() && board.getCell(x, y).getId() != 0) {
                    everyShotCells = addCellToArray(everyShotCells, board.getCell(x, y));
                }
            }
        }

        for (int i = 0; i < everyShotCells.length; i++) {
            for (int j = 0; j < everyShotCells.length; j++) {
                if (neighbors(everyShotCells[i], everyShotCells[j]) && j != i) {

                    boolean vertical = everyShotCells[i].getX() == everyShotCells[j].getX();

                    if (vertical) {
                        if (Helper.isValid(everyShotCells[i].getX() - 1, everyShotCells[i].getY())) {
                            simplifiedBoard[everyShotCells[i].getX() - 1][everyShotCells[i].getY()] = 1;
                            simplifiedBoard[everyShotCells[j].getX() - 1][everyShotCells[j].getY()] = 1;
                        }
                        if (Helper.isValid(everyShotCells[i].getX() + 1, everyShotCells[i].getY())) {
                            simplifiedBoard[everyShotCells[i].getX() + 1][everyShotCells[i].getY()] = 1;
                            simplifiedBoard[everyShotCells[j].getX() + 1][everyShotCells[j].getY()] = 1;
                        }
                    } else {
                        if (Helper.isValid(everyShotCells[i].getX(), everyShotCells[i].getY() - 1)) {
                            simplifiedBoard[everyShotCells[i].getX()][everyShotCells[i].getY() - 1] = 1;
                            simplifiedBoard[everyShotCells[j].getX()][everyShotCells[j].getY() - 1] = 1;
                        }
                        if (Helper.isValid(everyShotCells[i].getX(), everyShotCells[i].getY() + 1)) {
                            simplifiedBoard[everyShotCells[i].getX()][everyShotCells[i].getY() + 1] = 1;
                            simplifiedBoard[everyShotCells[j].getX()][everyShotCells[j].getY() + 1] = 1;
                        }
                    }
                }
            }
        }

        // регистрируем длину каждого оставшегося корабля
        int[] lengthOfShipsLeft = new int[numberOfShipsLeft];
        int ind = 0;
        for (int i = 0; i < board.getShips().length; i++) {
            if (!board.getShips()[i].isSunk()) {
                lengthOfShipsLeft[ind] = board.getShips()[i].getSize();
                ind++;
            }
        }

        // создаем поле, которое регистрирует количество раз, когда можно разместить корабль в данной ячейке
        int[][] probabilityBoard = new int[16][16];
        for (int x = 0; x < probabilityBoard.length; x++) {
            for (int y = 0; y < probabilityBoard[0].length; y++) {
                probabilityBoard[x][y] = 0;
            }
        }

        // заполняем поле
        for (int b = 0; b < numberOfShipsLeft; b++) { // для каждого оставшегося корабля
            for (int d = 0; d <= 1; d++) { // для каждого направления: горизонтального или вертикального
                for (int x = 0; x < probabilityBoard.length; x++) { // для каждой позиции x
                    for (int y = 0; y < probabilityBoard[0].length; y++) { // для каждой позиции y
                        boolean possible = true;
                        boolean containsNonSunkShip = false;
                        for (int i = 0; i < lengthOfShipsLeft[b]; i++) { // определяем, можно ли разместить корабль здесь
                            if (d == 0) {
                                if (probabilityBoard.length - x >= lengthOfShipsLeft[b]) {
                                    if (simplifiedBoard[x + i][y] == 1) {
                                        possible = false;
                                    } else if (simplifiedBoard[x + i][y] == 2) { // определяем, есть ли попадание в этой ячейке
                                        containsNonSunkShip = true;
                                    }
                                } else {
                                    possible = false;
                                }
                            } else { // то же самое для другого направления
                                if (probabilityBoard.length - y >= lengthOfShipsLeft[b]) {
                                    if (simplifiedBoard[x][y + i] == 1) {
                                        possible = false;
                                    } else if (simplifiedBoard[x][y + i] == 2) {
                                        containsNonSunkShip = true;
                                    }
                                } else {
                                    possible = false;
                                }
                            }
                        }
                        if (possible) { // если можно разместить корабль здесь
                            for (int i = 0; i < lengthOfShipsLeft[b]; i++) {
                                if (d == 0) {
                                    if (containsNonSunkShip) {
                                        probabilityBoard[x + i][y] += 15; // если есть попадание, увеличиваем вероятность
                                    } else {
                                        probabilityBoard[x + i][y]++; // в противном случае добавляем 1 к вероятности
                                    }
                                } else { // то же самое для другого направления
                                    if (containsNonSunkShip) {
                                        probabilityBoard[x][y + i] += 15;
                                    } else {
                                        probabilityBoard[x][y + i]++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // если ячейка подбита, мы не должны устанавливать ее в качестве целевой ячейки: для этого устанавливаем вероятность этой ячейки равной 0
        for (int x = 0; x < probabilityBoard.length; x++) {
            for (int y = 0; y < probabilityBoard[0].length; y++) {
                if (simplifiedBoard[x][y] == 2) {
                    probabilityBoard[x][y] = 0;
                }
            }
        }

        // регистрируем координаты ячейки с наибольшей вероятностью
        int finalX = -1;
        int finalY = -1;
        int maxProbability = 0;
        for (int x = 0; x < probabilityBoard.length; x++) {
            for (int y = 0; y < probabilityBoard[0].length; y++) {
                if (probabilityBoard[x][y] >= maxProbability) {
                    finalX = x;
                    finalY = y;
                    maxProbability = probabilityBoard[x][y];
                }
            }
        }

        return new int[]{finalX, finalY};
    }

    // Возвращает true, если две ячейки являются соседними
    private boolean neighbors(Cell cell1, Cell cell2) {
        int[][] neighborCells1 = {{cell1.getX(), 1 + cell1.getY()}, {cell1.getX(), -1 + cell1.getY()},
                {-1 + cell1.getX(), cell1.getY()}, {1 + cell1.getX(), cell1.getY()}};

        for (int[] coor1 : neighborCells1) {
            if (coor1[0] == cell2.getX() && coor1[1] == cell2.getY()) {
                return true;
            }
        }
        return false;
    }
}