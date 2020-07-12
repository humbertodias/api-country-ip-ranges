package helper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class DownloadHelper {

    private static final int BUFFER_SIZE = 1024;

    public static void downloadFiles(File saveDir, String ... filesURL)  {
        for(var fileUrl : filesURL){
            try {
                var file = downloadFile(fileUrl, saveDir);
                System.out.printf("[OK] %s - %s bytes\n", file.getName(), file.length());
            } catch (IOException e) {
                System.out.printf("[ERROR] %s\n", e.getLocalizedMessage());
            }
        }
    }

    public static File downloadFile(String fileURL, File saveDir) throws IOException {

        var fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1);
        var file = new File(saveDir, fileName);

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