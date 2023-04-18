#### Purpose
Program runs client, node, and leader that implements a simple leader algorithm. Client should be able to make

multiple request where leader grabs money from the bank to loan to client.

Client -> Leader -> Node -> Leader -> Client

### How to run it

Run nodes1 and node2 take 2 arguments port is the port established and money is amount of money in bank

runNode1 -Pport=7001 -Pmoney=8763

runNode2 -Pport=7002 -Pmoney=4652

Leader takes two arguments port thats established and the nodes that are connected be sure to include ":" between the port num

leader -Pport=4000 -Pnode=7001:7002

Client takes in a port number to connected to leader

client -Pport=4000

### Checklist on what works


(10 points) You will need a README.md which contains the following (most goes
   to the protocol):
   
1. [x] A short screencast where you show your project in action and explain everything
   we need to know about it. (if you do not include a screencast you might lose
   more points if we cannot see some of your features).
2. [x] Explain how to run your program, be specific on what we need to do.
3. [] c) Explain how your different systems communicate with each other. 

    Program starts up with a leader, which then node makes a connections to leader gets a response back from
    leader, from there client can make request to leader from leader get money from nodes and send back to client amount borrowed and pay back

4. [x] d) Explain your project and which requirements you were able to fulfill.
5. [x] e) Explain your protocol. Protocals listed in json Protocal

    Client makes a request that is always associated with a type for request like credit and payback, 

6. [x] (3 point) Project is well-structured and easy to understand.
7. Starting the program: the order in which we need to start things is up to you but
   you need to tell us in your Reamde.
8. [x] a) (3 points) We want to run the leader node through "gradle leader" with some
   default port that is set for us, so the client can easily connect to the leader.
9. [x] b) (4 points) We want to be able to start at least 2 nodes that can communicate
   with the leader. We need to be able to start the nodes through Gradle. Include
   in your Readme how we can start these nodes. It is up to you to treat the
   nodes as servers or as clients to the leader – there are pros and cons to each.
   You can choose what you like best.
10. [x] c) (3 points) Nodes need to start with initial money, include this in your gradle
    task with a default value but also let us call it through something like this
    "gradle node -Pmoney=1000", which would then set the start money of the
    bank to 1000.
11. [x] d) (2 points) The client should start through "gradle client", connecting to the
    leader node with which the client can talk.
12. [x] (3 points) Leader will ask the Client for their clientID, which should be provided by
    the client (simple number is fine, see this as a username or user id).
13. [x] (4 points) Client will then have the choice between requesting a credit or paying part
   of the credit. This should be a client side choice followed by the amount of "credit"
   or "payback". 
14. [x] (3 points) The leader receives the request ("credit", "payback" – or however you want
   to call this) with the amount. See below for details on each operation.
15. Credit (20 points):
16. [x] a) If the client wants credit, then the leader sends all known nodes the information
that a specific client wants a credit of that amount.
17. [] b) Nodes will check if that client already has credit with them and if the node
(representing a bank) has enough money available. If the client with that ID
does not have credit with them yet and the bank has at least 150% of what
the client wants available (yes we want each bank to have more than what the
client wants), they respond with a "yes". If that is not the case, they respond
with a "no".
18. [] c) If the majority of nodes (so if two nodes are connected, both have to answer
yes) say "yes", then the credit is granted to the client.
19. [x] d) The leader will split up the amount as evenly as possible between the nodes
and notify them that this client now has this amount of credit with them. The
nodes will have to persistently save that amount and clientID. The client is
informed that they get the credit and the leader stores the amount persistently
as well.
20. [x] e) Nodes will decrease their available money.
21. [] f) If the majority vote "no", the client will be informed that they do not get any
credit and the nodes will not decrease their available money.
22. [x] g) The client should always be informed why they cannot receive a credit, e.g. too
much money or already have a loan.
23. Pay back (16 points):
24. [x] a) If the client wants to pay money back, then the leader informs the nodes that
   this client wants to pay money back.
25.[x]  b) Nodes will check how much the client owes and return how much that clients
   owes to them individually to the leader.
26. [x] c) If the client wants to pay back more than is owed, they will just get an error
   message which the leader will send to the client.
27. [x] d) If the client pays back partially or all of their existing credit, the leader will
   split up the amount to each node. You should of course not pay back more
   than is owed to a node. The nodes will update their records, the leader will
   update its records and the client will be informed about how much debt is still
   owed.

28. [x] (3 points) You should make sure that when a node crashes the whole system does
   not go down. If the leader crashes then of course a restart might be needed but the
   data should be persistent.
29. [] (6 points) If a restart is needed, the first thing the leader should do is check in with
    the nodes and verify their records, e.g. client=1 owes=100 based on leader, node1
    says client=1 owes=50, node2 says client=1 owes=20. In this case something went
    wrong and you might want to check what happened. Maybe have the leader keep
    track of all transactions so you can roll back. It is up to you how you handle this
    exactly but you should think about this scenario and come up with an idea.
30. [] (5 points) This gets interesting if more than one client can interact with the leader
    and make requests. The system will need to make sure it handles them correctly
    and the order of transactions is still correct.