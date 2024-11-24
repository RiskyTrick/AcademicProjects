public class DistributedSystemMain {
    public static void main(String[] args) throws Exception {
        DirectoryServer directoryServer = new DirectoryServer(1);

        // Initialize nodes
        Node node1 = new Node(1, true, directoryServer);
        Node node2 = new Node(2, false, directoryServer);
        Node node3 = new Node(3, false, directoryServer);
        Node node4 = new Node(4, false, directoryServer);

        // Start NodeServer threads for each node to ensure they are listening
        NodeServer server1 = new NodeServer(node1);
        NodeServer server2 = new NodeServer(node2);
        NodeServer server3 = new NodeServer(node3);
        NodeServer server4 = new NodeServer(node4);

        server1.start();
        server2.start();
        server3.start();
        server4.start();

        // Small delay to ensure servers are up and listening
        Thread.sleep(500);

        // Join nodes to the distributed system
        node1.join();
        node2.join();
        node3.join();
        node4.join();

        // Simulate leader election and node operations
        node2.initiateElection();
        node3.leave();
    }
}
