package shakram02.blue

import java.nio.channels.AsynchronousSocketChannel
import java.util.*

data class OnClientStateChangedEventArgs(val channel: AsynchronousSocketChannel, val clientId: Int)
data class OnDataReceivedEventArgs(val channel: AsynchronousSocketChannel, val bytes: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OnDataReceivedEventArgs

        if (channel != other.channel) return false
        if (!Arrays.equals(bytes, other.bytes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = channel.hashCode()
        result = 31 * result + Arrays.hashCode(bytes)
        return result
    }
}
