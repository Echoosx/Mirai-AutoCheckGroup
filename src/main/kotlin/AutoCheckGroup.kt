package org.echoosx.mirai.plugin

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.extension.PluginComponentStorage
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.utils.ExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.MiraiExperimentalApi
import net.mamoe.mirai.utils.info
import org.echoosx.mirai.plugin.command.AutoCheckGroupCommand
import org.echoosx.mirai.plugin.command.BlockCommand
import org.echoosx.mirai.plugin.command.MuteCommand
import org.echoosx.mirai.plugin.data.AutoCheckGroupData
import java.io.File
import java.util.*

object AutoCheckGroup : KotlinPlugin(
    JvmPluginDescription(
        id = "org.echoosx.mirai.plugin.AutoCheckGroup",
        name = "AutoCheckGroup",
        version = "0.1.0"
    ) {
        author("Echoosx")
        info(
            """
            被拉进小型群聊后自动退出    
        """.trimIndent()
        )
    }
) {
    override fun PluginComponentStorage.onLoad() {
        GlobalEventChannel.subscribeOnce<BotOnlineEvent> {
            bot.groups.forEach(){
                if(it.id !in AutoCheckGroupData.whiteGroup)
                    AutoCheckGroupData.whiteGroup.add(it.id)
            }
            AutoCheckGroupData.outofWhite.clear()
        }
    }
    @OptIn(MiraiExperimentalApi::class)
    override fun onEnable() {
        AutoCheckGroupData.reload()
        AutoCheckGroupCommand.register()
        MuteCommand.register()
        BlockCommand.register()

        val eventChannel = GlobalEventChannel.parentScope(this)

        eventChannel.subscribeAlways<BotJoinGroupEvent.Invite> {
            if(AutoCheckGroupData.enable && group.id !in AutoCheckGroupData.whiteGroup){
                delay(5000)
                val image = group.uploadImage(AutoCheckGroup::class.java.classLoader.getResourceAsStream("pa.png")!!.toExternalResource())
                group.sendMessage(image)
                group.sendMessage("已自动退出，本群将在10s后爆炸")
                group.quit()
                bot.getFriendOrFail(AutoCheckGroupData.owner).sendMessage(
                    "被邀请加入群聊[${group.name}](${group.id})\n" +
                    "邀请人[${invitor.nick}](${invitor.id})\n" +
                    "已自动退出"
                )
            }
        }
        logger.info { "AutoCheckGroup plugin loaded" }
    }

    override fun onDisable(){
        AutoCheckGroupCommand.unregister()
        MuteCommand.unregister()
        BlockCommand.unregister()
        logger.info{"AutoCheckGroup plugin stopped"}
    }
}
