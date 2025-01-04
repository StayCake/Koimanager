package com.koisv.dkm.ktor

import com.koisv.dkm.DataManager.WSChat
import com.koisv.dkm.debug
import com.koisv.dkm.ktor.WSHandler.handle
import com.koisv.dkm.ktor.WSHandler.serverAlert
import com.koisv.dkm.ktor.WSHandler.sessionMap
import com.koisv.dkm.ktor.WSHandler.statusUpdate
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.DelicateCoroutinesApi
import org.apache.logging.log4j.LogManager
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.ExperimentalUuidApi

@ExperimentalUuidApi
@ExperimentalEncodingApi
@DelicateCoroutinesApi
fun Application.configureRouting() {
    val kcLogger = LogManager.getLogger("KTor-Server")
    install(WebSockets) {
        pingPeriod = 10.seconds
        timeoutMillis = 10000
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        webSocket("/") {
            val originIP =
                if (debug) call.request.origin.remoteAddress
                else call.request.headers["X-Real_IP"] ?: call.request.origin.remoteAddress
            if (!sessionMap.containsKey(this))
                sessionMap[this] = WSHandler.ChatSession(originIP, this)

            kcLogger.info("웹소켓 연결 됨 - {}", originIP)
            try {
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> this.handle(frame.readText())
                        else -> {
                            kcLogger.warn("Unsupported Frame Type: {}", frame)
                            outgoing.send(Frame.Text("Not Yet Implemented"))
                        }
                    }
                }
            }
            catch (e: CancellationException) {
                kcLogger.warn("웹소켓 연결 취소됨 - {}", originIP)
                e.printStackTrace()
            }
            catch (e: Exception) {
                kcLogger.error("웹소켓 연결 중 오류 발생 - {}", e.message)
                e.printStackTrace()
            }
            val remainOnline = WSChat.online.filter {
                it.userId == sessionMap[this]?.loggedInWith?.userId &&
                it.conType == sessionMap[this]?.loggedInWith?.conType
            }
            remainOnline.firstOrNull()?.let {
                serverAlert("[${it.conType.name}] ${it.nick ?: it.userId} 님이 로그아웃 했습니다.")
                WSChat.online.remove(it)
            }
            sessionMap[this]?.otpJob?.cancel()
            sessionMap.remove(this)
            statusUpdate()
            flush()
            kcLogger.info("웹소켓 연결 종료 - {}", originIP)
        }
    }
}