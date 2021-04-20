package com.cxz.imdemo.ui.plugin

import io.rong.imkit.conversation.extension.DefaultExtensionConfig
import io.rong.imkit.conversation.extension.component.plugin.FilePlugin
import io.rong.imkit.conversation.extension.component.plugin.IPluginModule
import io.rong.imkit.conversation.extension.component.plugin.ImagePlugin
import io.rong.imkit.feature.location.plugin.CombineLocationPlugin
import io.rong.imkit.feature.location.plugin.DefaultLocationPlugin
import io.rong.imlib.model.Conversation

/**
 * @author chenxz
 * @date 2021/4/19
 * @desc
 */

class MyExtensionConfig : DefaultExtensionConfig() {

    override fun getPluginModules(
        conversationType: Conversation.ConversationType?,
        targetId: String?
    ): MutableList<IPluginModule> {
        val pluginModules = super.getPluginModules(conversationType, targetId)
        val iterator = pluginModules.listIterator()
        // 删除扩展项
        while (iterator.hasNext()) {
            val integer = iterator.next()
            if (integer is FilePlugin
                || integer is ImagePlugin
                || integer is CombineLocationPlugin
                || integer is DefaultLocationPlugin
            ) {
                iterator.remove()
            }
        }
        // 增加扩展项
        pluginModules.add(MyShotPlugin())
        pluginModules.add(MyImagePlugin())
        return pluginModules
    }

}