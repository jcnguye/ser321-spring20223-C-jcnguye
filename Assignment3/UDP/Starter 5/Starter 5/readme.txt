a) (1.5 points) A description of your project and a detailed description of what it
does.
Implementing a udp protocal that responds for name, request made to name, and request for image coming back.
b) (1.5 points) Include a checklist of the requirements marking if you think you
fulfill this requirement or not.
1. asks for name	[x]
2. sends name	[x]
3. gets picture   [x]
c) (1 points) An explanation of how we can run the program (hopefully exactly
as described in this document).
- gradle runClient - Pport='your port num' - Phost='localhost' (example: gradle runClient -Pport=8080 -Phost=localhost)
- gradle runServer - Pport= 'your port num' (example: gradle runServer - Pport=8080)
default port and host if no input given,
8080 and local host.
d) (3 points) A UML diagram showing the back and forth communication between
client and server. Which UML diagram you use is up to you.

e) (4.5 points) A description of your protocol similar to what you usually see
when a protocol is described. You should describe each request and all possible
responses (including errors). See the add-on video on Canvas on the Sockets
page for more details.

f) (2.5 points) Explain how you designed your program to be robust (see later
under constraints).
