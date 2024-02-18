package dev.yarashevich.dzmitry;

import dev.yarashevich.dzmitry.Help.Helper;
import dev.yarashevich.dzmitry.model.Config;
import dev.yarashevich.dzmitry.model.Game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class Menu {

    private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    public int selectOption() {
        String input = "";
        do {
            try {
                input = in.readLine();
            } catch (java.io.IOException e) {
                System.out.println("Произошла ошибка: " + e);
            }
        } while (!Pattern.matches("[0123]", input));
        return Integer.parseInt(input);
    }

    public void showMenu() {
        System.out.print("Выберите пункт меню: ");
        System.out.println();
        System.out.println(" 1. Начать игру \n 2. Правила игры");
        System.out.println();
    }

    public int setOpponent() {
        int input = 0;
        boolean isValidInput = false;
        do {
            System.out.println();
            System.out.print("Выберите оппонента: ");
            System.out.println();
            System.out.println(" 1. Игрок \n 2. Компьютер ");
            System.out.println();
            try {
                String userInput = in.readLine();
                try {
                    input = Integer.parseInt(userInput);
                    if (input != 1 && input != 2) {
                        System.out.println("Некорректные данные. Пожалуйста, выберите 1 или 2.");
                    } else {
                        isValidInput = true;
                    }
                } catch (NumberFormatException ex) {
                    System.out.println("Некорректные данные. Пожалуйста, выберите 1 или 2.");
                }
            } catch (IOException e) {
                System.out.println("Произошла ошибка: " + e);
            }
        } while (!isValidInput);
        return input;
    }

    public void showRules() {
        Helper.cleanConsole();
        System.out.println(" Морской бой: Правила ");
        System.out.println();
        System.out.println(" Вы сразитесь с противником в ожесточенном морском бою со своим флотом военных кораблей.");
        System.out.println();
        System.out.println(" У обоих будет флот, состоящий из " + Config.getNbShips() + " боевых кораблей:");
        for (int i = 0; i < Config.getShipsConfig().length; i++) {
            int shipSize = Integer.parseInt(Config.getShipsConfig()[i][2]);
            String plural = switch (shipSize) {
                case 1 -> "а";
                case 5, 6 -> "";
                default -> "и";
            };
            System.out.println(" - 1 " + Config.getShipsConfig()[i][1] + " (" + shipSize + " ячейк" + plural + ")");
        }
        System.out.println();
        System.out.println(" Ваша цель проста: уничтожить флот противника как можно быстрее, прежде чем он уничтожит ваш.");
        System.out.println();
        System.out.println(" Приготовьте свою стратегию и играйте в роли капитана морского флота прямо сейчас!");
        System.out.println();
        System.out.println(" 0. Вернуться в меню \n 1. Начать игру \n 2. Правила игры \n 3. Узнать больше ");
        System.out.println();
    }

    public void adminMenu() {
        boolean flag = true;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        while (flag) {
            int ans = 0;
            do {
                System.out.print("""
                        
                        Выберите пункт меню:
                        1. Показать список сыгранных игр
                        2. Удалить игру по №
                        3. Архивировать игру по №
                        4. Вернуться в меню
                        """);
                try {
                    ans = Integer.parseInt(in.readLine());
                    if (ans < 1 || ans > 4) {
                        System.out.println("Введены некорректные данные");
                    }
                } catch (IOException e) {
                    System.out.println("Вы ввели не число");
                }
            } while (ans < 1 || ans > 4);

            switch (ans) {
                case 1 -> {
                    new Game().printGameRecords();
                }
                case 2 -> {
                    try {
                        System.out.println("Введите нормер игры, которую хотите удалить");
                        int numbDel = Integer.parseInt(in.readLine());
                        new AdminMet().deleteGameByNumber(numbDel);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                case 3 -> {
                    new Archiver().createArchive();
                }
                case 4 -> {
                    showMenu();
                    flag = false;
                }
                default -> throw new IllegalStateException("Unexpected value: " + ans);
            }
        }
    }
}