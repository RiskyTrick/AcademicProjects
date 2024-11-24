import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class DirectoryServer {
    private static final int PORT = 5000;
    private static final Map<Integer, String> nodes = new ConcurrentHashMap<>();
    private static int leaderId = -1;
    private static final ExecutorService clientExecutor = Executors.newCachedThreadPool();
    private static ServerSocket serverSocket;

    public static void main(String[] args) throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Directory Server running on port " + PORT);

        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                clientExecutor.submit(() -> handleClient(clientSocket));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void handleClient(Socket socket) {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            String message = in.readUTF();

            if (message.startsWith("JOIN")) {
                int nodeId = Integer.parseInt(message.split(" ")[1]);
                String address = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
                nodes.put(nodeId, address);
                System.out.println("Node " + nodeId + " joined. Active nodes: " + nodes.keySet());
                
                if (leaderId == -1 || nodeId > leaderId) {
                    leaderId = nodeId;
                    notifyAllNodes("LEADER " + leaderId);
                    System.out.println("New leader elected: Node " + leaderId);
                }

                out.writeUTF("JOIN_ACK");
                out.flush();

            } else if (message.startsWith("LEAVE")) {
                int nodeId = Integer.parseInt(message.split(" ")[1]);
                nodes.remove(nodeId);
                System.out.println("Node " + nodeId + " left. Active nodes: " + nodes.keySet());
                
                if (nodeId == leaderId) {
                    initiateElection();
                }

            } else if (message.startsWith("HEARTBEAT")) {
                out.writeUTF("HEARTBEAT_ACK");
                out.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void notifyAllNodes(String msg) {
        nodes.keySet().forEach(nodeId -> {
            String[] address = nodes.get(nodeId).split(":");
            try (Socket socket = new Socket(address[0], Integer.parseInt(address[1]));
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
                out.writeUTF(msg);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static void initiateElection() {
        leaderId = nodes.keySet().stream().max(Integer::compareTo).orElse(-1);
        if (leaderId != -1) {
            notifyAllNodes("LEADER " + leaderId);
            System.out.println("New leader elected after failure: Node " + leaderId);
        }
    }

    public static void shutdownServer() {
        try {
            clientExecutor.shutdown();
            serverSocket.close();
            System.out.println("Server shutting down...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
