package api.helper;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileStreamingOutput implements StreamingOutput {

    final int BUFFER_SIZE = 1024;
    private final File file;

    public FileStreamingOutput(File file){
        this.file = file;
    }

    @Override
    public void write(OutputStream output) throws IOException, WebApplicationException {
        try(var inputStream = new FileInputStream(file)) {
            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            output.flush();
        }
    }
}