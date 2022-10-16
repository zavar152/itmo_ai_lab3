package tree;

import java.util.HashMap;
import java.util.List;

public final class Node {
    private final String tag;
    private final boolean isLeaf;
    private final HashMap<String, Node> optionsMap = new HashMap<>();

    public Node(String tag, boolean isLeaf) {
        this.tag = tag;
        this.isLeaf = isLeaf;
    }

    public void attachNodeToOption(String option, Node attachment) {
        if(isLeaf)
            throw new UnsupportedOperationException("This node is leaf");
        optionsMap.put(option, attachment);
    }

    public Node getAttachedNode(String option) {
        if(isLeaf)
            throw new UnsupportedOperationException("This node is leaf");
        return optionsMap.get(option);
    }

    public String getTag() {
        return tag;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    @Override
    public String toString() {
        return "Node{" +
                "tag='" + tag + '\'' +
                ", isLeaf=" + isLeaf +
                ", optionsMap=" + optionsMap +
                '}';
    }
}
