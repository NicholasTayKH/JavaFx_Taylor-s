package org.example.assignment;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileManager {

    private static final String BASE_PATH = "";

    // ✅ 通用读取方法：支持 .jar 和开发环境
    public static List<String> readLines(String fileName) {
        List<String> lines = new ArrayList<>();

        // 尝试用资源流读取（兼容 .jar）
        try (InputStream is = FileManager.class.getClassLoader().getResourceAsStream("org/example/assignment/" + fileName)) {
            if (is != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        lines.add(line);
                    }
                }
                return lines;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 如果资源流找不到，尝试用文件系统路径（开发模式）
        Path path = Paths.get(BASE_PATH + fileName);
        if (Files.exists(path)) {
            try {
                lines = Files.readAllLines(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("❌ File not found: " + path.toAbsolutePath());
        }

        return lines;
    }

    // ✅ 写入（只适合开发模式）
    public static void writeLines(String fileName, List<String> lines) {
        try {
            Files.write(Paths.get(BASE_PATH + fileName), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ✅ 追加（只适合开发模式）
    public static void appendLine(String fileName, String line) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BASE_PATH + fileName, true))) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeLineFromFile(String fileName, java.util.function.Predicate<String> condition) {
        List<String> lines = readLines(fileName);
        lines.removeIf(condition);
        writeLines(fileName, lines);
    }
}


