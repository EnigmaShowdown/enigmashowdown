Technologies
===============

This project uses many different technologies. This aims to document them

Kotlin
----------

`Kotlin <https://kotlinlang.org/>`_ is a language that can run on many platforms.
In this project we are targeting the JVM, which is what Java runs on.


Gradle
---------

Gradle is the build tool we are using for this project.
It can be hard to wrap your head around, but its configuration helps automatically download dependencies and helps auto format the code.
Some projects that target the JVM use Maven, but Gradle is a bit more modern and has better Kotlin support.

Spotless
^^^^^^^^

We use the `Spotless Gradle Plugin <https://github.com/diffplug/spotless/tree/main/plugin-gradle#ktlint>`_
with `ktlint <https://pinterest.github.io/ktlint>`_.
This forces you to use consistent coding style.
It comes with an auto-formatter, which you can run by executing ``./gradlew spotlessApply`` or running the ``spotlessApply`` task in IntelliJ.

libGDX
--------

`libGDX <https://libgdx.com/>`_ is a game engine for games written in Java (or more specifically games on the JVM).

Box2D
^^^^^^

`Box2D <https://box2d.org/documentation/>`_ is the `physics engine <https://libgdx.com/wiki/extensions/physics/physics>`_ we will be using.
Box2D is supported in many different programming languages, but since we will only be using Box2D on the server side,
using LibGDX's Box2D wrapper makes the most sense.

ZeroMQ
----------

`ZeroMQ <https://zeromq.org/>`_ is a library that helps with delivering messages between running programs.
We are using the `JeroMQ <https://github.com/zeromq/jeromq>`_ implementation, which is specifically for the JVM.
