package impl.graphics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Helper {
    public static List<List<LifecycleNode>> getLevelNodes(
            LifecycleNode node) {

        HashSet<LifecycleNode> countedNodes =
                new HashSet<>();

        List<LifecycleNode> firstLevelNodes =
                Arrays.asList(node);

        List<List<LifecycleNode>> levelNodes =
                new ArrayList<>();

        levelNodes.add(firstLevelNodes);

        traverseGetLevelNodes(firstLevelNodes, countedNodes, levelNodes);

        return levelNodes;
    }

    private static void traverseGetLevelNodes(
            List<LifecycleNode> nodes,
            HashSet<LifecycleNode> countedNodes,
            List<List<LifecycleNode>> levelNodes) {

        List<LifecycleNode> nextLevelNodes =
                new ArrayList<>();

        for (LifecycleNode node : nodes) {
            if (!countedNodes.contains(node)) {
                if (node instanceof LifecycleHandlerNode) {
                    for (LifecycleNode child : ((LifecycleHandlerNode)node).getChildren()) {
                        nextLevelNodes.add(child);
                    }
                }
                countedNodes.add(node);
            }
        }

        if (nextLevelNodes.size() > 0) {
            levelNodes.add(nextLevelNodes);
            traverseGetLevelNodes(nextLevelNodes, countedNodes, levelNodes);
        }
    }
}
