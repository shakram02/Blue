# Blue
[![Build Status](https://travis-ci.org/shakram02/Blue.svg?branch=master)](https://travis-ci.org/shakram02/Blue)
[![](https://jitpack.io/v/shakram02/Blue.svg)](https://jitpack.io/#shakram02/Blue)

An asynchronous networking library that just works


### Use cases

Connect a client

```Kotlin
val server = BlueServer()
server.onConnected += { ch ->
    System.err.println("Client connected")
    val helloMsg = "Hey, Client!".toByteArray()
    ch.write(ByteBuffer.wrap(helloMsg)).get()
}

server.onReceived += { connectedClient ->
    val received = connectedClient.bytes
    System.out.println("Received: ${String(received)}")
}

server.start("localhost", 60001)

val client = BlueClient()

// Register for events
client.onConnected += { /* Your code here */ }
client.onReceived += { b:ByteArray -> /*your code here*/ }

// Simple usage
client.connect("localhost", 6001)
client.send("Hello World!".toByteArray())
```

Check the `tests/kotlin` for more info.

