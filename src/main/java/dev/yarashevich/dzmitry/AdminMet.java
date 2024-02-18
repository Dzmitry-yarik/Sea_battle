package dev.yarashevich.dzmitry;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AdminMet {

    // удалить игру по №
    public void deleteGameByNumber(int gameNumber) {
        try {
            File file = new File("src\\main\\java\\org\\example\\listGame");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            List<String> lines = new ArrayList<>();
            String line;

            while ((line = reader.readLine()) != null) {
                if (!line.contains("Игра №" + gameNumber)) {
                    lines.add(line);
                }
            }
            reader.close();

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String updatedLine : lines) {
                writer.write(updatedLine + "\n");
            }
            writer.close();
            System.out.println("Строка с игрой №" + gameNumber + " успешно удалена.");
        } catch (IOException e) {
            System.err.println("Ошибка при обработке файла: " + e.getMessage());
        }
    }
}
