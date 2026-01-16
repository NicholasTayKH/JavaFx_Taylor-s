package Classes;

import java.io.*;
import java.util.*;

public class FAQDatabase {
    private static final String FILE_NAME = "faq.txt";

    public static List<FAQ> loadAllFAQs() {
        List<FAQ> faqs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String question = null;
            String answer = null;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Q:")) {
                    question = line.substring(2).trim();
                } else if (line.startsWith("A:")) {
                    answer = line.substring(2).trim();
                } else if (line.equals("---")) {
                    if (question != null && answer != null) {
                        faqs.add(new FAQ(question, answer));
                        question = null;
                        answer = null;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return faqs;
    }

    public static List<FAQ> searchFAQs(String query) {
        List<FAQ> results = new ArrayList<>();
        for (FAQ faq : loadAllFAQs()) {
            if (faq.getQuestion().toLowerCase().contains(query.toLowerCase()) ||
                    faq.getAnswer().toLowerCase().contains(query.toLowerCase())) {
                results.add(faq);
            }
        }
        return results;
    }

    public static void saveFAQ(FAQ newFaq) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write("Q: " + newFaq.getQuestion());
            writer.newLine();
            writer.write("A: " + newFaq.getAnswer()); // can be blank
            writer.newLine();
            writer.write("---");
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
