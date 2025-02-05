@file:Suppress("SqlSourceToSinkFlow")

package com.koisv.dkm

import com.koisv.dkm.DataManager.Discord.dataCleanup
import com.koisv.dkm.DataManager.Discord.guildSave
import com.koisv.dkm.discord.Events
import com.koisv.dkm.discord.KoiManager.Companion.dBotInstance
import com.koisv.dkm.discord.KoiManager.Companion.dGDataList
import com.koisv.dkm.discord.KoiManager.Companion.kordLogger
import com.koisv.dkm.discord.data.Bot
import com.koisv.dkm.discord.data.GuildData
import com.koisv.dkm.irc.IRCChannelImpl.IRCChannel
import com.koisv.dkm.irc.IRCConfig
import com.koisv.dkm.irc.IRCConfig.IRCCredentials
import com.koisv.dkm.irc.IRCModeImpl
import com.koisv.dkm.ktor.WSHandler
import dev.kord.common.annotation.KordVoice
import dev.kord.common.entity.Snowflake
import io.ktor.server.websocket.*
import kotlinx.coroutines.*
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File
import java.io.IOException
import java.math.BigInteger
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.spec.X509EncodedKeySpec
import java.sql.*
import java.text.SimpleDateFormat
import java.time.ZoneOffset
import java.util.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.random.Random
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.uuid.ExperimentalUuidApi
import kotlinx.datetime.TimeZone as KTimeZone

@ExperimentalEncodingApi
@OptIn(ExperimentalSerializationApi::class)
object DataManager {
    const val SQL_BATCH_SIZE = 100

