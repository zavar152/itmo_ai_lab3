package algo;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.apache.commons.lang3.ArrayUtils;
import tree.DecisionTree;
import tree.Node;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public final class C45 {

    private final List<String[]> data;
    private final String[] header;
    private C45(List<String[]> data, String[] header) {
        this.data = data;
        this.header = header;
    }

    public DecisionTree buildDecisionTree() {
        Node root = build(data, header.length - 1, header);
        return new DecisionTree(root, header);
    }

    private Node build(List<String[]> data, int classIndex, String[] header) {
        double infoT = calcInfoT(data, classIndex);
        if(infoT == 0) {
            System.out.println("finish (infoT is 0)");
            return new Node(data.get(0)[classIndex], true);
        }
        HashMap<Integer, Double> infoTxMap = new HashMap<>();
        for (int i = 1; i < classIndex; i++)
            infoTxMap.put(i, calcInfoTx(data, i, classIndex));
        HashMap<Integer, Double> splitInfoXMap = new HashMap<>();
        for (int i = 1; i < classIndex; i++)
            splitInfoXMap.put(i, calcSplitInfoX(data, i));
        HashMap<Integer, Double> gainRatioXMap = new HashMap<>();
        for (int i = 1; i < classIndex; i++) {
            Double infoTx = infoTxMap.get(i);
            Double splitInfoX = splitInfoXMap.get(i);
            gainRatioXMap.put(i, (infoT - infoTx) / splitInfoX);
            System.out.println("gainRatioX of " + i + " is " + (infoT - infoTx) / splitInfoX);
        }
        Integer key = gainRatioXMap.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : entry1.getValue().equals(entry2.getValue()) ? 0 : -1).get().getKey();
        System.out.println(header[key] + " has max gain: " + gainRatioXMap.get(key));

        Node decisionNode = new Node(header[key], false);
        for (String par : calcFrequencies(data, key).keySet()) {
            System.out.println("For " + header[key] + "'s " + par + " value: ");
            List<String[]> collect = data.stream().filter(strings -> strings[key].equals(par)).toList();
            List<String[]> splittedCollect = new ArrayList<>(collect.size());
            collect.forEach(strings -> splittedCollect.add(ArrayUtils.remove(strings, key)));
            Node ret = build(splittedCollect, classIndex - 1, ArrayUtils.remove(header, key));
            decisionNode.attachNodeToOption(par, ret);
        }
        return decisionNode;
    }

    private Map<String, Integer> calcFrequencies(List<String[]> data, int index) {
        List<String> splitting = new ArrayList<>();
        data.forEach(strings -> splitting.add(strings[index]));
        return splitting.stream().collect(toMap(identity(), v -> 1, Integer::sum));
    }

    private double calcSplitInfoX(List<String[]> data, int index) {
        double splitInfoX = 0;
        Map<String, Integer> frequencies = calcFrequencies(data, index);
        int count = frequencies.values().stream().mapToInt(value -> value).sum();

        for (Integer freq : frequencies.values()) {
            double t = (double) freq / count;
            splitInfoX = splitInfoX + t * log2(t);
        }
        if (splitInfoX != 0)
            splitInfoX = -splitInfoX;
        System.out.println("splitInfoX of " + index + " is " + splitInfoX);
        return splitInfoX;
    }

    private double calcInfoTx(List<String[]> data, int index, int classIndex) {
        double infoTx = 0;
        Map<String, Integer> frequencies = calcFrequencies(data, index);
        int count = frequencies.values().stream().mapToInt(value -> value).sum();

        for (String s : frequencies.keySet()) {
            List<String[]> collect = data.stream().filter(strings -> strings[index].equals(s)).toList();
            infoTx = infoTx + ((double) frequencies.get(s) / count) * calcInfoT(collect, classIndex);
        }
        System.out.println("InfoTx of " + index + " is " + infoTx);
        return infoTx;
    }

    private double calcInfoT(List<String[]> data, int index) {
        double infoT = 0;
        Map<String, Integer> frequencies = calcFrequencies(data, index);
        int count = frequencies.values().stream().mapToInt(value -> value).sum();

        for (Integer freq : frequencies.values()) {
            double t = (double) freq / count;
            infoT = infoT + t * log2(t);
        }
        if (infoT != 0)
            infoT = -infoT;
        System.out.println("InfoT is: " + infoT);
        return infoT;
    }

    private double log2(double N) {
        return (Math.log(N) / Math.log(2));
    }

    public static class C45Builder {

        private String fileName;
        private char separator;
        private Function<List<String[]>, List<String[]>> randomizer;

        public C45Builder setCsvDataFile(String fileName, char separator) {
            this.fileName = fileName;
            this.separator = separator;
            return this;
        }

        public C45Builder setRandomizer(Function<List<String[]>, List<String[]>> randomizer) {
            this.randomizer = randomizer;
            return this;
        }

        public C45 build() {
            CSVParser csvParser = new CSVParserBuilder().withSeparator(separator).build();
            try (CSVReader reader = new CSVReaderBuilder(
                    new FileReader(fileName))
                    .withCSVParser(csvParser).build()) {
                List<String[]> data = reader.readAll();
                String[] header = data.get(0);
                data.remove(header);
                List<String[]> randomizedData = Collections.unmodifiableList(randomizer.apply(data));
                return new C45(randomizedData, header);
            } catch (IOException | CsvException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
