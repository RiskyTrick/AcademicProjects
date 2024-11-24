import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class UserStoreClient {
    private String serverAddress;
    private int serverPort;

    public UserStoreClient(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void sendRequest(String request) {
        try (Socket socket = new Socket(serverAddress, serverPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Sending request: " + request);
            out.println(request);
            String response = in.readLine();
            System.out.println("Response: " + response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter server address: ");
        String address = scanner.nextLine();

        System.out.print("Enter server port: ");
        int port = scanner.nextInt();
        scanner.nextLine();

        UserStoreClient client = new UserStoreClient(address, port);

        while (true) {
            System.out.println("Choose operation: [1] Add/Update User, [2] Read User, [3] Exit");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                System.out.print("Enter username: ");
                String username = scanner.nextLine();
                System.out.print("Enter SSN: ");
                String ssn = scanner.nextLine();
                client.sendRequest("ADD " + username + " " + ssn);
            } else if (choice == 2) {
                System.out.print("Enter username: ");
                String username = scanner.nextLine();
                client.sendRequest("READ " + username);
            } else if (choice == 3) {
                System.out.println("Exiting client.");
                break;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }
}
