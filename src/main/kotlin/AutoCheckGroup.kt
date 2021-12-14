package org.echoosx.mirai.plugin

import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.EventChannel
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.NewFriendRequestEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.utils.info

/**
 * 使用 kotlin 版请把
 * `src/main/resources/META-INF.services/net.mamoe.mirai.console.plugin.jvm.JvmPlugin`
 * 文件内容改成 `org.example.mirai.plugin.PluginMain` 也就是当前主类全类名
 *
 * 使用 kotlin 可以把 java 源集删除不会对项目有影响
 *
 * 在 `settings.gradle.kts` 里改构建的插件名称、依赖库和插件版本
 *
 * 在该示例下的 [JvmPluginDescription] 修改插件名称，id和版本，etc
 *
 * 可以使用 `src/test/kotlin/RunMirai.kt` 在 ide 里直接调试，
 * 不用复制到 mirai-console-loader 或其他启动器中调试
 */

object AutoCheckGroup : KotlinPlugin(
    JvmPluginDescription(
        id = "org.echoosx.mirai.plugin.AutoCheckGroup",
        name = "AutoCheckGroup",
        version = "0.1.0"
    ) {
        author("Echoosx")
        info(
            """
            自动检测bot是否在白名单以外的群聊
            每次重启会自动将bot所在群更新至群名单
        """.trimIndent()
        )
    }
) {
    override fun onEnable() {
        val eventChannel = GlobalEventChannel.parentScope(this)
        logger.info { "AutoCheckGroup plugin loaded" }
    }

    override fun onDisable(){
        logger.info{"AutoCheckGroup plugin stopped"}
    }
}
