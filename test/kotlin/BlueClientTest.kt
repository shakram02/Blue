import org.junit.After
import org.junit.Assert
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import java.nio.ByteBuffer

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class BlueClientTest {
    private val server = BlueServer()

    @org.junit.Before
    fun setup() {
        server.start("localhost", 60001)
        server.onConnected += { ch ->
            System.err.println("Client connected ${ch.remoteAddress}")
            ch.write(ByteBuffer.wrap("asdsad".toByteArray())).get()
        }
    }

    @After
    fun teardown() {
        server.close()
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
        socket.close()
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

        socket.close()
        Assert.assertTrue(hasConnected)
        Assert.assertTrue(received.isNotEmpty())
        Assert.assertTrue(false)
    }

    private fun waitNetworkOperation(){
        Thread.sleep(20)
    }
}