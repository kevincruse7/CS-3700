# CS 3700 Project 1
Kevin Cruse

## Design
I wrote this project with an object-oriented approach, loosely following the model-view-controller design pattern. For
the model, I defined a set of simple data objects meant to be directly converted to and from their respective JSON
objects for communicating with the server endpoint, in addition to a simple state object for internally tracking the
guesses made and the flag retrieved. For the controller, I split the code into three components: the socket handler, the
message handler, and the word guesser. The socket handler provides a basic interface for reading and writing strings to
a specified host and port, with one implementation providing an unencrypted socket and the other an encrypted one. The
message handler abstracts over the socket handler by providing an interface for sending and receiving Wordle guesses and
responses to and from a specified server. Finally, the word guesser provides an interface for processing a list of
previous Wordle guesses and retrieving a new guess. The message handler and the word guesser are connected in the main
object, which parses the program arguments and defines the main program loop, simply running the word guesser and
message handler until a flag is retrieved, and printing the result out at the end.

## Challenges
The main challenge I faced was the fact that Java doesn't have a built-in JSON parser, meaning that I had to pull in a
third-party library to parse messages to and from the server. While importing dependencies is simple with a build
system like Maven, which I used on my end, Gradescope doesn't have such functionality. So, it took some trial and error
playing around with the Makefile and the client script to ensure everything would compile and run properly. Since I
figured out how to manage third-party libraries, though, I was also able to pull in Lombok, which provides convenient
annotations for automatically generating many common types of boilerplate code.

## Guessing Strategy
The guessing strategy I went with is very straightforward. I start with a completely random guess from the given word
list, and for subsequent guesses, I keep track of exact and inexact character guesses. I then filter out all words that
don't possess all the found exact and inexact character matches, and I randomly select a word from this new, filtered
list. This strategy seems to consistently find the correct word in under ~8 guesses.

## Testing
I tested my code by trying different permutations of port and encryption command-line arguments, ensuring that I
consistently got the same, expected flags from the server. I also sent a preliminary submission to Gradescope to ensure
that the program still worked when given an alternative hostname and/or username.