public class DistributedUserStore {
    public static void main(String[] args) {
        try {
            // Create nodes
            Node primaryNode = new Node(1, true);
            Node backupNode1 = new Node(2, false);
            Node backupNode2 = new Node(3, false);
            Node backupNode3 = new Node(4, false);

            // Add neighbors to each node
            primaryNode.addNeighbor(backupNode1);
            primaryNode.addNeighbor(backupNode2);
            primaryNode.addNeighbor(backupNode3);

            backupNode1.addNeighbor(primaryNode);
            backupNode2.addNeighbor(primaryNode);
            backupNode3.addNeighbor(primaryNode);

            // Start the NodeServer instances for each node
            new NodeServer(primaryNode, 8080).start();
            new NodeServer(backupNode1, 8081).start();
            new NodeServer(backupNode2, 8082).start();
            new NodeServer(backupNode3, 8083).start();

            System.out.println("Distributed nodes are ready and awaiting requests...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
