# Protocol
This document describes the protocol used in the simulated greenhouse. The greenhouse consists of
different actuators and nodes that detects changes in the environment inside the greenhouse. These
actuators and nodes connect to different control panels that gets the information over the network
and can see the different temperatures, humidity, etc. inside the greenhouse. From the control 
panels, one can operate these actuators and perform different task to change the different 
variables inside.

## Terminology
Actuators, Nodes, control panel, server

## Transport
This application uses TCP for its communication. TCP (Transmission Control Protocol)

## Used port numbers
Server port: 1337

## Architecture
Servers: 
- One dedicated server hosted centrally in the greenhouse.
- Responsible for: Receiving data from sensors, processing that data, 
accepting new commands from the control panel and sending those commands to the actuators.

Clients: 
1. Sensors/Actuators:
- Collect data (temperature, humidity etc.) and sending it to the server at either intervals or when 
a change is registered.
- Receive commands from the server to perform actions (opening windows, activating fans etc.)

2. Control Panels:
- Interface for a user to visualize real-time data from the sensors.
- Sending commands to the server based on user input.

## Information flow
1. All sensors and actuator nodes connects to the main Server.
2. Control panels connects the Server.
3. Sensors register new data and sends this data to the server.
4. Control panels receives the data from the server, and needs to understand the data and react.
5. Control panels then sends (if needed) commands or instructions to the nodes to do a task.
6. Then steps 3-5 will loop for as long as needed/wanted.

Data send:
- temperature
- humidity
- window on/off
- heater on/off
- fan on/off

Commands send:
- which sensor to command
- turn on / turn off
- open window / close window
- turn on heater / turn off heater
- turn on fan / turn off fan

## Type of protocol
- Connection-orientated
- State-full

## Constants used
different types and special values used...

## Message format
Allowed message types:

Data-message sent from sensors to control panel:
- "'desired_node';temperature=## °C";humidity=## %"

Command-message send from control panel to sensors/actuators:
- ""

Type of marshalling used (fixed size, separators, TLV?)
Which messages sent by which node?

## Error handling

## Realistic scenario
A hypothetical scenario could start with someone somewhere opens/starts the control panel app and 
can see every connected node. In the application, the user can then observe every parameter for each
node and perform different commands, like open a window, turn on a heater etc. The commands will
then be sent to the desired node and a change will happen. The greenhouse will operate like this for
its whole lifespan, each node sending its information to the control panel, and the control panel 
sending commands to the nodes.

## Security