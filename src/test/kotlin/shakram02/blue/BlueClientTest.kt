package shakram02.blue

import org.junit.After
import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousCloseException

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class BlueClientTest {
    private val server = BlueServer()
    private var serverRead = 0
    private var serverExpectedRead = 0

    @org.junit.Before
    fun setup() {
        server.onConnected += { ch ->
            System.err.println("Client connected")
            val helloMsg = "Hey, Client!".toByteArray()
            ch.channel.write(ByteBuffer.wrap(helloMsg)).get()
        }

        server.onReceived += { connectedClient ->
            val received = connectedClient.bytes
            serverRead += received.size
            System.err.println("Received: ${String(received)}")
        }

        server.start("localhost", 60001)
        waitNetworkOperation()  // Wait for Server to start
    }

    @After
    fun teardown() {
        try {
            Assert.assertEquals(serverExpectedRead, serverRead)
            waitNetworkOperation()
            server.close()
        } catch (e: AsynchronousCloseException) {
            // TODO checkout how to make sure that both sides close gracefully
        }
    }

    @org.junit.Test
    fun aConnect() {
        val socket = BlueClient()
        var hasConnected = false
        socket.onConnected += { hasConnected = true }
        socket.connect("localhost", 60001)

        waitNetworkOperation()
        Assert.assertTrue(hasConnected)
    }

    @Test
    fun bRead() {
        val socket = BlueClient()
        var hasConnected = false
        var received = ""
        socket.onConnected += { hasConnected = true }
        socket.onReceived += { b -> received = String(b) }

        socket.connect("localhost", 60001)
        waitNetworkOperation()

        val msg = "Hello world".toByteArray()
        socket.send(msg)
        serverExpectedRead += msg.size
        waitNetworkOperation()

        Assert.assertTrue(hasConnected)
        Assert.assertTrue(received.isNotEmpty())
    }

    private fun waitNetworkOperation() {
        Thread.sleep(300)
    }
}
