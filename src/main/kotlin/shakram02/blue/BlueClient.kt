package shakram02.blue

import shakram02.events.Event
import java.io.Closeable
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.ByteBuffer.allocateDirect
import java.nio.channels.AsynchronousCloseException
import java.nio.channels.AsynchronousSocketChannel


class BlueClient : Closeable {
    private val readHandler = HandlerUtils.toHandler { byteCount: Int, _: Nothing? -> finishRead(byteCount) }
    private val writeHandler = HandlerUtils.toHandler { x: Int?, _: Nothing? -> onSent(x!!) }
    private val onConnectHandler = HandlerUtils.toHandler({ _: Void?, _: Nothing? ->
        run { onConnected(Unit); readNext() }
    })

    private val rb = allocateDirect(1024)
    private val channel: AsynchronousSocketChannel = AsynchronousSocketChannel.open()

    val onReceived = Event<ByteArray>()
    val onSent = Event<Int>()
    val onConnected = Event<Unit>()

    fun connect(ip: String, port: Int) {
        channel.connect(InetSocketAddress(ip, port), null, onConnectHandler)
    }

    private fun finishRead(byteCount: Int) {
        if (byteCount == -1) return
        onReceived(HandlerUtils.read(rb).toByteArray())
        readNext()
    }

    fun send(bytes: ByteArray) {
        val wb = ByteBuffer.wrap(bytes)
        // Replace null with attachment object
        channel.write(wb, null, writeHandler)
    }

    private fun readNext() {
        channel.read(rb, null, readHandler)
    }

    @Throws(IOException::class)
    override fun close() {
        try {
            this.channel.close()
        } catch (e: AsynchronousCloseException) {
            e.printStackTrace()
        }
    }
}