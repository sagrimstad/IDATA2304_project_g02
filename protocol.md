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
1337

## Architecture
- Servers: One separate Server that is used for communication between each client.
- Clients: Each control panel and nodes are clients.

## Information flow
1. All sensors and actuator nodes connects to the main Server.
2. Control panels connects the Server.
3. Control panels connects to every actuator and sensor nodes in the network.
4. Sensors register new data and sends this data to every connected control panel.
5. Control panels receives the data and needs to understand the data and react.
6. Control panels then sends (if needed) commands or instructions to the nodes to do a task.
7. Then steps 4-6 will loop for as long as needed/wanted.

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