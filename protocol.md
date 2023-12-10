# Protocol
This document describes the protocol used in the simulated greenhouse. The greenhouse consists of
different  sensor and actuator nodes that detects changes in the environment inside the greenhouse. These
nodes are connected to a server which provide information to control panels over the network. 
The control panels can see the different temperatures, humidity, etc. inside the greenhouse. From the control 
panels, one can operate these actuators. Typical operations would be turning on or off fans, window and heaters. 

## Terminology
Actuator: A device that produces a motion by converting energy and signals going into the system. In our case the 
actuator open/closes windows and turn on/off heaters and fans. 
Sensors: Devices processing environment data such as temperatures and humidity data.
Control panel: A system displaying information about actuator and sensor nodes. Also works as a remote control. 
Server: Information handling and information distribution. Feeding information to the control panels.

## Transport
This application uses TCP for its communication. TCP (Transmission Control Protocol)

## Used port numbers
Server port: 1337
An ephemeral port number is recommended to be greater than
1023 for some client/server programs to work properly.

## Architecture
Servers: 
- One dedicated server hosted centrally in the greenhouse. Contains a map of all sensor/actuator nodes
- Responsible for: Receiving data from sensors, processing that data, 
  accepting new commands from the control panel and changing the actuators accordingly. 

Clients:
1. Control Panels:
- Interface for a user to visualize real-time data from the sensors.
- Sending commands to the server based on user input.

## Information flow
1. The Greenhouse simulator initializes and starts the Greenhouse Server.  
2. Control panels connects to the Server. When the control-panel is connected to the greenhouse, 
   Information about sensor and actuator nodes are forwarded.
3. Sensors register new data and sends this data to the control-panels through the server.
4. Control panels receives the data from the server, and needs to understand the data and react.
5. Control panels then sends (if needed) commands or instructions to the server to change the state of the actuator(s).
6. Then steps 3-5 will loop for as long as needed/wanted.

The sensor nodes will actively report information to the server. The clients/control-panels subscribe to this 
information. This makes the information flow a publish-subscribe pattern. Changes made by one component are 
communicated via a central component, the server.

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

## Message format
Allowed message types:

Data-message sent from sensors to control panel:
- Advertise sensor data "'desired_node';type=value unit""
- Advertise Actuator state "desired_node, desired_actuator, on (true or false)"

Command-message send from control panel to sensors/actuators:
- Specify the nodeId, actuatorId and the desired actuator state ("on", "off").

Data message sent from server to clients
- If the server is finished sending over all the nodes the control panel a null is sent to mark the end.

Type of marshalling used (fixed size, separators, TLV?)
The type of marshalling used is separators. Commas and semicolons To separate message and individual fields 
within a message.

Which messages sent by which node?
The data message are sent by the sensor nodes. 
The command messages are sent from the control panels.

## Error handling
There is no handling of errors such as network outages, however there is used structured error handling. The try catch
statement is an idea of enclosing a block of code (the 'try' block) that might generate exceptions and then specify a
'catch' block handling those exceptions. This is commonly used in the development of the application There is also 
simple if checks ensuring that the right input is inputted. 

## Realistic scenario
A hypothetical scenario could start with someone somewhere opens/starts the control panel app and 
can see every connected node. In the application, the user can then observe every parameter for each
node and perform different commands, like open a window, turn on a heater etc. The commands will
then be sent to the desired node and a change will happen. The greenhouse will operate like this for
its whole lifespan, each node sending its information to the control panel, and the control panel 
sending commands to the nodes.

## Security
There are no security features implemented in the protocol yet.  