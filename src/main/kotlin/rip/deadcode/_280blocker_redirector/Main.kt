package rip.deadcode._280blocker_redirector

import org.eclipse.jetty.server.HttpConfiguration
import org.eclipse.jetty.server.HttpConnectionFactory
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector

object Main {
    @JvmStatic
    fun main(args: Array<String>) {

        val config = HttpConfiguration().also {
            it.sendServerVersion = false
        }
        val connectionFactory = HttpConnectionFactory(config)

        val port = args.firstOrNull()?.toIntOrNull() ?: System.getenv("PORT")?.toIntOrNull() ?: 8080
        Server()
            .also {
                it.connectors = arrayOf(ServerConnector(it, connectionFactory).also { it.port = port })
                it.handler = Handler()
            }
            .start()
    }
}
