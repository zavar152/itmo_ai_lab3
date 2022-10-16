package reader;

import algo.C45;
import algo.C45Tester;
import tree.DecisionTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        C45 c45 = new C45.C45Builder()
                .setCsvDataFile("C:\\Users\\yarus\\Рабочий стол\\DATA.csv", ';')
                .setRandomizer(strings -> {
                    List<String[]> returnList = new ArrayList<>(strings);
                    int n = (int) Math.sqrt(strings.size());
                    Collections.shuffle(returnList);
                    List<String[]> randomized = returnList.subList(0, n);
                    System.out.println("Random data:");
                    randomized.forEach(strings1 -> System.out.println(Arrays.toString(strings1)));
                    return randomized;
                })
                .build();

        DecisionTree decisionTree = c45.buildDecisionTree();
        C45Tester c45Tester = new C45Tester("C:\\Users\\yarus\\Рабочий стол\\DATA.csv", ';', decisionTree);
        c45Tester.test();
        //System.out.println(decisionTree.test(new String[]{"1", "2", "0", "1"}, "1"));
    }
}
