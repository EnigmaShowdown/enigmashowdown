Enigma Showdown Framework
==========================

This contains documentation for how the framework works.

The framework is a set of files that help facilitate communication using the protocols we have defined.
In an Enigma Showdown game, there are a few different components:

* The server

  * Has two open ports that clients can communicate with

    * Broadcast port (31877)

      * Broadcast messages are sent on this port that all clients can see.
      * One broadcast message is sent each tick. This message contains the state of the entire world, and gives the client AI enough information on what actions it has available to make
      * Clients can not send messages to the server on this port

    * Server port (31878)

      * Clients can send messages to the server on this port, and the server responds with a response.
      * This port is most similar to an HTTP endpoint.
      * This is how client AIs send their desired actions to the server

* The clients

  * There are two main types of clients: The visualization client and the client AI
  * The ``visualization`` module can connect to a server, and can also host a server and act as a client to the server its hosting
  * The client AI is the program created by the player.
  * All clients receive broadcast messages from the server, and for interaction that needs to be done with the server, the client will make requests to the server port.
