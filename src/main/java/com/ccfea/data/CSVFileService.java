package com.ccfea.data;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CSVFileService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CSVFileService.class);
    private final String csvFile;
    private final List<List<String>> dataBuffer;

    public CSVFileService(List<String> headers, String csvFile) throws IOException {
        this.dataBuffer = new CopyOnWriteArrayList<>();
        this.csvFile = csvFile;
        final Path tmpFilePath = getCSVFilePath();
        LOGGER.info("File path for {} {}", csvFile, tmpFilePath);

        if (!headers.isEmpty()) {
            try (
                    final BufferedWriter writer = Files.newBufferedWriter(
                            getCSVFilePath(),
                            StandardOpenOption.CREATE,
                            StandardOpenOption.TRUNCATE_EXISTING,
                            StandardOpenOption.WRITE);
                    final CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.EXCEL.builder().setHeader(headers.toArray(new String[0])).build());
            ) {
                csvPrinter.flush();
            }
        }
    }

    public void appendToFile(List<String> csvRowValues) {
        dataBuffer.add(csvRowValues);
    }

    public void saveToFile() {
        try (
                final BufferedWriter writer = Files.newBufferedWriter(getCSVFilePath(), StandardOpenOption.APPEND);
                final CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.EXCEL.builder().build());
        ) {
            for (List<String> dataRow: dataBuffer) {
                try {
                    csvPrinter.printRecord(dataRow);
                    dataBuffer.remove(dataRow);
                } catch (IOException exp) {
                    LOGGER.error("Error writing row", exp);
                }
            }
            csvPrinter.flush();
        } catch (IOException exp) {
            LOGGER.error("Error opening file", exp);
        }
    }

    public Path getCSVFilePath() {
        return Paths.get(System.getProperty("java.io.tmpdir"), this.csvFile);
    }
}