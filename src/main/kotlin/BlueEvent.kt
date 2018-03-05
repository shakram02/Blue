import java.nio.channels.AsynchronousSocketChannel

data class OnNewClientEventArgs(val channel: AsynchronousSocketChannel, val clientId: Int)
data class OnDataReceivedEventArgs(val channel: AsynchronousSocketChannel, val bytes: ByteArray)
