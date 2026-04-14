package cs.vsu.radiomanagerapibysolid.service.impl;

import cs.vsu.radiomanagerapibysolid.service.inter.DataTableService;
import cs.vsu.radiomanagerapibysolid.util.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class ExcelServiceImpl implements DataTableService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelServiceImpl.class);

    private final String excelFilesDir;

    private static final DateTimeFormatter XLSX_DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss",
            Locale.of("ru","RU"));

    public ExcelServiceImpl(@Value("${files.table.directory}") String excelFilesDir) {
        this.excelFilesDir = excelFilesDir;
        createDirectory(Path.of(excelFilesDir));
    }

    private void createDirectory(Path directory) {
        FileUtils.createDirectory(directory);
    }

    public String saveFile(MultipartFile file, Long fileId) {
        return FileUtils.saveFile(file, Path.of(excelFilesDir), fileId);
    }

    public byte[] getFile(String filename) {
        return FileUtils.getFile(filename, Path.of(excelFilesDir));
    }

    public boolean deleteFile(String filename) {
        return FileUtils.deleteAudio(filename, Path.of(excelFilesDir));
    }

    public List<Pair<LocalDateTime, LocalDateTime>> getTimeFromTable(MultipartFile file) {
        try {
            LOGGER.info("Starting to read Excel file for time intervals");

            List<Pair<LocalDateTime, LocalDateTime>> timeList = new ArrayList<>();
            DataFormatter dataFormatter = new DataFormatter();
            try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
                Sheet sheet = workbook.getSheetAt(0);
                LOGGER.debug("Opened workbook and retrieved sheet: {}", sheet.getSheetName());

                int rowNum;
                for (Row row : sheet) {
                    if (row == null) {
                        LOGGER.debug("Row is null, skipping...");
                        continue;
                    }

                    rowNum = row.getRowNum();

                    Cell firstCell = row.getCell(0);
                    Cell secondCell = row.getCell(1);


                    if (firstCell == null || secondCell == null) {
                        LOGGER.warn("Row {} skipped because one or both cells are null", rowNum);
                        continue;
                    }

                    String startStr = dataFormatter.formatCellValue(firstCell).trim();
                    String endStr = dataFormatter.formatCellValue(secondCell).trim();

                    if (startStr.isEmpty() || endStr.isEmpty()) {
                        LOGGER.warn("Row {} skipped because one or both cell values are empty", rowNum);
                        continue;
                    }

                    LOGGER.debug("Row {}: Start string = '{}', End string = '{}'", rowNum, startStr, endStr);

                    try {
                        LocalDateTime startTime = LocalDateTime.parse(startStr, XLSX_DTF);
                        LocalDateTime endTime = LocalDateTime.parse(endStr, XLSX_DTF);

                        timeList.add(Pair.of(startTime, endTime));
                    } catch (Exception e) {
                        LOGGER.error("Row {}: Error parsing date/time values." +
                                " Start: '{}', End: '{}'. Error: {}", rowNum, startStr, endStr, e.getMessage());
                    }
                }
            }
            LOGGER.info("Finished reading Excel file. Total valid time intervals read: {}", timeList.size());
            return timeList;

        } catch (Exception e) {
            LOGGER.error("Error reading excel file: {}", e.getMessage());
            throw new RuntimeException("Error reading excel file", e);
        }
    }

}
