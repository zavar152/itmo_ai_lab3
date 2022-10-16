package algo;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import tree.DecisionTree;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class C45Tester {
    private final DecisionTree decisionTree;
    private final List<String[]> data;

    public C45Tester(String fileName, char separator, DecisionTree decisionTree) {
        this.decisionTree = decisionTree;
        CSVParser csvParser = new CSVParserBuilder().withSeparator(separator).build();
        try (CSVReader reader = new CSVReaderBuilder(
                new FileReader(fileName))
                .withCSVParser(csvParser).build()) {
            List<String[]> fullData = reader.readAll();
            data = new ArrayList<>();
            String[] header = fullData.get(0);
            fullData.remove(header);
            fullData.forEach(strings -> data.add(ArrayUtils.remove(strings, 0)));
            data.forEach(strings -> System.out.println(Arrays.toString(strings)));
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }
    }

    public void test() {
        int tp = 0;
        int fp = 0;
        int fn = 0;
        int tn = 0;
        double accuracy;
        double precision;
        double recall;
        double tpr;
        double fpr;
        for (String[] strings : data) {
            String[] testData = ArrayUtils.remove(strings, strings.length - 1);
            String expected = strings[strings.length - 1];
            System.out.println("Test data: " + Arrays.toString(testData));
            System.out.println("Expected: " + expected);
            Optional<Boolean> test = decisionTree.test(testData, expected);
            if (test.isPresent()) {
                String result = test.get() ? "1" : "0";
                System.out.println("Test passed: " + result);
                if (expected.equals("1")) {
                    if (result.equals("1"))
                        tp++;
                    else
                        fn++;
                } else if (expected.equals("0")) {
                    if (result.equals("1"))
                        fp++;
                    else
                        tn++;
                }
            } else {
                System.out.println("Test failed!");
                fn++;
            }
            System.out.println();
        }
        accuracy = (double) (tp + tn) / (tp + tn + fp + fn);
        precision = (double) tp / (tp + fp);
        recall = (double) tp / (tp + fn);
        tpr = (double) tp / (tp + fn);
        fpr = (double) fp / (fp + tn);
        System.out.println("True Positive (TP): " + tp);
        System.out.println("False Negative (FN): " + fn);
        System.out.println("False Positive (FP): " + fp);
        System.out.println("True Negative (TN): " + tn);
        System.out.println("Accuracy: " + accuracy);
        System.out.println("Precision: " + precision);
        System.out.println("Recall: " + recall);
        System.out.println("TPR: " + tpr);
        System.out.println("FPR: " + fpr);
    }

}
