from flask import Flask, request, jsonify

class Node:
    def __init__(self, node_id):
        self.node_id = node_id
        self.data_store = {}

    def write_data(self, product_id, new_price):
        self.data_store[product_id] = new_price
        print(f"[SUCCESS] Node {self.node_id}: Updated Product {product_id} with price {new_price}")
        return True

    def recover(self, neighbors_data):
        for product_id in self.data_store.keys():
            values = [data[product_id] for data in neighbors_data if product_id in data]
            if len(values) >= 2:
                majority_value = max(set(values), key=values.count)
                self.data_store[product_id] = majority_value
                print(f"[INFO] Node {self.node_id}: Recovered Product {product_id} with price {majority_value}")
            else:
                print(f"[WARNING] Node {self.node_id}: Could not recover Product {product_id} due to insufficient quorum.")

app = Flask(__name__)
node = None  

@app.route('/write', methods=['POST'])
def write():
    data = request.json
    product_id = data['product_id']
    new_price = data['new_price']
    success = node.write_data(product_id, new_price)
    return jsonify({"status": "success" if success else "failure"})

@app.route('/recover', methods=['POST'])
def recover():
    neighbors_data = request.json['neighbors_data']
    node.recover(neighbors_data)
    return jsonify({"status": "recovered"})

@app.route('/data', methods=['GET'])
def get_data():
    return jsonify({"data_store": node.data_store})

if __name__ == "__main__":
    import sys
    node_id = int(sys.argv[1])
    node = Node(node_id)
    port = 5000 + node_id
    print(f"[INFO] Node {node_id} starting on port {port}")
    app.run(host="0.0.0.0", port=port)
