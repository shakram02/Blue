# Blue
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