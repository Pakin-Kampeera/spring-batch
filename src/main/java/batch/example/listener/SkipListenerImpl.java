package batch.example.listener;

import batch.example.model.StudentCsv;
import batch.example.model.StudentJson;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

@Component
public class SkipListenerImpl implements SkipListener<StudentCsv, StudentJson> {
    @Override
    public void onSkipInRead(Throwable throwable) {
        if (throwable instanceof FlatFileParseException) {
            createFile("Second Job/Second Chunk Step/read/SkipInRead.txt", ((FlatFileParseException) throwable).getInput());
        }
    }

    @Override
    public void onSkipInWrite(StudentJson studentJson, Throwable throwable) {
        createFile("Second Job/Second Chunk Step/write/SkipInWrite.txt", studentJson.toString());
    }

    @Override
    public void onSkipInProcess(StudentCsv studentCsv, Throwable throwable) {
        createFile("Second Job/Second Chunk Step/processor/SkipInProcess.txt", studentCsv.toString());
    }

    private void createFile(String filePath, String data) {
        try (FileWriter fileWriter = new FileWriter(filePath, true)) {
            fileWriter.write(data + ", " + new Date() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
