import org.junit.After
import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import shakram02.blue.BlueClient
import shakram02.blue.BlueServer
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousCloseException

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class BlueClientTest {
    private val server = BlueServer()

    @org.junit.Before
    fun setup() {
        server.onConnected += { ch ->
            System.err.println("Client connected ${ch.remoteAddress}")
            ch.write(ByteBuffer.wrap("asdsad".toByteArray())).get()
        }

        server.onReceived += { connectedClient ->
            System.err.println("${connectedClient.channel.remoteAddress} sent ${String(connectedClient.bytes)}")
        }

        server.start("localhost", 60001)
    }

    @After
    fun teardown() {
        try {
            server.close()
            waitNetworkOperation()
        } catch (e: AsynchronousCloseException) {
            // TODO checkout how to make sure that both sides close gracefully
        }
    }

    @org.junit.Test
    fun aConnect() {
        waitNetworkOperation()  // Wait for Server to start

        val socket = BlueClient()
        var hasConnected = false
        socket.onConnected += { hasConnected = true }
        socket.connect("localhost", 60001)

        waitNetworkOperation()
        Assert.assertTrue(hasConnected)
        try {
            socket.close()
        } catch (e: AsynchronousCloseException) {

        }
    }

    @Test
    fun bRead() {
        waitNetworkOperation()

        val socket = BlueClient()
        var hasConnected = false
        var received = ""
        socket.onConnected += { hasConnected = true }
        socket.onReceived += { b -> received = String(b) }

        socket.connect("localhost", 60001)
        waitNetworkOperation()

        socket.send("Hello world".toByteArray())
        waitNetworkOperation()

        Assert.assertTrue(hasConnected)
        Assert.assertTrue(received.isNotEmpty())
        try {
            socket.close()
        } catch (e: AsynchronousCloseException) {

        }
    }

    private fun waitNetworkOperation() {
        Thread.sleep(20)
    }
}