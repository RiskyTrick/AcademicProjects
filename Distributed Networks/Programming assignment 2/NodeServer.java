import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class NodeServer extends Thread {
    private Node node;
    private ServerSocket serverSocket;
    private String ipAddress;

    public NodeServer(Node node, int port, String ipAddress) throws IOException {
        this.node = node;
        this.ipAddress = ipAddress;
        this.serverSocket = new ServerSocket(port, 50, InetAddress.getByName(ipAddress));
    }

    @Override
    public void run() {
        System.out.println("Node " + node.getNodeId() + " listening on " + ipAddress + ":" + serverSocket.getLocalPort());

        while (true) {
            try (Socket clientSocket = serverSocket.accept()) {
                handleRequest(clientSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
                if (node.isPrimary()) {
                    if (node.hasToken()) {
                        node.addUser(username, ssn);
                        out.println("User added/updated successfully");
                        System.out.println("User added/updated successfully on primary node " + node.getNodeId());
                        node.replicateToBackups(username, ssn);
                    } else {
                        System.out.println("Primary node " + node.getNodeId() + " requesting token for write operation");
                        node.requestToken();
                        out.println("Token requested, try again");
                    }
                } else {
                    System.out.println("Forwarding write request to primary node");
                    node.forwardToPrimary("ADD " + username + " " + ssn, out);
                }
            } else if ("READ".equalsIgnoreCase(command) && username != null) {
                String result = node.getUserStore().getOrDefault(username, "User not found");
                out.println("SSN: " + result);
                System.out.println("Read operation for " + username + " completed with result: " + result);
            } else {
                out.println("Invalid command");
                System.out.println("Invalid command received: " + request);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
