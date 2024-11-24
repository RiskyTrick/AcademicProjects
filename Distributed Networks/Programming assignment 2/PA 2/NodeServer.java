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
        System.out.println("Node Server initialized on " + ipAddress + ":" + port);
    }

    @Override
    public void run() {
        System.out.println("Node " + node.getNodeId() + " listening on " + ipAddress + ":" + serverSocket.getLocalPort());

        while (true) {
            try (Socket clientSocket = serverSocket.accept()) {
                System.out.println("Received a connection on Node " + node.getNodeId());
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

            if ("ADD".equalsIgnoreCase(command) && username != null && ssn != null) {
                if (node.isPrimary()) {
                    node.addUser(username, ssn);
                    out.println("User added/updated on primary successfully");
                } else {
                    node.forwardToPrimary(request, out);  // Pass PrintWriter to forward response
                }
            } else if ("READ".equalsIgnoreCase(command) && username != null) {
                String result = node.getUserStore().getOrDefault(username, "User not found");
                out.println("SSN: " + result);
            } else {
                out.println("Invalid command");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
