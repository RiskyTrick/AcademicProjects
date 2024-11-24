import java.io.*;
import java.net.*;

public class NodeServer extends Thread {
    private Node node;
    private ServerSocket serverSocket;

    public NodeServer(Node node) throws IOException {
        this.node = node;
        this.serverSocket = new ServerSocket(8000 + node.getId());
    }

    @Override
    public void run() {
        while (true) {
            try (Socket clientSocket = serverSocket.accept();
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                String message = in.readLine();
                processMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processMessage(String message) {
        if (message.startsWith("ELECTION")) {
            node.initiateElection();
        } else if (message.startsWith("NEW_LEADER")) {
            int leaderId = Integer.parseInt(message.split(" ")[1]);
            if (leaderId != node.getId()) node.leave();
        } else if (message.startsWith("HEARTBEAT")) {
            node.receiveHeartbeat();
        }
    }
}
