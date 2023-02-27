a) (1.5 points) A description of your project and a detailed description of what it
does.
This is a guessing game that tracks your score and leader board every time a new cloent connects. If you give 3 right guess before timer
runs out you win. If you dont give 3 right guesses you lose and would be asked to play again.
b) (1.5 points) Include a checklist of the requirements marking if you think you
fulfill this requirement or not.
Checklist is in another text doc.
c) (1 points) An explanation of how we can run the program (hopefully exactly
as described in this document).
to run in terminal command is
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
client protocal
request SendName{
	"type", "name"
	"message", "User name"
}
request guess{
	"type", "guess"
	"Score", 8 
	"name", "client name"
	"message", "character guess" -- joker, captain america, etc
}
request leaderboard{
	"type", "leader"
}
request next{
	"type", "next"
}
request more{
	"type", "more"
}
request exit{
	"type", "exit game"
	"message", "game has ended"
	"score", 5
	"name", "users name"
}
request errorReq{
	"type", "error" --handles any invalid input by user
	"message", "not a valid input"
}

Server protocal
response askUser{		//asks for their name
	"type", "demand"
	"question", "name"
	"message", "Enter your name"
}
response error{	//if client sends a requests that does not exist
	"type", "error"
	"message", "no valid options"
}
response reply{
	"type", "welcome"
	"message", "welcome to my game "their name" type 'play' to play game or 'leader' to see leaderboard"
}
response gameplay{
	"type", "game"
	"message", "game has started"
}
response imageIter{ //iterates over image quotes
	"type", "image"
	"characterName", "character" // it joker, wolverine, etc
	"data", "image data sent back"
	if error occers
	"errorMsg", "can not find file"
}
response imageScore{ //iterates over image quotes
	"type", "image"
	"characterName", "character" // it joker, wolverine, etc
	"score", 5 // some number
	"message", "you guessed right" 
	"data", "image data sent back"
	if error occers
	"errorMsg", "can not find file"
}

f) (2.5 points) Explain how you designed your program to be robust (see later
under constraints).
How I made this design robust is by making sure each protcal sent between client to server and
vice versa is a type is always specified to easily identify what response and request is given.
Another way I went about was to seperate where my client is being ran and how its being ran.
