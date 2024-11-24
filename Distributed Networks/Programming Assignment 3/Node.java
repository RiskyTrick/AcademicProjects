import java.io.*;
import java.net.*;
import java.util.*;

public class Node {
    private int id;
    private boolean leader;
    private List<Node> groupList;
    private String ipAddress;
    private int port;
    private boolean active;

    public Node() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your node ID: ");
        this.id = scanner.nextInt();

        System.out.print("Enter the port number: ");
        this.port = scanner.nextInt();
        
        this.leader = false;
        this.groupList = new ArrayList<>();
        this.active = true;
        
        this.ipAddress = "127.0.0.1";  // Hardcoded for simplicity in this example.
    }

    public int getId() {
        return id;
    }

    public boolean isLeader() {
        return leader;
    }

    public void setLeader(boolean leader) {
        this.leader = leader;
    }

    public void addToGroup(Node node) {
        this.groupList.add(node);
    }

    public void removeFromGroup(Node node) {
        this.groupList.remove(node);
    }

    public List<Node> getGroupList() {
        return groupList;
    }

    public void join() throws IOException {
        System.out.println("Connecting to directory server at " + ipAddress + ":" + port);
        Socket socket = new Socket(ipAddress, port);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.writeUTF("JOIN " + this.id);
        out.flush();
        socket.close();
        System.out.println("Joined the network.");
    }

    public void leave() throws IOException {
        System.out.println("Notifying directory server of exit...");
        Socket socket = new Socket(ipAddress, port);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.writeUTF("LEAVE " + this.id);
        out.flush();
        socket.close();
        System.out.println("Left the network.");
    }

    public void notifyGroup(String action) throws IOException {
        for (Node node : groupList) {
            if (node.getId() != this.id) {
                Socket socket = new Socket(node.getIPAddress(), node.getPort());
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.writeUTF(action);
                out.writeObject(this);
                out.flush();
                socket.close();
            }
        }
    }

    public String getIPAddress() {
        return ipAddress; 
    }

    public int getPort() {
        return port; 
    }

    public void displayGroupList() {
        System.out.println("Current group: ");
        for (Node node : groupList) {
            System.out.println("Node ID: " + node.getId());
        }
    }

    // Heartbeat to check if the node is alive
    public void sendHeartbeat() {
        try {
            Socket socket = new Socket(ipAddress, port);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeUTF("HEARTBEAT");
            out.flush();
            socket.close();
        } catch (IOException e) {
            System.out.println("Failed to send heartbeat to server");
            e.printStackTrace();
        }
    }

    // Periodic heartbeat check
    public void startHeartbeat() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);  // Send heartbeat every 5 seconds
                    sendHeartbeat();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void main(String[] args) {
        try {
            Node node = new Node(); 
            node.join();
            node.startHeartbeat();  // Start sending heartbeats
            
            Scanner scanner = new Scanner(System.in);
            System.out.print("Press Enter to leave the network.");
            scanner.nextLine();
            node.leave(); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
