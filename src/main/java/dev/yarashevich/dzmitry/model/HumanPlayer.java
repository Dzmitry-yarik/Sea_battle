package dev.yarashevich.dzmitry.model;

import dev.yarashevich.dzmitry.Help.Helper;
import dev.yarashevich.dzmitry.Menu;

import java.io.IOException;
import java.util.regex.Pattern;

public class HumanPlayer extends Player {
    public HumanPlayer() {
        setPlayerName();
    }

//    Запрашивает у пользователя координаты для выстрела по вражеской клетке
    protected String shot(Player enemy) {
        boolean error = true;
        boolean continueShooting = true;
        int x = 0;
        int y = 0;
        String input = "";

        Helper.cleanConsole();
        gameBoard.showPlayBoard(this, enemy);

        while (error) {
            System.out.print(playerName + ", в какую клетку вы хотите выстрелить? ");
            try {
                input = in.readLine();
            } catch (java.io.IOException e) {
                System.out.println("Произошла ошибка: " + e);
            }
            if (Pattern.matches("[A-Pa-p][1-9][0-6]{0,1}$", input)) {
                x = Integer.parseInt(input.substring(1)) - 1;
                y = Integer.parseInt(Helper.convertLetterToNumber(input.substring(0, 1).toUpperCase()));
                if (Helper.isValid(x, y)) {
                    if (!enemy.getGameBoard().getCell(x, y).isShot()) {
                        error = false;
                    } else {
                        System.out.println("Вы уже стреляли в эту клетку.");
                    }
                } else {
                    System.out.println("Неверные координаты.");
                }
            }
        }

        Cell targetCell = enemy.getGameBoard().getCell(x, y);
        int cellValue = targetCell.getId();
        targetCell.shot();
        setLastCellShot(x, y);
        incrementStatNbTotalShot();
        if (cellValue > 0) {
            Ship shipHit = enemy.getGameBoard().getShips(cellValue);
            shipHit.getCells(x, y).shot();
            incrementStatNbSuccessfullyShot();
            gameBoard.showPlayBoard(this, enemy);
            if (shipHit.isSunk()) {
                incrementStatNbShipShot();
                System.out.println("Убил!");
            } else {
                System.out.println("Ранил!");
            }
            System.out.println("Стреляй снова!");
        } else {
            gameBoard.showPlayBoard(this, enemy);
            System.out.println("Мимо!");
            continueShooting = false;
        }

        Helper.sleep(1500);

        if (continueShooting) {
            shot(enemy);
        }
        return input;
    }

    //Работа с расстановкой караблей пользователем
    protected void placeShips() {
        Helper.cleanConsole();
        System.out.println("                         Создание флота ");
        System.out.println();
        System.out.println(playerName + ", вы должны разместить свои корабли на сетке.");
        System.out.println("Для этого вы должны указать ориентацию вашего корабля, а также его позицию развертывания.");
        System.out.println("Если вы вводите \"VC6\" для корабля из 3 клеток, то корабль будет развернут вертикально, начиная с C6 и заканчивая F6.");
        System.out.println("Если вы вводите \"HI3\" для корабля из 3 клеток, то корабль будет развернут горизонтально, начиная с I3 и заканчивая I6.");
        System.out.println("Будьте осторожны, чтобы не выйти за границы игрового поля, не перекрыть два корабля или не склеить два корабля, в таком случае вам придется снова разместить свой корабль.");
        System.out.println();
        System.out.println("Разместите их стратегически! Скоро начнется битва...");
        Helper.sleep(1500);

        for (int i = 0; i < Config.getNbShips(); i++) {
            boolean error = true;
            String input = "";
            int shipSize = Integer.parseInt(Config.getShipsConfig(i)[2]);
            gameBoard.showPersonalBoard();
            System.out.println(playerName + ", вам нужно разместить ваш " + Config.getShipsConfig(i)[1] + " (" + shipSize + " квадратов).");

            while (error) {
                System.out.print("Введите направление и позицию (например: VC6): ");
                try {
                    input = in.readLine();
                } catch (IOException var9) {
                    System.out.println("Произошла ошибка: " + var9);
                }
                if (Pattern.matches("^[HVhv][A-Pa-p][1-9][0-6]{0,1}$", input)) {
                    String direction = input.substring(0, 1).toUpperCase();
                    int x = Integer.parseInt(input.substring(2)) - 1;
                    int y = Integer.parseInt(Helper.convertLetterToNumber(input.substring(1, 2).toUpperCase()));
                    Cell[] shipCoordinates = gameBoard.generateShipCoordinates(x, y, direction, shipSize, Integer.parseInt(Config.getShipsConfig(i)[0]));
                    System.out.println();
                    if (x >= 0 && x <= 15) {
                        if (y >= 0 && y <= 15) {
                            if (!gameBoard.isInBoard(shipCoordinates)) {
                                System.out.println("Ваш корабль выходит за границы поля.");
                            } else if (gameBoard.existsOverlap(shipCoordinates)) {
                                System.out.println("Ваш корабль перекрывается с другим вашим кораблем.");
                            } else if (gameBoard.existsNeighbors(shipCoordinates)) {
                                System.out.println("Ваш корабль не должен быть примыкающим к другому вашему кораблю.");
                            } else {
                                gameBoard.addShip(new Ship(shipCoordinates, Integer.parseInt(Config.getShipsConfig(i)[0]), Config.getShipsConfig(i)[1]));
                                error = false;
                            }
                        } else {
                            System.out.println("Недопустимая координата Y.");
                        }
                    } else {
                        System.out.println("Недопустимая координата X.");
                    }
                }
            }
            if (i == Config.getNbShips() - 1) {
                Helper.cleanConsole();
                gameBoard.showPersonalBoard();
                System.out.println(playerName + ", вы разместили все свои корабли. Ваше поле выглядит следующим:");
                Helper.sleep(1500);
            }
        }
    }

    protected void setPlayerName() {
        String input = "";
        boolean error = true;
        do {
            System.out.print("Введите имя: ");

            try {
                input = in.readLine().replaceAll("\\s", "-");
            } catch (IOException var4) {
                System.out.println("Произошла ошибка: " + var4);
            }

            if (Pattern.matches("[A-ZА-Яa-zа-я]+", input) && input.length() >= 3 && input.length() <= 12) {
                error = false;
            }
        } while (error);

        if ("Admin".equals(input)) {
            new Menu().adminMenu();
        }
        playerName = input;
    }
}