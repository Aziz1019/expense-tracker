package uz.expense.service.impl;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import uz.expense.entity.Expense;
import uz.expense.exception.ExpenseBadException;
import uz.expense.service.FileService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
public class FileServiceImpl implements FileService {


    @Override
    public void writeToFile(Expense expense) {

        File file = new File("storage/files/weekly.txt");

        // In case folder does not exist
        boolean mkdirs = file.getParentFile().mkdirs();

        Log.info("Directories created: " + mkdirs);

        try (OutputStream outputStream = new FileOutputStream(file, true)) {
            outputStream.write((
                    expense.toCsv() + System.lineSeparator())
                    .getBytes(StandardCharsets.UTF_8));
        }

        catch (Exception e) {
            throw new ExpenseBadException("Not able to write to file", e.getMessage());
        }

    }

}
