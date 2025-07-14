package uz.expense.service.impl;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import uz.expense.entity.Expense;
import uz.expense.exception.ExpenseBadException;
import uz.expense.service.FileService;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

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
        } catch (Exception e) {
            throw new ExpenseBadException("Not able to write to file", e.getMessage());
        }

    }

    @Override
    public String readFromFileById(Long id) {

        File file = new File("storage/files/weekly.txt");

        try (BufferedReader inputStream = new BufferedReader(new FileReader(file))) {
            if (!file.exists()) {
                throw new ExpenseBadException("File does not exist");
            }

            String line;

            // Read on byte at a time until the end of the file is reached
            while ((line = inputStream.readLine()) != null) {
                if(line.contains(id.toString())) {
                    Log.info("CSV read: " + line);
                    return line;
                }
            }

        } catch (Exception e) {
            Log.info("Not able to read file");
            throw new ExpenseBadException("Not able to read from file", e.getMessage());
        }

        return "No value found";
    }


    @Override
    public String readFromFile() {

        File file = new File("storage/files/weekly.txt");


        try {
            if (!file.exists()) {
                throw new ExpenseBadException("File does not exist");
            }
            byte[] bytes = Files.readAllBytes(file.toPath());
            String csv = new String(bytes, StandardCharsets.UTF_8);
            Log.info("CSV read: " + csv);

            return csv;

        } catch (IOException e) {
            throw new ExpenseBadException("Not able to read from file", e.getMessage());
        }
    }

}
