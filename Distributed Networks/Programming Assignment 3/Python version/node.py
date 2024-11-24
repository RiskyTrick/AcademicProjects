import socket
import threading
import time

class Node:
    def __init__(self, node_id, ip_address='127.0.0.1', port=5000):
        self.node_id = node_id
        self.ip_address = ip_address
        self.port = 5000 + node_id
        self.is_leader = False
        self.server_address = (ip_address, port)

    # Join the network by connecting to the directory server
    def join_network(self):
        try:
            print(f"Node {self.node_id}: Attempting to join network at {self.server_address}")
            with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
                s.connect(self.server_address)
                s.sendall(f"JOIN {self.node_id}".encode())
                response = s.recv(1024).decode()
                if response == "JOIN_ACK":
                    print(f"Node {self.node_id} has successfully joined the network.")
        except Exception as e:
            print(f"Node {self.node_id}: Error joining network: {e}")

    # Leave the network
    def leave_network(self):
        try:
            with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
                s.connect(self.server_address)
                s.sendall(f"LEAVE {self.node_id}".encode())
                print(f"Node {self.node_id} has left the network.")
        except Exception as e:
            print(f"Node {self.node_id}: Error leaving network: {e}")

    # Send heartbeat to the directory server
    def send_heartbeat(self):
        while True:
            time.sleep(5)
            try:
                with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
                    s.connect(self.server_address)
                    s.sendall(f"HEARTBEAT {self.node_id}".encode())
                    response = s.recv(1024).decode()
            except Exception as e:
                print(f"Node {self.node_id}: Error sending heartbeat: {e}")

    # Receive messages from the directory server
    def receive_messages(self):
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.bind((self.ip_address, self.port))
            s.listen(5)
            while True:
                client_socket, client_address = s.accept()
                message = client_socket.recv(1024).decode()
                print(f"Node {self.node_id}: Received message: {message}")
                
                if message.startswith("LEADER"):
                    new_leader = int(message.split()[1])
                    print(f"Node {self.node_id}: New leader elected: Node {new_leader}")
                    self.is_leader = (new_leader == self.node_id)

                elif message.startswith("New Node"):
                    print(f"Node {self.node_id}: {message}")

                elif message.startswith("ELECTION"):
                    self.handle_election(message.split()[1])

                client_socket.close()

    # Handle the election message
    def handle_election(self, sender_id):
        print(f"Node {self.node_id}: Handling election from Node {sender_id}")
        if sender_id < self.node_id:
            self.respond_to_election(sender_id)

    # Respond to an election
    def respond_to_election(self, sender_id):
        try:
            with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
                s.connect(self.server_address)
                s.sendall(f"OK {self.node_id}".encode())
                print(f"Node {self.node_id}: Responded with OK to Node {sender_id}")
        except Exception as e:
            print(f"Node {self.node_id}: Error responding to election: {e}")

    # Start the node processes
    def start(self):
        threading.Thread(target=self.send_heartbeat, daemon=True).start()
        threading.Thread(target=self.receive_messages, daemon=True).start()

if __name__ == "__main__":
    node_id = int(input("Enter Node ID: "))
    node = Node(node_id)
    node.join_network()
    node.start()

    leave = input(f"Do you want Node {node_id} to leave the network? (yes/no): ")
    if leave.lower() == 'yes':
        node.leave_network()
