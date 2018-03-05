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
        val socket = BlueClient()
        var hasConnected = false
        socket.onConnected += { hasConnected = true }
        socket.connect("localhost", 60001)

        Thread.sleep(200)
        Assert.assertTrue(hasConnected)
        socket.close()
    }

    @Test
    fun bRead() {
        val socket = BlueClient()
        var hasConnected = false
        var received = ""
        socket.onConnected += { hasConnected = true }
        socket.onReceived += { b -> received = String(b) }
        socket.connect("localhost", 60001)
        socket.send("Hello world".toByteArray())
        Thread.sleep(200)
        Assert.assertTrue(hasConnected)
        Assert.assertTrue(received.isNotEmpty())
    }
}