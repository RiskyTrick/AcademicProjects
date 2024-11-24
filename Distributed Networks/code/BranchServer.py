import socket
import xmlrpc.client

class BranchServer:
    def __init__(self, branch_ip='192.168.1.110', branch_port=12345, main_server_url='http://127.0.0.1:1234'):
        self.branch_ip = branch_ip
        self.branch_port = branch_port
        self.main_server_url = main_server_url
        print(f"Branch server starting at {branch_ip}:{branch_port}")

    def start_branch_server(self):
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.bind((self.branch_ip, self.branch_port))
            s.listen()
            print(f"Branch server started at {self.branch_ip}:{self.branch_port}")

            while True:
                conn, addr = s.accept()
                print(f"Connection established with {addr}")
                with conn:
                    data = conn.recv(1024).decode('utf-8')
                    print(f"Data received from client: {data}")

                    command, *args = data.split(",")
                    response = self.handle_request(command.strip(), *args)
                    conn.sendall(response.encode('utf-8'))

    def handle_request(self, command, *args):
        with xmlrpc.client.ServerProxy(self.main_server_url) as server:
            if command == "create_account":
                return server.create_account(args[0], args[1], float(args[2]))
            elif command == "deposit_money":
                return server.deposit_money(args[0], float(args[1]))
            elif command == "withdraw_money":
                return server.withdraw_money(args[0], float(args[1]))
            elif command == "check_balance":
                return server.check_balance(args[0])
            else:
                return "Invalid command"

if __name__ == "__main__":
    branch_server = BranchServer()
    branch_server.start_branch_server()
