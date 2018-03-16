package shakram02.blue

import org.junit.Assert
import org.junit.Assert.*
import java.nio.ByteBuffer

class BlueServerTest {
    private var serverRead = 0
    private val serverMessage = "Hey, Client!"
    private val clientMessage = "Hello world"

    @org.junit.Test
    fun aConnect() {
        val server = BlueServer()
        var received = ""

        server.onConnected += { ch ->
            System.err.println("Client connected")
            val helloMsg = serverMessage.toByteArray()
            ch.write(ByteBuffer.wrap(helloMsg)).get()
        }

        server.onReceived += { receivedEventArgs ->
            serverRead += receivedEventArgs.bytes.size
            received = String(receivedEventArgs.bytes)
            println("Server received:$received")
        }

        server.start("localhost", 60001)

        val socket = BlueClient()

        socket.connect("localhost", 60001)
        waitNetworkOperation()

        socket.send(clientMessage.toByteArray())
        waitNetworkOperation()

        Assert.assertEquals(clientMessage, received)
    }


    private fun waitNetworkOperation() {
        Thread.sleep(300)
    }
}