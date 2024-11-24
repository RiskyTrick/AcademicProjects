This Distributed Banking System Project is Built by Team effort of Mr.Bhavesh Naidu Kulluru, MS.CSIS SVSU, And Mr.Robert Orosz MS.CSIS SVSU. 

The project is a Simulation of how a Distributed banking system Operates without cryptography incorporated!

The Model uses Java version 8.0 or Above to operate!

The Database used in the model is MySQL. 

========================================================================

Understanding Architecture

The core of the model is its Database which is critical for any function of the model. 

Followed by a central Server which has the Main methods for the functionality of the model. 

Followed by sever Branch Modules which make use of an RMI server Interface to communicate with CentralServer , these Branch Modules are distributed located at different branches. 

The client Code connects to a specified branch's ip remotely using Socket programming!

======================================================================

How to RUN the application

Post installation of the database !

run the Central server 1st in a terminal , with help of DBinit.sql it initializes the database and tables for first time.

run any branch Module code in a different terminal. 

now find your Device Network IP using ipconfig command in CMD

using the Device IP replace the Host name in client code and run it in any device!
