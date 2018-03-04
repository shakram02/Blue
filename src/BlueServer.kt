import java.io.Closeable
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousCloseException
import java.nio.channels.AsynchronousServerSocketChannel
import java.nio.channels.AsynchronousSocketChannel

class BlueServer : Closeable {
    private var channel: AsynchronousServerSocketChannel = AsynchronousServerSocketChannel.open()
    private val clients = mutableSetOf<AsynchronousSocketChannel>()

    private val acceptHandler = HandlerUtils.toHandler { channel: AsynchronousSocketChannel, _: Nothing? ->
        processAccept(channel)
        onConnected(channel)
    }

    val onConnected = Event<AsynchronousSocketChannel>()

    @Throws(IOException::class)
    fun start(address: String, port: Int) {
        channel.bind(InetSocketAddress(address, port))
        channel.accept(null, acceptHandler)
    }

    private fun processAccept(clientChannel: AsynchronousSocketChannel) {
        clients.add(clientChannel)
        channel.accept(null, acceptHandler)
    }

    @Throws(IOException::class)
    override fun close() {
        try {
            channel.close()
        } catch (e: AsynchronousCloseException) {
            e.printStackTrace()
        }

        for (c in this.clients) {
            c.close()
        }
    }
}
