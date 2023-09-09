Project Structure
===================

Enigma Showdown is composed of many multiple Gradle modules.
The ``build.gradle`` file in each module folder contains information for the dependencies of that module,
along with information of what module(s) it depends on.

For instance, in :blob:`main/client/build.gradle`, the line ``api project(":core")`` indicates that this module depends on the core module.

Modules
----------

``core`` module
^^^^^^^^^^^^^^^^^

The core module contains pieces of code that are apart of every other module.
Do not put game logic in here. You may put enums, or classes that are a part of the public API (such as packets).

``server`` module
^^^^^^^^^^^^^^^^^^^

The server module contains pieces of code that are for the server portion of Enigma Showdown.
This includes:

* Networking code specifically for the server
* Logic for the game

``client`` module
^^^^^^^^^^^^^^^^^^^^^

This module contains pieces of code that are for communicating with the server and nothing more

``visualization`` module
^^^^^^^^^^^^^^^^^^^^^^^^^^^

The visualization module is the module that actually contains the most game engine (libGDX) code.
This module depends on the ``client`` module so that it can communicate with external servers and render what's actually happening.
If you look closely, you'll also notice that this depends on ``server``.
We include the ``server`` module here because it should not be required to host an external server.
You can simply run the visualization and have a server and visualization in one!
Note that we should develop this so that using an external server works almost the same as using the internal server (it should be a configuration option).

``visualization-lwjgl3`` module
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This module is the desktop implementation of the ``visualization`` module.
You should not have to touch this module.
If we were able to run this game on android, we would have a ``visualization^android`` module, for example.


``test-ai`` module
^^^^^^^^^^^^^^^^^^^^^^

This module should closely mimic how the player would create an ai to play the game.
This depends on the ``client`` module.
We can put whatever code we want in here to test the game.
This can be a sort of "solutions" module for solutions to each level (not necessarily the best solution, though).


