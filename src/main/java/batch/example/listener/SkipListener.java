package batch.example.listener;

import batch.example.model.StudentCsv;
import batch.example.model.StudentJson;
import org.springframework.batch.core.annotation.OnSkipInProcess;
import org.springframework.batch.core.annotation.OnSkipInRead;
import org.springframework.batch.core.annotation.OnSkipInWrite;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

@Component
public class SkipListener {
    @OnSkipInRead
    public void skipInRead(Throwable throwable) {
        if (throwable instanceof FlatFileParseException) {
            createFile("Second Job/Second Chunk Step/read/SkipInRead.txt", ((FlatFileParseException) throwable).getInput());
        }
    }

    private void createFile(String filePath, String data) {
        try (FileWriter fileWriter = new FileWriter(filePath, true)) {
            fileWriter.write(data + ", " + new Date() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnSkipInProcess
    public void skipInProgress(StudentCsv studentCsv, Throwable throwable) {
        createFile("Second Job/Second Chunk Step/processor/SkipInProcess.txt", studentCsv.toString());
    }

    @OnSkipInWrite
    public void skipInWrite(StudentJson studentJson, Throwable throwable) {
        createFile("Second Job/Second Chunk Step/write/SkipInWrite.txt", studentJson.toString());
    }
}
