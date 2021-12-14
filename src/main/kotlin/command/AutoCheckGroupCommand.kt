package org.echoosx.mirai.plugin.command

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.MessageEvent
import org.echoosx.mirai.plugin.AutoCheckGroup
import org.echoosx.mirai.plugin.data.AutoCheckGroupData

object AutoCheckGroupCommand: CompositeCommand(
    AutoCheckGroup,"gcheck",
    description = "白名单群聊"
) {
    private val config get() = AutoCheckGroupData

    @SubCommand("list")
    suspend fun CommandSender.list(){
        try {
            val msg = buildString {
                append("当前群聊列表:")
                bot!!.groups.forEach(){
                    if(it.id in config.whiteGroup)
                        append("\n[${it.name}](${it.id})")
                    else
                        append("\n[${it.name}](${it.id})[X]")
                }
            }
            sendMessage(msg)
        }catch (e:Exception){
            sendMessage("群聊列表获取失败")
        }
    }

    @SubCommand("wlist")
    suspend fun CommandSender.listwhite(){
        try{
            val msg = buildString {
                append("当前白名单列表:")
                config.whiteGroup.forEach(){
                    if(bot!!.groups.contains(it))
                        append("\n" + "[${bot!!.groups.get(it)!!.name}](${it})")
                    else
                        append("\n" + "[Unknown](${it})")
                }
            }
            sendMessage(msg)
        }catch(e:Exception){
            sendMessage("白名单列表获取失败")
        }
    }

    @SubCommand("quit")
    suspend fun CommandSender.quit(groupId:Long){
        if(bot!!.groups.contains(groupId)){
            try {
                val targetGroup = bot!!.getGroup(groupId)
                sendMessage(
                    "确定要退出群聊[${targetGroup?.name}](${groupId})吗？\n" +
                        "确定(1) / 取消(0)"
                )
                GlobalEventChannel.parentScope(this).subscribeOnce<MessageEvent> {
                    if(message.contentToString() == "1") {
                        targetGroup?.sendMessage("さようなら〜")
                        targetGroup?.quit()
                        config.whiteGroup.remove(groupId)
                        config.outofWhite.remove(groupId)
                        sendMessage("已退出群聊[${targetGroup?.name}](${groupId})")
                    }else
                        sendMessage("已取消")
                }
            }catch (e:Exception){
                sendMessage("退出群聊失败")
            }
        }
    }

    @SubCommand("add")
    suspend fun CommandSender.addwhite(groupId:Long){
        try {
            val targetGroup = bot!!.getGroup(groupId)
            if(!config.whiteGroup.contains(groupId))
                config.whiteGroup.add(groupId)
            sendMessage("已将群聊[${targetGroup?.name}](${groupId})加入白名单")
        }catch (e:Exception){
            sendMessage("加入白名单失败")
        }
    }

    @SubCommand("rm")
    suspend fun CommandSender.rmwhite(groupId:Long){
        try {
            val targetGroup = bot!!.getGroup(groupId)
            if(config.whiteGroup.contains(groupId)) {
                config.whiteGroup.remove(groupId)
                sendMessage("已将群聊[${targetGroup?.name}](${groupId})移出白名单")
            }else
                sendMessage("此群聊不在白名单内")
        }catch (e:Exception){
            sendMessage("移出白名单失败")
        }
    }

    @SubCommand("disable")
    suspend fun CommandSender.disable(){
        config.enable = false
        sendMessage("白名单已禁用")
    }

    @SubCommand("enable")
    suspend fun CommandSender.enable(){
        config.enable = true
        sendMessage("白名单已启用")
    }

    @SubCommand("status")
    suspend fun CommandSender.status(){
        val msg = if(config.enable) "白名单状态：启用" else "白名单状态：禁用"
        sendMessage(msg)
    }
}