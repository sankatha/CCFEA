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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CSVFileService {
    private static final int SAVE_SCHEDULE_SECONDS = 5;
    private static final int TIMEOUT_SECONDS = 10;
    private static final Logger LOGGER = LoggerFactory.getLogger(CSVFileService.class);
    private final String csvFile;
    private final List<List<String>> dataBuffer;
    private ScheduledExecutorService executorService;
    private static int timeoutElapsed = 0;

    public CSVFileService(List<String> headers, String csvFile) throws IOException {
        this.dataBuffer = new CopyOnWriteArrayList<>();
        this.csvFile = csvFile;
        final Path tmpFilePath = getTempCSVFilePath();
        LOGGER.info("File path for {} {}", csvFile, tmpFilePath);

        if (!headers.isEmpty()) {
            try (
                    final BufferedWriter writer = Files.newBufferedWriter(
                            getTempCSVFilePath(),
                            StandardOpenOption.CREATE,
                            StandardOpenOption.TRUNCATE_EXISTING,
                            StandardOpenOption.WRITE);
                    final CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.EXCEL.builder().setHeader(headers.toArray(new String[0])).build());
            ) {
                csvPrinter.flush();
            }
        }
    }

    public Path getTempCSVFilePath() {
        return Paths.get(System.getProperty("java.io.tmpdir"), this.csvFile);
    }

    public void appendToFile(List<String> csvRowValues) {
        dataBuffer.add(csvRowValues);
        fileSaveScheduler();
    }

    private int saveToFile() {
        final int dataBufferSize = dataBuffer.size();

        if (dataBufferSize > 0) {
            try (
                    final BufferedWriter writer = Files.newBufferedWriter(getTempCSVFilePath(), StandardOpenOption.APPEND);
                    final CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.EXCEL.builder().build());
            ) {
                for (List<String> dataRow : dataBuffer) {
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
        return dataBufferSize;
    }

    private void fileSaveScheduler() {
        if (executorService == null || executorService.isShutdown() || executorService.isTerminated()) {
            executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.scheduleAtFixedRate(() -> {
                final int processedRecords = saveToFile();
                if (processedRecords == 0) {
                    timeoutElapsed = timeoutElapsed + SAVE_SCHEDULE_SECONDS;
                }

                if (timeoutElapsed > TIMEOUT_SECONDS) {
                    executorService.shutdown();
                    timeoutElapsed = 0;
                    LOGGER.info("Temp data file persisted successfully for: {}", csvFile);
                }
            }, SAVE_SCHEDULE_SECONDS, SAVE_SCHEDULE_SECONDS, TimeUnit.SECONDS);
        }
    }
}