package com.medirisk.service;

import com.medirisk.model.HealthRecord;
import com.medirisk.storage.FileManager;
import com.medirisk.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Service managing uploaded health document metadata records.
 */
public class ReportService {

    private final FileManager fileManager;

    public ReportService(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    /**
     * Saves a health record metadata entry.
     */
    public HealthRecord createRecord(String userId, String title, String category, String filename, long sizeBytes) {
        String recordId = fileManager.generateNextId(FileManager.FILE_RECORDS, "R");
        String dateAdded = DateUtil.getCurrentDate();
        String timeAdded = DateUtil.getCurrentTime();

        // The HealthRecord constructor takes: id, patientId, fileName, filePath, date, time, fileType
        // Mapping sizeBytes string to fileType for now based on legacy code expectation.
        HealthRecord record = new HealthRecord(recordId, userId, title, filename, dateAdded, timeAdded, String.valueOf(sizeBytes));
        boolean saved = fileManager.appendLine(FileManager.FILE_RECORDS, record.toFileString());
        return saved ? record : null;
    }

    /**
     * Retrieves all health records.
     */
    public List<HealthRecord> getAllRecords() {
        List<String> lines = fileManager.readAllLines(FileManager.FILE_RECORDS);
        List<HealthRecord> list = new ArrayList<HealthRecord>();
        for (String line : lines) {
            HealthRecord record = HealthRecord.fromFileString(line);
            if (record != null) {
                list.add(record);
            }
        }
        return list;
    }

    /**
     * Deletes a health record metadata entry by ID.
     */
    public boolean deleteRecord(String recordId) {
        if (recordId == null) return false;
        return fileManager.deleteLineById(FileManager.FILE_RECORDS, recordId);
    }
}
