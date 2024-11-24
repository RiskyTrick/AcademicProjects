import random
import time
import matplotlib.pyplot as plt
from flask import Flask, render_template

app = Flask(__name__)

# Initialize variables for plotting
time_steps = []
attack_counts = []

def simulate_attack():
    # Simulate an attack function
    print("Attacker attempting to exploit vulnerability...")

def change_network_config():
    # Simulate changing network configuration
    print("Changing network configuration...")
    # Here you can implement logic to change network configurations such as IP addresses, firewall rules, etc.

def plot_data():
    # Plot the data
    plt.clf()
    plt.plot(time_steps, attack_counts, marker='o')
    plt.xlabel('Time Step')
    plt.ylabel('Number of Attack Attempts')
    plt.title('Moving Target Defense Simulation')
    plt.savefig('plot.png')

@app.route('/')
def index():
    return render_template('index.html', time_steps=time_steps, attack_counts=attack_counts)

def main():
    # Simulate MTD by periodically changing network configuration
    attack_count = 0
    time_step = 0
    
    while True:
        try:
            # Simulate network activity
            print("Monitoring network activity...")
            time.sleep(2)  # Simulate network monitoring interval
            
            # Update time step and record for plotting
            time_step += 1
            time_steps.append(time_step)
            
            # Check for attack attempts
            if random.random() < 0.3:  # Simulate 30% chance of attack attempt
                attack_count += 1
                attack_counts.append(attack_count)
                print(f"Attack detected ({attack_count} attempts).")
                simulate_attack()
                
                # Implement MTD: Change network configuration after attack attempt
                change_network_config()
                
                # Plot the data
                plot_data()
                
            # Check if user wants to stop the simulation
            if input("Enter 'stop' to stop the simulation: ").lower() == 'stop':
                print("Simulation stopped by user.")
                break
                
        except KeyboardInterrupt:
            print("\nSimulation stopped by user.")

if __name__ == "__main__":
    main()
    app.run(debug=True)
