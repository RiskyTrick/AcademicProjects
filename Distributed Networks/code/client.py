import socket

def main():
    branch_ip = '192.168.1.110'  # Change this to your branch server IP
    branch_port = 12345
    
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.connect((branch_ip, branch_port))
        while True:
            print("\nBanking System")
            print("1. Create Account")
            print("2. Deposit Money")
            print("3. Withdraw Money")
            print("4. Check Balance")
            print("5. Exit")
            choice = input("Enter your choice (1-5): ")

            if choice == '1':
                account_number = input("Enter account number: ")
                name = input("Enter account holder's name: ")
                balance = input("Enter initial balance: ")
                request = f"create_account,{account_number},{name},{balance}"
            elif choice == '2':
                account_number = input("Enter account number: ")
                amount = input("Enter amount to deposit: ")
                request = f"deposit_money,{account_number},{amount}"
            elif choice == '3':
                account_number = input("Enter account number: ")
                amount = input("Enter amount to withdraw: ")
                request = f"withdraw_money,{account_number},{amount}"
            elif choice == '4':
                account_number = input("Enter account number: ")
                request = f"check_balance,{account_number}"
            elif choice == '5':
                break
            else:
                print("Invalid choice. Please try again.")
                continue
            
            s.sendall(request.encode('utf-8'))
            response = s.recv(1024).decode('utf-8')
            print(f"Response from branch server: {response}")

if __name__ == "__main__":
    main()
