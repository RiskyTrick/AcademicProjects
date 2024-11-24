import socket
import threading
import time

PORT = 5000
nodes = {}
leader_id = -1
election_in_progress = False

# Broadcast message to all nodes
def broadcast(msg):
    for node_id, node_address in nodes.items():
        try:
            with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
                s.connect((node_address[0], node_address[1]))
                s.sendall(msg.encode())
                print(f"Directory Server: Broadcasted to Node {node_id}: {msg}")
        except Exception as e:
            print(f"Failed to send message to {node_id}: {e}")

# Handle incoming client connections (nodes)
def handle_client(client_socket, client_address):
    global leader_id, election_in_progress
    try:
        msg = client_socket.recv(1024).decode()
        print(f"Directory Server: Received from {client_address}: {msg}")

        # Node Join
        if msg.startswith("JOIN"):
            node_id = int(msg.split()[1])
            nodes[node_id] = client_address
            print(f"Directory Server: Node {node_id} joined. Active nodes: {list(nodes.keys())}")
            broadcast(f"New Node {node_id} has joined the network.")
            if leader_id == -1 or node_id > leader_id:
                leader_id = node_id
                broadcast(f"LEADER {leader_id}")
                print(f"Directory Server: New leader elected: Node {leader_id}")
            client_socket.send("JOIN_ACK".encode())

        # Node Leave
        elif msg.startswith("LEAVE"):
            node_id = int(msg.split()[1])
            if node_id in nodes:
                del nodes[node_id]
                print(f"Directory Server: Node {node_id} left. Active nodes: {list(nodes.keys())}")
                broadcast(f"Node {node_id} has left the network.")
                if node_id == leader_id:
                    initiate_election()

        # Heartbeat from Node
        elif msg.startswith("HEARTBEAT"):
            node_id = int(msg.split()[1])
            print(f"Directory Server: Heartbeat received from Node {node_id}")
            client_socket.send("HEARTBEAT_ACK".encode())

        # Election Process Triggered
        elif msg.startswith("ELECTION"):
            node_id = int(msg.split()[1])
            print(f"Directory Server: Election message received from Node {node_id}")
            if not election_in_progress:
                election_in_progress = True
                initiate_election()

    except Exception as e:
        print(f"Error with client {client_address}: {e}")
    finally:
        client_socket.close()

# Listen for client connections (nodes)
def listen_for_clients():
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.bind(("", PORT))
    server_socket.listen(5)
    print(f"Directory Server running on port {PORT}")

    while True:
        client_socket, client_address = server_socket.accept()
        print(f"Directory Server: New connection from {client_address}")
        threading.Thread(target=handle_client, args=(client_socket, client_address), daemon=True).start()

# Initiate Leader Election (Bully Algorithm)
def initiate_election():
    global leader_id, election_in_progress
    print("Directory Server: Leader election triggered...")
    if nodes:
        election_in_progress = True
        higher_nodes = [node_id for node_id in nodes.keys() if node_id > leader_id]
        if higher_nodes:
            for node in higher_nodes:
                send_election_message(node)
        else:
            leader_id = max(nodes.keys())
            broadcast(f"LEADER {leader_id}")
            print(f"Directory Server: New leader elected: Node {leader_id}")
            election_in_progress = False

# Send Election Message to Node
def send_election_message(node_id):
    try:
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.connect(nodes[node_id])
            s.sendall(f"ELECTION {leader_id}".encode())
            print(f"Directory Server: Sent ELECTION to Node {node_id}")
    except Exception as e:
        print(f"Failed to send ELECTION to Node {node_id}: {e}")

if __name__ == "__main__":
    listen_for_clients()
