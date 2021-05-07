package com.cxz.imdemo.ui.message

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.view.ViewGroup
import com.cxz.imdemo.R
import io.rong.imkit.conversation.messgelist.provider.BaseMessageItemProvider
import io.rong.imkit.model.UiMessage
import io.rong.imkit.widget.adapter.IViewProviderListener
import io.rong.imkit.widget.adapter.ViewHolder
import io.rong.imlib.model.MessageContent

/**
 * @author chenxz
 * @date 2021/4/19
 * @desc
 */
class CustomMessageItemProvider : BaseMessageItemProvider<CustomMessageContent>() {

    init {
        // 修改模板属性
        mConfig.showProgress = false
        mConfig.showReadState = false
        mConfig.showPortrait = true
        mConfig.centerInHorizontal = true
        mConfig.showWarning = false
        mConfig.showSummaryWithName = false
        mConfig.showContentBubble = true
    }

    /**
     * 创建 ViewHolder
     * @param parent 父 ViewGroup
     * @param viewType 视图类型
     * @return ViewHolder
     */
    override fun onCreateMessageContentViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder? {
        return ViewHolder.createViewHolder(parent?.context, parent, R.layout.item_custom_message_list)
    }

    /**
     * 设置消息视图里各 view 的值
     * @param holder ViewHolder
     * @param parentHolder 父布局的 ViewHolder
     * @param customMessageContent 此展示模板对应的消息
     * @param uiMessage [UiMessage]
     * @param position 消息位置
     * @param list 列表
     * @param listener ViewModel 的点击事件监听器。如果某个子 view 的点击事件需要 ViewModel 处理，可通过此监听器回调。
     */
    override fun bindMessageContentViewHolder(
        holder: ViewHolder?,
        parentHolder: ViewHolder?,
        customMessageContent: CustomMessageContent?,
        uiMessage: UiMessage?,
        position: Int,
        list: List<UiMessage?>?,
        listener: IViewProviderListener<UiMessage?>?
    ) {
    }

    /**
     * @param holder ViewHolder
     * @param customMessageContent 自定义消息
     * @param uiMessage [UiMessage]
     * @param position 位置
     * @param list 列表数据
     * @param listener ViewModel 的点击事件监听器。如果某个子 view 的点击事件需要 ViewModel 处理，可通过此监听器回调。
     * @return 点击事件是否被消费
     */
    override fun onItemClick(
        holder: ViewHolder?,
        customMessageContent: CustomMessageContent?,
        uiMessage: UiMessage?,
        position: Int,
        list: List<UiMessage?>?,
        listener: IViewProviderListener<UiMessage?>?
    ): Boolean {
        return false
    }

    /**
     * 根据消息内容，判断是否为本模板需要展示的消息类型
     *
     * @param customMessageContent 消息内容
     * @return 本模板是否处理。
     */
    override fun isMessageViewType(customMessageContent: MessageContent?): Boolean {
        return false
    }

    /**
     * 在会话列表页某条会话最后一条消息为该类型消息时，会话里需要展示的内容。
     * 比如: 图片消息在会话里需要展示为"图片"，那返回对应的字符串资源即可。
     * @param context 上下文
     * @param customMessageContent 消息内容
     * @return 会话里需要展示的字符串资源
     */
    override fun getSummarySpannable(context: Context?, customMessageContent: CustomMessageContent?): Spannable? {
        return SpannableString(customMessageContent?.content)
    }
}