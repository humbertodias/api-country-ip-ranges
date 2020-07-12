package helper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DownloadHelper {

    private static File downloadDir = new File(System.getProperty("user.home"), "download");

    private static final int BUFFER_SIZE = 1024;

    public static String[] files() {
        return downloadDir.list();
    }

    public static File[] listFiles() {
        var list = downloadDir.listFiles();
        return list != null ? list : new File[0];
    }

    public static void downloadFiles(Set<String> filesURL) {
        downloadDir.mkdirs();
        for (var fileUrl : filesURL) {
            try {
                var file = downloadFile(fileUrl);
                System.out.printf("[OK] %s - %s bytes\n", file.getName(), file.length());
            } catch (IOException e) {
                System.out.printf("[ERROR] %s\n", e.getLocalizedMessage());
            }
        }
    }

    public static File downloadFile(String fileURL) throws IOException {

        var fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1);
        var file = new File(downloadDir, fileName);

        var in = new BufferedInputStream(new URL(fileURL).openStream());
        var fileOutputStream = new FileOutputStream(file);
        byte dataBuffer[] = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = in.read(dataBuffer, 0, BUFFER_SIZE)) != -1) {
            fileOutputStream.write(dataBuffer, 0, bytesRead);
        }
        return file;

    }

}