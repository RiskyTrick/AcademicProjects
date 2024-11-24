import java.io.IOException;

public class DistributedUserStore {
    public static void main(String[] args) {
        try {
            String primaryNodeIP = "192.168.56.1";
            String backupNode1IP = "192.168.1.11";
            String backupNode2IP = "192.168.1.12";
            String backupNode3IP = "192.168.1.13";

            Node primaryNode = new Node("Node1", true);
            Node backupNode1 = new Node("Node2", false);
            Node backupNode2 = new Node("Node3", false);
            Node backupNode3 = new Node("Node4", false);

            primaryNode.addNeighbor(backupNode1);
            primaryNode.addNeighbor(backupNode2);
            primaryNode.addNeighbor(backupNode3);

            backupNode1.addNeighbor(primaryNode);
            backupNode2.addNeighbor(primaryNode);
            backupNode3.addNeighbor(primaryNode);

            new NodeServer(primaryNode, 8080, primaryNodeIP).start();
            new NodeServer(backupNode1, 8081, backupNode1IP).start();
            new NodeServer(backupNode2, 8082, backupNode2IP).start();
            new NodeServer(backupNode3, 8083, backupNode3IP).start();

            System.out.println("Distributed User Store is running...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
