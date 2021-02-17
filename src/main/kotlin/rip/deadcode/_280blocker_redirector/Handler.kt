package rip.deadcode._280blocker_redirector

import com.google.api.client.http.GenericUrl
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.common.net.HttpHeaders
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class Handler : AbstractHandler() {

    override fun handle(
        target: String,
        baseRequest: Request,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {

        val now = LocalDate.now().plusMonths(1)
        val url = getUrl(now, 5)

        response.status = 307
        response.addHeader("Location", url)
        response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
        baseRequest.isHandled = true
    }

    private val logger = LoggerFactory.getLogger(Handler::class.java)
    private val httpTransport = NetHttpTransport()
    private val formatter = DateTimeFormatter.ofPattern("uuuuMM")

    fun getUrl(date: LocalDate, count: Int): String {
        if (count == 0) {
            throw RuntimeException("Too many failures.")
        }

        val dateText = date.format(formatter)

        logger.info("Trying to check ${dateText}")
        val url = "https://280blocker.net/files/280blocker_adblock_${dateText}.txt"
        val response = httpTransport.createRequestFactory()
            .buildHeadRequest(GenericUrl(url))
            .setThrowExceptionOnExecuteError(false)
            .execute()

        return when {
            response.isSuccessStatusCode -> url
            response.statusCode == 404 -> {
                logger.info("404. Tries the previous month. Count: ${count}")
                getUrl(date.minusMonths(1), count - 1)
            }
            else -> throw RuntimeException("Unexpected response: ${response.headers}")
        }
    }
}
