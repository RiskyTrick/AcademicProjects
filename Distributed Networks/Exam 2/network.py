# network.py
class Network:
    def __init__(self):
        self.nodes = []

    def add_node(self, node):
        self.nodes.append(node)
    
    def get_all_nodes(self):
        return self.nodes
