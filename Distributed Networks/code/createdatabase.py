import mysql.connector

# Function to execute SQL file
def execute_sql_file(cursor, file_path):
    with open(file_path, 'r') as sql_file:
        sql_commands = sql_file.read()

    # Split the commands by semicolons
    commands = sql_commands.split(';')

    # Execute each command
    for command in commands:
        if command.strip():
            try:
                cursor.execute(command)
            except mysql.connector.Error as err:
                print(f"Error: {err}")
                return False
    return True

# Main function to set up the database
def setup_database():
    # Connect to MySQL Server
    connection = mysql.connector.connect(
        host="localhost",      # Replace with your host if necessary
        user="root",           # Your MySQL username
        password="5359"        # Your MySQL password
    )
    cursor = connection.cursor()

    # Execute the SQL file
    if execute_sql_file(cursor, 'database.sql'):
        print("Database and tables created successfully.")
    else:
        print("Failed to create database or tables.")

    # Commit changes and close the connection
    connection.commit()
    cursor.close()
    connection.close()

if __name__ == "__main__":
    setup_database()
