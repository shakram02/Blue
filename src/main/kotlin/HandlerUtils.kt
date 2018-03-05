import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.channels.CompletionHandler

/**
 * Source: https://bitbucket.org/javasabr/publictest
 */
class HandlerUtils {
    companion object {
        fun <V, A> toHandler(fnSuccess: (V, A) -> Unit): CompletionHandler<V, A> {
            return object : CompletionHandler<V, A> {
                override fun completed(result: V, attachment: A) {
                    fnSuccess(result, attachment)
                }

                override fun failed(t: Throwable?, p1: A) {
                    t!!.printStackTrace()
                    throw t
                }

            }
        }

        fun <V, A> toHandler(fnSuccess: (V, A) -> Unit,
                             fnFailure: (Throwable?, A) -> Unit): CompletionHandler<V, A> {
            return object : CompletionHandler<V, A> {
                override fun completed(result: V, attachment: A) {
                    fnSuccess(result, attachment)
                }

                override fun failed(exc: Throwable?, attachement: A) {
                    fnFailure(exc, attachement)
                }
            }
        }

        fun read(buffer: ByteBuffer): ByteArrayOutputStream {
            buffer.flip()

            val dataOutput = ByteArrayOutputStream()

            while (buffer.hasRemaining()) {
                dataOutput.write(buffer.get().toInt())
            }

            buffer.clear()
            return dataOutput
        }
    }
}