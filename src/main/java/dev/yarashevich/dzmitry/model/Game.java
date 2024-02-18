package dev.yarashevich.dzmitry.model;

import java.io.Serializable;

import dev.yarashevich.dzmitry.Help.Helper;
import dev.yarashevich.dzmitry.Menu;

import java.io.*;

public class Game implements Serializable {
    private Player player1;
    private Player player2;
    private int playerPlay;
    private String playerWinner;

    public void init() {
        player1 = new HumanPlayer();

        int opponentType = new Menu().setOpponent();

        if (opponentType == 1) {
            System.out.println("Вы будете играть против человека.");
            System.out.println();
            System.out.println("Введите данные второго игрока.");
            System.out.println();
            player2 = new HumanPlayer();
        } else {
            System.out.println("Вы будете играть против компьютера.");
            System.out.println();
            player2 = new ComputerPlayer();
        }
        System.out.println("Начинается размещение кораблей!");
        Helper.sleep(1500);

        player1.placeShips();
        player2.placeShips();

        setFirstPlayer();
    }

    public void play() {
        int gameNumber = 1; // Номер текущей игры
        StringBuilder gameRecord = new StringBuilder(); // Строка для записи ходов

        do {
            Helper.cleanConsole();
            if (playerPlay == 1) {
                if (player1 instanceof HumanPlayer) {
                    System.out.println(player1.getPlayerName() + ", ваш ход!");
                    Helper.sleep(1300);
                }
                String player1Input = player1.shot(player2);
                gameRecord.append("ход ").append(gameNumber).append(": ").append(player1.getPlayerName()).append(" выстрелил = ").append(player1Input).append(". ");
                playerWinner = player1.getPlayerName();
                playerPlay = 2;
            } else {
                if (player2 instanceof HumanPlayer) {
                    System.out.println(player2.getPlayerName() + ", ваш ход!");
                    Helper.sleep(1300);
                }
                String player2Input = player2.shot(player1);
                gameRecord.append("ход ").append(gameNumber).append(": ").append(player2.getPlayerName()).append(" выстрелил = ").append(player2Input).append(". ");
                playerWinner = player2.getPlayerName();
                playerPlay = 1;
            }
            gameNumber++;
        } while (!over());

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("src\\main\\java\\org\\example\\listGame", true));
            writer.write("Игра №" + gameNumber + ". Между: player1 name = " + player1.getPlayerName() +
                    ", player2 name = " + player2.getPlayerName() + "! " + gameRecord + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected boolean over() {
        boolean response1 = true;
        boolean response2 = true;
        for (int i = 0; i < player1.getGameBoard().getShips().length; i++) {
            if (!player1.getGameBoard().getShips()[i].isSunk()) {
                response1 = false;
                break;
            }
        }
        for (int i = 0; i < player2.getGameBoard().getShips().length; i++) {
            if (!player2.getGameBoard().getShips()[i].isSunk()) {
                response2 = false;
                break;
            }
        }
        return (response1 || response2);
    }

    //    Показывает имя победителя и статистику игры
    public void end() {
        Helper.cleanConsole();
        System.out.println(playerWinner + " выиграл игру!");
        player1.displayPlayerStats();
        System.out.println();
        player2.displayPlayerStats();
        System.out.println();
        System.out.println(" 0. Вернуться в меню \n 1. Играть снова \n 2. Правила игры \n 3. Узнать больше");
    }


    //    Устанавливает случайный порядок хода между двумя игроками
    private void setFirstPlayer() {
        playerPlay = (int) Math.round(Math.random() * 2 + 1);
    }

    //    Вывод всех игр в консоль
    public void printGameRecords() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("src\\main\\java\\org\\example\\listGame"));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}