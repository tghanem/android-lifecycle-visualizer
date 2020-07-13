package impl.graphics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Helper {
    public static int getMaxDepth(
            LifecycleNode node) {

        HashSet<LifecycleNode> visitedNodes =
                new HashSet<>();

        visitedNodes.add(node);

        return traverseGetMaxDepth(node, visitedNodes, 0);
    }

    public static int getMaxWidth(
            LifecycleNode node) {

        HashSet<LifecycleNode> countedNodes =
                new HashSet<>();

        List<LifecycleNode> firstLevelNodes =
                Arrays.asList(node);

        return traverseGetMaxNodeCount(firstLevelNodes, countedNodes);
    }

    private static int traverseGetMaxDepth(
            LifecycleNode node,
            HashSet<LifecycleNode> visitedNodes,
            int depth) {

        int maxDepth = depth;

        for (LifecycleNode child : node.getChildren()) {
            if (!visitedNodes.contains(child)) {
                visitedNodes.add(child);

                int childMaxDepth =
                        traverseGetMaxDepth(child, visitedNodes, depth + 1);

                if (childMaxDepth > maxDepth) {
                    maxDepth = childMaxDepth;
                }

                visitedNodes.remove(child);
            }
        }

        return maxDepth;
    }

    private static int traverseGetMaxNodeCount(
            List<LifecycleNode> nodes,
            HashSet<LifecycleNode> countedNodes) {

        List<LifecycleNode> nextLevelNodes =
                new ArrayList<>();

        int nodeCount = 0;

        for (LifecycleNode node : nodes) {
            if (!countedNodes.contains(node)) {
                for (LifecycleNode child : node.getChildren()) {
                    nodeCount++;
                    nextLevelNodes.add(child);
                }
                countedNodes.add(node);
            }
        }

        int nextLevelNodeCount =
                traverseGetMaxNodeCount(nextLevelNodes, countedNodes);

        return Math.max(nodeCount, nextLevelNodeCount);
    }
}
