import threading
import mysql.connector
from mysql.connector import Error
from xmlrpc.server import SimpleXMLRPCServer

def connect_to_db():
    try:
        return mysql.connector.connect(
            host="localhost",
            user="root",
            password="5359",
            database="bankdatabase"
        )
    except Error as err:
        print(f"Database connection error: {err}")
        return None

class BankServer:
    def __init__(self, host='127.0.0.1', port=1234):
        self.lock = threading.Lock()
        self.server = SimpleXMLRPCServer((host, port), allow_none=True)
        print(f"Bank Server started on {host}:{port}")
        self.server.register_function(self.create_account, "create_account")
        self.server.register_function(self.deposit_money, "deposit_money")
        self.server.register_function(self.withdraw_money, "withdraw_money")
        self.server.register_function(self.check_balance, "check_balance")

    def create_account(self, account_number, name, balance):
        print(f"Creating account: {account_number}, Name: {name}, Balance: {balance}")
        db_conn = connect_to_db()
        if db_conn is None:
            return "Error: Database connection failed"
        
        cursor = db_conn.cursor()
        try:
            with self.lock:
                cursor.execute("INSERT INTO accounts (account_number, name, balance) VALUES (%s, %s, %s)", 
                               (account_number, name, balance))
                db_conn.commit()
                # Record the transaction
                cursor.execute("INSERT INTO transactions (account_number, transaction_type, amount) VALUES (%s, 'create', %s)", 
                               (account_number, balance))
                db_conn.commit()
            return "Account created successfully"
        except Error as err:
            return f"Database error: {err}"
        finally:
            cursor.close()
            db_conn.close()

    def deposit_money(self, account_number, amount):
        db_conn = connect_to_db()
        if db_conn is None:
            return "Error: Database connection failed"
        
        cursor = db_conn.cursor()
        try:
            with self.lock:
                cursor.execute("UPDATE accounts SET balance = balance + %s WHERE account_number = %s", 
                               (amount, account_number))
                db_conn.commit()
                # Record the transaction
                cursor.execute("INSERT INTO transactions (account_number, transaction_type, amount) VALUES (%s, 'deposit', %s)", 
                               (account_number, amount))
                db_conn.commit()
            return "Deposit successful"
        except Error as err:
            return f"Database error: {err}"
        finally:
            cursor.close()
            db_conn.close()

    def withdraw_money(self, account_number, amount):
        db_conn = connect_to_db()
        if db_conn is None:
            return "Error: Database connection failed"
        
        cursor = db_conn.cursor()
        try:
            with self.lock:
                cursor.execute("SELECT balance FROM accounts WHERE account_number = %s", (account_number,))
                result = cursor.fetchone()
                if result is None:
                    return "Error: Account not found"
                
                balance = result[0]
                if balance >= amount:
                    cursor.execute("UPDATE accounts SET balance = balance - %s WHERE account_number = %s", 
                                   (amount, account_number))
                    db_conn.commit()
                    # Record the transaction
                    cursor.execute("INSERT INTO transactions (account_number, transaction_type, amount) VALUES (%s, 'withdraw', %s)", 
                                   (account_number, amount))
                    db_conn.commit()
                    return "Withdrawal successful"
                else:
                    return "Error: Insufficient funds"
        except Error as err:
            return f"Database error: {err}"
        finally:
            cursor.close()
            db_conn.close()

    def check_balance(self, account_number):
        db_conn = connect_to_db()
        if db_conn is None:
            return "Error: Database connection failed"
        
        cursor = db_conn.cursor()
        try:
            cursor.execute("SELECT balance FROM accounts WHERE account_number = %s", (account_number,))
            result = cursor.fetchone()
            if result is None:
                return "Error: Account not found"
            return f"Balance: {result[0]}"
        except Error as err:
            return f"Database error: {err}"
        finally:
            cursor.close()
            db_conn.close()

    def start_server(self):
        print("Bank Server is running and waiting for requests...")
        self.server.serve_forever()

if __name__ == "__main__":
    server = BankServer()
    server.start_server()
