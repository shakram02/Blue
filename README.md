# Blue
[![Build Status](https://travis-ci.org/shakram02/Blue.svg?branch=master)](https://travis-ci.org/shakram02/Blue)
[![](https://jitpack.io/v/shakram02/Blue.svg)](https://jitpack.io/#shakram02/Blue)

An asynchronous networking library that just works


### Use cases

Connect a client

```Kotlin

val client = BlueClient()

// Register for events
client.onConnected += { /* Your code here */ }
client.onReceived += { b:ByteArray -> /*your code here*/ }

// Simple usage
client.connect("localhost", 6001)
client.send("Hello World!".toByteArray())
```