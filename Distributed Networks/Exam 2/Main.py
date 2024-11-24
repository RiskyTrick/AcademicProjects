# main.py
from network import Network
from node import Node

# Create a network and nodes
network = Network()
nodes = [Node(i, network) for i in range(1, 6)]

# Add nodes to the network
for node in nodes:
    network.add_node(node)

# Initial data setup
nodes[0].store = {123: 50000, 456: 30000}
nodes[1].store = {123: 50000, 456: 30000}
nodes[2].store = {123: 50000, 456: 35000}
nodes[3].store = {123: 50000, 456: 30000}
nodes[4].store = {123: 50000, 456: 30000}

# Simulate a write operation
nodes[0].write_operation(123, 55000)

# Simulate a node crash and recovery
nodes[4].crash()  # Node 5 crashes
nodes[4].recover()  # Node 5 recovers and initiates recovery process
