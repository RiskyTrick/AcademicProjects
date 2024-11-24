import requests

nodes = {
    1: "http://127.0.0.1:5001",
    2: "http://127.0.0.1:5002",
    3: "http://127.0.0.1:5003",
    4: "http://127.0.0.1:5004",
    5: "http://127.0.0.1:5005",
}

def write_operation(initiator_node, product_id, new_price):
    print(f"[INFO] Initiating write operation on Node {initiator_node} for Product {product_id} with price {new_price}")
    data = {"product_id": product_id, "new_price": new_price}
    ack_count = 0

    for node_id, address in nodes.items():
        try:
            print(f"[INFO] Sending write request to Node {node_id}...")
            response = requests.post(f"{address}/write", json=data)
            if response.status_code == 200:
                print(f"[SUCCESS] Node {node_id} acknowledged the write.")
                ack_count += 1
        except requests.exceptions.ConnectionError:
            print(f"[ERROR] Node {node_id} is unreachable.")

    if ack_count >= 3:
        print(f"[SUCCESS] Write operation successful! Product {product_id} updated to price {new_price}.")
    else:
        print("[FAILURE] Write operation failed. Rolling back...")

def recover_node(node_id):
    print(f"[INFO] Recovering Node {node_id}")
    neighbors_data = []
    for neighbor_id, address in nodes.items():
        if neighbor_id != node_id:
            try:
                print(f"[INFO] Fetching data from Node {neighbor_id}...")
                response = requests.get(f"{address}/data")
                if response.status_code == 200:
                    neighbors_data.append(response.json()['data_store'])
                    print(f"[SUCCESS] Received data from Node {neighbor_id}.")
            except requests.exceptions.ConnectionError:
                print(f"[ERROR] Node {neighbor_id} is unreachable.")

    data = {"neighbors_data": neighbors_data}
    try:
        response = requests.post(f"{nodes[node_id]}/recover", json=data)
        if response.status_code == 200:
            print(f"[SUCCESS] Node {node_id} recovered successfully.")
    except requests.exceptions.ConnectionError:
        print(f"[ERROR] Node {node_id} is unreachable.")

def print_node_states():
    print("[INFO] Fetching current state of all nodes...")
    for node_id, address in nodes.items():
        try:
            response = requests.get(f"{address}/data")
            if response.status_code == 200:
                print(f"Node {node_id} Data: {response.json()['data_store']}")
        except requests.exceptions.ConnectionError:
            print(f"[ERROR] Node {node_id} is unreachable.")

if __name__ == "__main__":
    while True:
        print("\nDistributed Key-Value Store Manager\n")
        print("Menu:")
        print("1. Write Operation")
        print("2. Recover Node")
        print("3. Print Node States")
        print("4. Exit")
        option = int(input("Select an option: "))

        if option == 1:
            initiator = int(input("Enter initiating node ID: "))
            product_id = input("Enter product ID: ")
            price = int(input("Enter new price: "))
            write_operation(initiator, product_id, price)
        elif option == 2:
            node_id = int(input("Enter node ID to recover: "))
            recover_node(node_id)
        elif option == 3:
            print_node_states()
        elif option == 4:
            print("[INFO] Exiting Distributed Key-Value Store Manager.")
            break
        else:
            print("[ERROR] Invalid option. Please try again.")
