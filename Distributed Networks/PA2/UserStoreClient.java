import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class UserStoreClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter server IP address: ");
        String ip = scanner.nextLine();

        System.out.print("Enter server port: ");
        int port = Integer.parseInt(scanner.nextLine());

        try (Socket socket = new Socket(ip, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.print("Enter command (ADD <username> <ssn> or READ <username>): ");
            String command = scanner.nextLine();
            out.println(command);

            String response = in.readLine();
            System.out.println("Response from server: " + response);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
