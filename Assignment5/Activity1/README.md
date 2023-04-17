#### Purpose:
Very basic peer-2-peer for a chat. All peers can communicate with each other. 

Each peer is client and server at the same time. 
When started the peer has a serverthread in which the peer listens for potential other peers to connect thats automatically.

Chatting starts when client joins

Client Thread constantly listens.

ServerThread writes every registered listener (the other peers). 

### How to run it

Arguments are iip, port, and nodde. Start 2 to many peers each having a unique port number. 

runActivity -Pip=name -Pport=4000 -Pnode=7000
Pport = the client port
Pnode = the port its tryiing to connect
Pip = the name of the client

Will automatically listen to any number of peers when client tries to join. Unfortunately does not properly exit.