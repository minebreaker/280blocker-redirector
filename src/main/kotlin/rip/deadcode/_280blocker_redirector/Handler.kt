package rip.deadcode._280blocker_redirector

import com.google.common.net.HttpHeaders
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler
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
        val dateText = now.format(DateTimeFormatter.ofPattern("uuuuMM"))
        val url = "https://280blocker.net/files/280blocker_adblock_${dateText}.txt"

        response.status = 307
        response.addHeader("Location", url)
//        response.addHeader("Cache-Control", "max-age=604800")
        response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
        baseRequest.isHandled = true
    }
}
