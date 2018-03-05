package shakram02.blue

import java.nio.channels.AsynchronousSocketChannel

data class ConnectedClient(val id: Int, val channel: AsynchronousSocketChannel)