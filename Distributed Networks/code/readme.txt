Database Setup
Create the Database:

Run the createdatabase.py script to create the database and necessary tables. Ensure that the database.sql file contains the SQL commands to create both the accounts and transactions tables.
bash
Copy code
python createdatabase.py
Verify the Database:

After executing the above script, you can check your MySQL database to confirm that the bankdatabase, accounts, and transactions tables have been created successfully.
Running the Servers
Start the Main Server:

Open a terminal and navigate to the directory containing your server code. Run the following command:
bash
Copy code
python Server.py
This will start the main bank server, which listens for requests from branch servers.

Start the Branch Server:

In a new terminal window, navigate to the directory containing your branch server code and run:
bash
Copy code
python BranchServer.py
This will start the branch server, which connects to the main server and listens for client requests.

Running the Client
Start the Client:

Open another terminal window and run the client code:
bash
Copy code
python Client.py
The client will display a menu for the user to interact with the banking system. You can choose to create an account, deposit money, withdraw money, check balance, or exit.

Usage
Create Account: Enter account number, name, and initial balance.
Deposit Money: Enter account number and amount to deposit.
Withdraw Money: Enter account number and amount to withdraw.
Check Balance: Enter account number to see the current balance.
Exit: Exit the client application.
Troubleshooting
Ensure that the MySQL server is running and accessible.
Verify that the database credentials in createdatabase.py are correct.
Check for any errors in the terminal output for both the server and client.