import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Node {
    private int nodeId;
    private boolean isPrimary;
    private boolean hasToken;
    private List<Node> neighbors;
    public Map<String, String> userStore;

    public Node(int nodeId, boolean isPrimary) {
        this.nodeId = nodeId;
        this.isPrimary = isPrimary;
        this.hasToken = isPrimary;
        this.neighbors = new ArrayList<>();
        this.userStore = new HashMap<>();
    }

    public int getNodeId() {
        return nodeId;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public boolean hasToken() {
        return hasToken;
    }

    public void addNeighbor(Node neighbor) {
        neighbors.add(neighbor);
    }

    public List<Node> getNeighbors() {
        return neighbors;
    }

    public void addUser(String username, String ssn) {
        userStore.put(username, ssn);
        System.out.println("Node" + nodeId + ": Updated user " + username + " with SSN " + ssn);
    }

    public Map<String, String> getUserStore() {
        return userStore;
    }

    public void replicateToBackups(String username, String ssn) {
        for (Node neighbor : neighbors) {
            if (!neighbor.isPrimary()) {
                System.out.println("Node" + neighbor.getNodeId() + ": Received replication for user " + username + " with SSN " + ssn);
                neighbor.addUser(username, ssn);
                System.out.println("Node" + nodeId + ": Replicated update for user " + username + " with SSN " + ssn + " to Node" + neighbor.getNodeId());
            }
        }
    }

    public void passToken(Node nextNode) {
        this.hasToken = false;
        nextNode.hasToken = true;
        System.out.println("Node" + this.nodeId + ": Passing token to Node" + nextNode.getNodeId());
        System.out.println("Node" + nextNode.getNodeId() + ": Received token.");
    }
}
