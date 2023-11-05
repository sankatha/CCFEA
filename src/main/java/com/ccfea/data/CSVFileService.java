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

public class CSVFileService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CSVFileService.class);
    private final String csvFile;

    public CSVFileService(List<String> headers, String csvFile) throws IOException {
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
        try (
                final BufferedWriter writer = Files.newBufferedWriter(getCSVFilePath(), StandardOpenOption.APPEND);
                final CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.EXCEL.builder().build());
        ) {
            try {
                csvPrinter.printRecord(csvRowValues);
            } catch (IOException exp) {
                LOGGER.error("Error writing row", exp);
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