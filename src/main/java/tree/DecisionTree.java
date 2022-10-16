package tree;

import java.util.HashMap;
import java.util.Optional;

public final class DecisionTree {
    private final Node root;
    private final HashMap<String, Integer> headerOrder;
    
    public DecisionTree(Node root, String[] header) {
        this.root = root;
        this.headerOrder = new HashMap<>();
        for (int i = 1; i < header.length - 1; i++) {
            headerOrder.put(header[i], i - 1);
        }
    }

    public Node getRoot() {
        return root;
    }

    public Optional<Boolean> test(String[] testData, String expected) {
        try {
            return Optional.of(getClassForValues(testData).getTag().equals(expected));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public Node getClassForValues(String[] testData) {
        return getClassForValues0(root, testData);
    }

    private Node getClassForValues0(Node root, String[] testData) {
        if(root == null)
            throw new IllegalArgumentException("Can't complete test, check your parameters, this request is out of scope");
        if(!root.isLeaf()) {
            return getClassForValues0(root.getAttachedNode(testData[headerOrder.get(root.getTag())]), testData);
        } else {
            return root;
        }
    }
}
