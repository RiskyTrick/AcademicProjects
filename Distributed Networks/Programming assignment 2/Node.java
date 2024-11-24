import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class Node {
    private int nodeId;
    private boolean isPrimary;
    private boolean hasToken;
    private HashMap<String, String> userStore;
    private Queue<Integer> requestQueue;
    private int tokenHolder;

    public Node(int nodeId, boolean isPrimary) {
        this.nodeId = nodeId;
        this.isPrimary = isPrimary;
        this.hasToken = isPrimary; // Initial token holder is the primary node
        this.userStore = new HashMap<>();
        this.requestQueue = new LinkedList<>();
        this.tokenHolder = isPrimary ? nodeId : -1;
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

    public void addUser(String username, String ssn) {
        userStore.put(username, ssn);
        System.out.println("User " + username + " with SSN " + ssn + " added to node " + nodeId);
    }

    public HashMap<String, String> getUserStore() {
        return userStore;
    }

    public void requestToken() {
        if (!hasToken) {
            System.out.println("Node " + nodeId + " is requesting token from node " + tokenHolder);
            // Logic to send a token request to the current token holder
            // Here we would implement inter-node communication to request the token
        }
    }

    public void replicateToBackups(String username, String ssn) {
        System.out.println("Replicating user " + username + " with SSN " + ssn + " to backup nodes");
        // Logic to replicate the user data to backup nodes
    }

    public void forwardToPrimary(String command, PrintWriter out) {
        System.out.println("Forwarding command to primary: " + command);
        // Logic to forward the command to the primary node
    }

    public void receiveToken() {
        hasToken = true;
        tokenHolder = nodeId;
        System.out.println("Node " + nodeId + " received the token and can now perform write operations");
    }

    public void releaseToken() {
        hasToken = false;
        if (!requestQueue.isEmpty()) {
            int nextNode = requestQueue.poll();
            tokenHolder = nextNode;
            System.out.println("Token passed from node " + nodeId + " to node " + nextNode);
            // Logic to send the token to the next node in the queue
        }
    }
}
