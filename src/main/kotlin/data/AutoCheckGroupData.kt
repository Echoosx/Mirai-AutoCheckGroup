package org.echoosx.mirai.plugin.data

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.ValueName
import net.mamoe.mirai.console.data.value

object AutoCheckGroupData: AutoSavePluginData("whitelist") {
    @ValueDescription("管理员")
    val owner:Long by value()

    @ValueDescription("群聊白名单")
    val whiteGroup:MutableList<Long> by value(arrayListOf())

    @ValueDescription(
        """
        检测到白名单外群聊的缓存
        避免向管理员重复发送信息
        """
    )
    val outofWhite:MutableList<Long> by value(arrayListOf())

    @ValueDescription("是否启用白名单")
    var enable:Boolean by value(true)
}
