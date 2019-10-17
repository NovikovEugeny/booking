package com.autoslava.booking.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;

import java.io.IOException;

public class FileUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T getObjectFromFile(String file, Class<T> type) throws IOException {
        String json = FileUtils.readFileToString(FileUtils.toFile(FileUtil.class.getClassLoader().getResource(file)), "utf-8");
        return mapper.readValue(json, type);
    }
}