    private val dGTName: String = "discord_guilds" + if (debug) "_t" else ""
    private val sqlURL: String = "jdbc:mysql://" + (if (debug) "192.168.0.6" else "localhost") + ":3036/koimanager"
    private val jdbcTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREAN)

    val sqlCon: Connection = try {
        DriverManager.getConnection(sqlURL, if (debug) "km_test" else "km_main", "")
    } catch (e: Exception) { throw SQLException(e) }

    init { sqlCon.autoCommit = false }

    fun autoSave(): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            CoroutineScope(Dispatchers.IO).launch {
                while (this.isActive) {
                    delay(1.days)
                    dataCleanup(dGDataList)
                }
            }
            while (true) {
                delay(3.minutes)
                kordLogger.info("자동 저장 중...")
                try { guildSave() }
                catch (e: Exception) {
                    kordLogger.error("자동 저장 실패!")
                    e.printStackTrace()
                }
            }
        }
    }

    object WSChat {
        val online = mutableListOf<WSCUser>()

        enum class ConType { PC, MOBILE }

        data class WSCUser(
            val created: LocalDateTime,
            val userId: String,
            var nick: String?,
            var encKey: String,
            val conType: ConType,
            var lastLogin: LocalDateTime,
            var session: WebSocketServerSession? = null
        )

        fun saveMsgHistory(form: Triple<LocalDateTime, WSCUser, Pair<String, WSCUser?>>) {
            val tableName = if (debug) "wsc_history_t" else "wsc_history"
            val query = """
                INSERT INTO $tableName (time, sender_Id, content, recipient_id)
                VALUES (?, ?, ?, ?)
            """.trimIndent()

            sqlCon.prepareStatement(query).use { statement ->
                statement.setString(1, jdbcTimeFormat.format(Timestamp.valueOf(form.first.toJavaLocalDateTime())))
                statement.setString(2, form.second.userId)
                statement.setString(3, form.third.first)
                statement.setString(4, form.third.second?.userId)
                statement.executeUpdate()
                sqlCon.commit()
            }
        }

        const val MAX_HISTORY_LIMIT = 50

        @OptIn(ExperimentalUuidApi::class, DelicateCoroutinesApi::class)
        fun loadMsgHistory(): List<Triple<LocalDateTime, WSCUser?, Pair<String, WSCUser?>>> {
            val tableName = if (debug) "wsc_history_t" else "wsc_history"
            val query =
                """(SELECT * FROM $tableName 
                    WHERE RECIPIENT_ID IS NULL 
                    ORDER BY TIME DESC LIMIT ?) UNION ALL 
                    (SELECT * FROM $tableName 
                    WHERE RECIPIENT_ID IS NOT NULL 
                    ORDER BY TIME DESC LIMIT ?) ORDER BY TIME DESC""".trimMargin()
            val history = mutableListOf<Triple<LocalDateTime, WSCUser?, Pair<String, WSCUser?>>>()

            sqlCon.prepareStatement(query).use { statement ->
                statement.setInt(1, MAX_HISTORY_LIMIT)
                statement.setInt(2, MAX_HISTORY_LIMIT)
                statement.executeQuery().use { resultSet ->
                    while (resultSet.next()) {
                        val timestamp = resultSet.getString("time").let {
                            Instant.parse(it.replace(' ', 'T') + "Z")
                        }
                        val senderId = resultSet.getString("sender_id")
                        val message = resultSet.getString("content")
                        val receiverId = resultSet.getString("recipient_id")
                        history.add(Triple(
                            timestamp.toLocalDateTime(KTimeZone.UTC),
                            getWSCUser(senderId).firstOrNull() ?:
                            if (senderId == WSHandler.SERVER_MESSAGE_ID) WSHandler.server else null,
                            Pair(message, receiverId?.let { getWSCUser(it).firstOrNull() })
                        ))
                    }
                }
            }
            return history
        }


        fun saveWSCUser(user: WSCUser) {
            val tableName = if (debug) "wsc_users_t" else "wsc_users"
            val mobileKey = if (user.conType == ConType.MOBILE) "pKey_M" else "pKey_D"
            val query = """
                INSERT INTO $tableName (created, id, nickname, $mobileKey, lastLogin_date)
                VALUES (?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE 
                    nickname = VALUES(nickname),
                    $mobileKey = VALUES($mobileKey),
                    lastLogin_date = VALUES(lastLogin_date)
            """.trimIndent()

            sqlCon.prepareStatement(query).use { statement ->
                statement.setString(1, jdbcTimeFormat.format(Timestamp.valueOf(user.created.toJavaLocalDateTime())))
                statement.setString(2, user.userId)
                statement.setString(3, user.nick)
                statement.setString(4, user.encKey)
                statement.setString(5, jdbcTimeFormat.format(Timestamp.valueOf(user.lastLogin.toJavaLocalDateTime())))

                statement.executeUpdate()
                sqlCon.commit()
            }
        }

        fun deleteWSCUser(userId: String) {
            val tableName = if (debug) "wsc_users_t" else "wsc_users"
            val query = "DELETE FROM $tableName WHERE id = ?"

            sqlCon.prepareStatement(query).use { statement ->
                statement.setString(1, userId)
                statement.executeUpdate()
                sqlCon.commit()
            }
        }

        fun getWSCUser(userId: String): ArrayList<WSCUser> {
            val tableName = if (debug) "wsc_users_t" else "wsc_users"
            val query = "SELECT * FROM $tableName WHERE id = ?"

            sqlCon.prepareStatement(query).use { statement ->
                statement.setString(1, userId)
                statement.executeQuery().use { resultSet ->
                    if (resultSet.next()) {
                        val created = resultSet.getString("created").let {
                            LocalDateTime.parse(it.replace(' ', 'T'))
                        }
                        val id = resultSet.getString("id")
                        val nickname = resultSet.getString("nickname")
                        val pKeyM = resultSet.getString("pKey_M")
                        val pKeyD = resultSet.getString("pKey_D")
                        val lastLogin = resultSet.getString("lastLogin_date").let {
                            LocalDateTime.parse(it.replace(' ', 'T'))
                        }
                        val mobileUser = if (pKeyM != null) {
                            WSCUser(created, id, nickname, pKeyM, ConType.MOBILE, lastLogin)
                        } else null

                        val pcUser = if (pKeyD != null) {
                            WSCUser(created, id, nickname, pKeyD, ConType.PC, lastLogin)
                        } else null

                        val result = arrayListOf<WSCUser>()
                        result.addAll(listOfNotNull(mobileUser, pcUser))

                        return result
                    }
                    return arrayListOf()
                }
            }
        }
    }

    object IRC {
        private val ircJson = Json { useArrayPolymorphism = true }
        private val settingFile = File("./IRCconfig.json")
        val motdFile = File("./IRCmotd.txt")
        val ircOps = loadIRCOps()

        private const val QUERY_IRC_OPS = "SELECT * FROM irc_ops"
        private const val QUERY_IRC_CHANNELS = "SELECT * FROM irc_channels"

        fun loadConfig(): IRCConfig {
            return if (!settingFile.exists()) {
                settingFile.createNewFile()
                throw IOException("IRC 설정 파일[config/IRCConfig.json]이 없습니다.")
            } else {
                settingFile.inputStream().use { inputStream ->
                    ircJson.decodeFromStream<IRCConfig>(inputStream)
                }
            }
        }

        fun loadIRCOps(): List<IRCCredentials> {
            return executeQuery(QUERY_IRC_OPS) { results ->
                IRCCredentials(results.getString(1), results.getString(2))
            }
        }

        fun loadIRCChans(): List<IRCChannel> {
            return executeQuery(QUERY_IRC_CHANNELS) { results ->
                IRCChannel(
                    results.getString(1),
                    results.getString(2),
                    IRCModeImpl.ModeSet().apply {
                        results.getString(3).forEach {
                            IRCModeImpl.ChannelMode.get(it.toString())?.let { mode -> addMode(mode) }
                        }
                    }
                )
            }
        }

        private fun <T> executeQuery(query: String, mapper: (ResultSet) -> T): List<T> {
            val allItems = mutableListOf<T>()
            sqlCon.createStatement().use { statement ->
                statement.executeQuery(query).use { results ->
                    while (results.next()) {
                        allItems.add(mapper(results))
                    }
                }
            }
            return allItems
        }
    }

    object Discord {
        @OptIn(KordVoice::class)
        fun parseDGuilds(): List<GuildData> {
            val query = "SELECT * FROM $dGTName"
            val prepare = sqlCon.createStatement()
            val results = prepare.executeQuery(query)

            val data = arrayListOf<GuildData>()

            while (results.next()) {
                val channels = mutableMapOf<Events.NotifyChannel, Snowflake?>()

                val name = results.getString(1)
                val id = Snowflake(results.getLong(2))
                val volume = results.getInt(3)
                val shuffle = results.getBoolean(4)
                val repeat = GuildData.RepeatType.entries[results.getInt(5)]
                val notify = results.getLong(6)
                if (!results.wasNull()) channels[Events.NotifyChannel.Default] = Snowflake(notify)
                val update = results.getLong(7)
                if (!results.wasNull()) channels[Events.NotifyChannel.Update] = Snowflake(update)

                data.plus(GuildData(name, id, volume, shuffle, repeat, channels))
            }
            results.close()
            prepare.close()

            return data
        }

        fun updateDGuilds(data: List<GuildData>) {
            val query = """
                INSERT INTO $dGTName (name, id, volume, shuffle, `repeat`, notify_channel, update_channel)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    name = VALUES(name),
                    volume = VALUES(volume),
                    shuffle = VALUES(shuffle),
                    `repeat` = VALUES(`repeat`),
                    notify_channel = VALUES(notify_channel),
                    update_channel = VALUES(update_channel)
            """.trimIndent()

            sqlCon.prepareStatement(query).use { statement ->
                data.forEachIndexed { idx, guild ->
                    statement.setString(1, guild.name)
                    statement.setLong(2, guild.id.value.toLong())
                    statement.setInt(3, guild.volume)
                    statement.setBoolean(4, guild.shuffle)
                    statement.setInt(5, guild.repeat.ordinal)
                    guild.channels[Events.NotifyChannel.Default]?.let { statement.setLong(6, it.value.toLong()) }
                        ?: statement.setNull(6, Types.BIGINT)
                    guild.channels[Events.NotifyChannel.Update]?.let { statement.setLong(7, it.value.toLong()) }
                        ?: statement.setNull(7, Types.BIGINT)

                    statement.addBatch()

                    if ((idx + 1) % SQL_BATCH_SIZE == 0) {
                        statement.executeBatch()
                        sqlCon.commit()
                    }
                }
                statement.executeBatch()
                sqlCon.commit()
            }
        }

        fun botLoad() : List<Bot> {
            val allBots = arrayListOf<Bot>()

            val query = "SELECT * FROM discord_bots"
            val prepare = sqlCon.createStatement()
            val results = prepare.executeQuery(query)

            while (results.next()) {
                val bot = Bot(
                    Bot.Tokens(results.getString(3), results.getString(4)),
                    Bot.Type.entries[results.getInt(2)]
                )

                val presence = results.getString(5)
                if (results.wasNull()) bot.presence = presence
                val debugGuild = results.getLong(6)
                if (results.wasNull()) bot.debugGuild = Snowflake(debugGuild)

                allBots.add(bot)
            }

            return allBots
        }

        fun guildSave() {
            updateDGuilds(dGDataList)
            kordLogger.info("저장 완료.")
        }

        suspend fun dataCleanup(list: MutableList<GuildData>) {
            dBotInstance.guilds.collect { list.removeIf { data -> list.none { data.id == it.id } } }
        }
    }

    fun String.hash(): String {
        val md = MessageDigest.getInstance("SHA-512")
        val messageDigest = md.digest(toByteArray())

        val no = BigInteger(1, messageDigest)
        var hashText = no.toString(16)
        while (hashText.length < 32) hashText = "0$hashText"

        return hashText
    }

    fun String.decryptWithRSA(pubKey: String): String? {
        val rawKey = Base64.decode(pubKey)
        val spec = X509EncodedKeySpec(rawKey)
        val fact = KeyFactory.getInstance("RSA")
        val pubKey = fact.generatePublic(spec)

        try {
            val cipher = Cipher.getInstance("RSA")
            cipher.init(Cipher.DECRYPT_MODE, pubKey)
            return String(cipher.doFinal(Base64.decode(this)))
        } catch (_: BadPaddingException) {
            return null
        }
    }

    fun String.encryptWithRSA(pubKey: String): String? {
        val rawKey = Base64.decode(pubKey)
        val spec = X509EncodedKeySpec(rawKey)
        val fact = KeyFactory.getInstance("RSA")
        val prvKey = fact.generatePublic(spec)

        return try {
            val cipher = Cipher.getInstance("RSA")
            cipher.init(Cipher.ENCRYPT_MODE, prvKey)
            Base64.encode(cipher.doFinal(this.toByteArray()))
        } catch (_: BadPaddingException) {
            null
        }
    }

    fun String.compressEncRSA(pubKey: String): String? {
        val password = passcodeGen(16)
        val (secretKeySpec, gcmParamSpec) = getSpec(password)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmParamSpec)

        val encryptedValue = cipher.doFinal(this.toByteArray())

        return ("%%${password.encryptWithRSA(pubKey)}~" + Base64.encode(encryptedValue))
    }

    fun String.compressDecRSA(pubKey: String): String? {
        if (!this.startsWith("%%")) return null
        val data = this.substringAfter('~')
        val password = this.substringAfter("%%").substringBefore('~')
            .decryptWithRSA(pubKey) ?: return null

        val (secretKeySpec, gcmParamSpec) = getSpec(password)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmParamSpec)

        val decryptedByteValue = cipher.doFinal(Base64.decode(data))
        return String(decryptedByteValue)
    }

    fun getSpec(password: String): Pair<SecretKeySpec, GCMParameterSpec> {
        val secretKeySpec = SecretKeySpec(password.toByteArray(), "AES")
        val iv = ByteArray(16)
        val charArray = password.toCharArray()
        for (i in 0 until charArray.size) {
            iv[i] = charArray[i].code.toByte()
        }
        val gcmParamSpec = GCMParameterSpec(128, iv)
        return Pair(secretKeySpec, gcmParamSpec)
    }

    fun passcodeGen(len: Int): String {
        val alphabet: List<Char> = ('가'..'힣').toList()
        val pre = buildString {
            for (i in 0 until (len-1)/3) {
                append(alphabet.random())
            }
        }
        val rand = Random(java.time.LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
        return pre.toMutableList()
            .apply { add(rand.nextInt(0, pre.length + 1), "${rand.nextInt(0, 9)}".toCharArray()[0]) }
            .joinToString("")
    }
}