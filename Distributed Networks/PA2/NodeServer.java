import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class NodeServer extends Thread {
    private Node node;
    private int port;

    public NodeServer(Node node, int port) {
        this.node = node;
        this.port = port;
    }


    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port, 50, InetAddress.getByName("0.0.0.0"))) {
            System.out.println("Node" + node.getNodeId() + " is listening on all network interfaces, port: " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleRequest(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRequest(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String request = in.readLine();
            String[] parts = request.split(" ");
            String command = parts[0];
            String username = parts.length > 1 ? parts[1] : null;
            String ssn = parts.length > 2 ? parts[2] : null;

            System.out.println("Received request: " + request);

            if ("ADD".equalsIgnoreCase(command) && username != null && ssn != null) {
                if (node.hasToken()) {
                    System.out.println("Node" + node.getNodeId() + ": Holding token, performing write operation.");
                    node.addUser(username, ssn);
                    out.println("User added/updated successfully");
                    node.replicateToBackups(username, ssn);
                    System.out.println("Node" + node.getNodeId() + ": Releasing token after write operation.");
                    passTokenToNextNode();
                } else {
                    System.out.println("Node" + node.getNodeId() + ": Token already held by another node.");
                    out.println("Token requested, try again later.");
                }
            } else if ("READ".equalsIgnoreCase(command) && username != null) {
                String result = node.getUserStore().getOrDefault(username, "User not found");
                out.println("SSN: " + result);
                out.println(node.userStore.values());
            } else {
                out.println("Invalid command");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void passTokenToNextNode() {
        System.out.print("Enter the node ID to pass the token to (e.g., 2 for Node2): ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            int nextNodeId = Integer.parseInt(reader.readLine());
            Node nextNode = findNodeById(nextNodeId);

            if (nextNode != null) {
                node.passToken(nextNode);
            } else {
                System.out.println("Node with ID " + nextNodeId + " not found.");
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Invalid input, token not passed.");
        }
    }

    private Node findNodeById(int id) {
        for (Node neighbor : node.getNeighbors()) {
            if (neighbor.getNodeId() == id) {
                return neighbor;
            }
        }
        return null; // Node not found
    }
}
