package shakram02.blue

import shakram02.events.PooledEvent
import java.io.Closeable
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousCloseException
import java.nio.channels.AsynchronousServerSocketChannel
import java.nio.channels.AsynchronousSocketChannel

class BlueServer : Closeable {
    private var channel: AsynchronousServerSocketChannel = AsynchronousServerSocketChannel.open()
    private val clients = mutableMapOf<Int, AsynchronousSocketChannel>()
    private val readBuffer = ByteBuffer.allocate(1024)

    companion object {
        private var MAX_ID = 0
    }

    private val readHandler = HandlerUtils.toHandler { count: Int, client: ConnectedClient ->
        if (count == -1) {
            client.channel.close()
            return@toHandler
        }

        val bytes = ByteArray(count)
        readBuffer.get(bytes, 0, 0)

        onReceived(OnDataReceivedEventArgs(client.channel, bytes))
        readNext(client)
    }

    private val acceptHandler = HandlerUtils.toHandler { channel: AsynchronousSocketChannel, id: Int ->
        clients[id] = channel
        channel.read(readBuffer, ConnectedClient(id, channel), readHandler) // Read incoming messages

        onConnected(channel)
        acceptNext()
    }

    val onConnected = PooledEvent<AsynchronousSocketChannel>()
    val onReceived = PooledEvent<OnDataReceivedEventArgs>()

    @Throws(IOException::class)
    fun start(address: String, port: Int) {
        channel.bind(InetSocketAddress(address, port))
        channel.accept(MAX_ID++, acceptHandler)
    }

    private fun acceptNext() {
        channel.accept(MAX_ID++, acceptHandler)
    }

    private fun readNext(client: ConnectedClient) {
        // This method exists because I can't reference readHandler inside itself as it's not yet initialized
        client.channel.read(readBuffer, client, readHandler)
    }

    @Throws(IOException::class)
    override fun close() {
        try {
            channel.close()
        } catch (e: AsynchronousCloseException) {
            e.printStackTrace()
        }

        for (c in this.clients.values) {
            c.close()
        }
    }
}