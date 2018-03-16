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
    private val readHandler = HandlerUtils.toHandler { byteCount: Int, bytes: ByteArray? ->
        onReceived(bytes!!);
        finishRead(byteCount)
    }

    private val writeHandler = HandlerUtils.toHandler { _: Int?, bytes: ByteArray? -> onSent(bytes!!) }
    private val onConnectHandler = HandlerUtils.toHandler({ _: Void?, _: Nothing? -> onConnected(Unit) })

    private val rb = allocateDirect(1024)
    private val channel: AsynchronousSocketChannel = AsynchronousSocketChannel.open()

    val onReceived = Event<ByteArray>()
    val onSent = Event<ByteArray>()
    val onConnected = Event<Unit>()
    val onDisconnected = Event<Unit>()

    fun connect(ip: String, port: Int) {
        channel.connect(InetSocketAddress(ip, port), null, onConnectHandler)
        channel.read(rb, null, readHandler)
    }

    private fun finishRead(byteCount: Int) {
        if (byteCount == -1) {
            onDisconnected(Unit)
            return
        }

        val bytes = HandlerUtils.read(rb).toByteArray()
        onReceived(bytes)
        channel.read(rb, bytes, readHandler)
    }

    fun send(bytes: ByteArray) {
        val wb = ByteBuffer.wrap(bytes)
        channel.write(wb, bytes, writeHandler)
    }

    @Throws(IOException::class)
    override fun close() {
        if (!this.channel.isOpen) return

        try {
            this.channel.shutdownOutput()
            this.channel.shutdownInput()
            this.channel.close()
        } catch (e: AsynchronousCloseException) {
            e.printStackTrace()
        }
    }
}