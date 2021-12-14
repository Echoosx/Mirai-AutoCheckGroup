package org.echoosx.mirai.plugin

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.extension.PluginComponentStorage
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.*
import net.mamoe.mirai.utils.MiraiExperimentalApi
import net.mamoe.mirai.utils.info
import org.echoosx.mirai.plugin.command.AutoCheckGroupCommand
import org.echoosx.mirai.plugin.data.AutoCheckGroupData
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
            自动检测bot是否在白名单以外的群聊
            每次重启会自动将bot所在群更新至群名单
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

        val eventChannel = GlobalEventChannel.parentScope(this)
//        eventChannel.subscribeOnce<BotOnlineEvent> {
//            val groupCheckTimer = object : TimerTask(){
//                override fun run() {
//                    this@AutoCheckGroup.launch {
//                        try {
//                            var flag = false
//                            val msg = buildString {
//                                appendLine("检测到不在白名单中的群聊:")
//                                bot.groups.forEach() {
//                                    if((it.id !in AutoCheckGroupData.whiteGroup) && (it.id !in AutoCheckGroupData.outofWhite)) {
////                                    it.sendMessage("さようなら〜")
////                                    it.quit()
//                                        AutoCheckGroupData.outofWhite.add(it.id)
//                                        append("\n[${it.name}](${it.id})")
//                                        flag = true
//                                    }
//                                }
//                            }
//                            if(flag)
//                                bot.getFriendOrFail(AutoCheckGroupData.owner).sendMessage(msg)
//                        }catch (e:Exception){
//                            logger.error("自动检测群聊失败")
//                        }
//                    }
//                }
//            }
//            Timer().schedule(groupCheckTimer,Date(),2 * 60 * 1000)
//        }

        eventChannel.subscribeAlways<BotJoinGroupEvent.Invite> {
            if(AutoCheckGroupData.enable && group.id !in AutoCheckGroupData.whiteGroup){
                delay(5000)
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
        logger.info{"AutoCheckGroup plugin stopped"}
    }
}
