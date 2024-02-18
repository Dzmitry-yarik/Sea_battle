package dev.yarashevich.dzmitry;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Archivator {

    public void createArchive() {
        String sourceFile = "src\\main\\java\\org\\example\\listGame";
        String zipFile = "src\\main\\java\\org\\example\\listGame.zip";

        try {
            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);

            addToZipFile(sourceFile, zos);

            zos.close();
            fos.close();

            System.out.println("Архив успешно создан: " + zipFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // архивировать игру
    private void addToZipFile(String sourceFile, ZipOutputStream zos) throws IOException {
        FileInputStream fis = new FileInputStream(sourceFile);
        ZipEntry zipEntry = new ZipEntry(sourceFile);
        zos.putNextEntry(zipEntry);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) > 0) {
            zos.write(buffer, 0, length);
        }
        zos.closeEntry();
        fis.close();
    }
}
