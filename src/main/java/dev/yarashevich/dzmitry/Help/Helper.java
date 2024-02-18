package dev.yarashevich.dzmitry.Help;

public class Helper {

    //Очищает экран консоли
    public static void cleanConsole() {
        for (int i = 0; i < 5; i++) {
            System.out.println("\n\n\n\n\n");
        }
        try {
            String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else if (os.contains("Linux")) {
                new ProcessBuilder("bash", "-c", "clear").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("cmd", "/c", "clear").inheritIO().start().waitFor();
            }
        } catch (final Exception e) {
            System.out.println("Ошибка : " + e);
        }
    }

    //    Приостанавливает выполнение программы
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Преобразует "буквенную" координату в соответствующую "числовую"
    public static String convertLetterToNumber(String coordinate) {
        return String.valueOf("ABCDEFGHIJKLMNOP".indexOf(coordinate));
    }

    // Преобразует "числовую" координату в "буквенную" эквивалентную
    public static String convertNumberToLetter(int coordinate) {
        String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P"};
        return letters[coordinate];
    }

    // Проверяет, не выходит ли координата за пределы поля
    public static boolean isValid(int x, int y) {
        return (x >= 0 && x <= 15 && y >= 0 && y <= 15);
    }
}