package com.smartpark.interfaces;

import java.util.List;

public interface ReportGenerator<T> {
    void generateReport(List<T> data, String filePath) throws Exception;
}