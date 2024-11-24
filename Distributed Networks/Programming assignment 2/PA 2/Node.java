import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class Node {
    private int nodeId;
    private boolean isPrimary;
    private boolean hasToken;
    private HashMap<String, String> userStore;
    private Queue<Integer> tokenRequestQueue;

    public Node(int nodeId, boolean isPrimary) {
        this.nodeId = nodeId;
        this.isPrimary = isPrimary;
        this.hasToken = isPrimary;
        this.userStore = new HashMap<>();
        this.tokenRequestQueue = new LinkedList<>();
        System.out.println("Node " + nodeId + " created as " + (isPrimary ? "Primary" : "Backup"));
    }

    public int getNodeId() {
        return nodeId;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public HashMap<String, String> getUserStore() {
        return userStore;
    }

    public synchronized void addUser(String username, String ssn) {
        if (hasToken) {
            userStore.put(username, ssn);
            System.out.println("Node " + nodeId + " added/updated user: " + username + " with SSN: " + ssn);
        } else {
            System.out.println("Node " + nodeId + " cannot write without token");
        }
    }

    public void requestToken(int requestingNodeId) {
        System.out.println("Node " + nodeId + " received token request from Node " + requestingNodeId);
        if (hasToken && !isPrimary) {
            transferToken(requestingNodeId);
        } else {
            tokenRequestQueue.add(requestingNodeId);
        }
    }

    public void transferToken(int newTokenHolder) {
        this.hasToken = false;
        System.out.println("Node " + nodeId + " transferring token to Node " + newTokenHolder);
        // Simulate token transfer here
    }

    public void forwardToPrimary(String request, PrintWriter out) {
        System.out.println("Node " + nodeId + " forwarding request to primary: " + request);
        // Logic to connect to the primary node and forward the request
        // For simplicity, we assume the primary node responds directly to `out` here.
        out.println("Request forwarded to primary and handled");
    }
}
