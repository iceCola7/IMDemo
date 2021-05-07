package com.cxz.imdemo.manager

import android.content.Context
import android.view.View
import com.cxz.imdemo.ui.conversation.ConversationActivity
import com.cxz.imdemo.ui.conversationlist.ConversationListActivity
import com.cxz.imdemo.ui.message.CustomConversationProvider
import com.cxz.imdemo.ui.message.CustomMessageContent
import com.cxz.imdemo.ui.message.CustomMessageItemProvider
import com.cxz.imdemo.ui.plugin.MyExtensionConfig
import io.rong.imkit.RongIM
import io.rong.imkit.config.ConversationClickListener
import io.rong.imkit.config.ConversationListBehaviorListener
import io.rong.imkit.config.DataProcessor
import io.rong.imkit.config.RongConfigCenter
import io.rong.imkit.conversation.extension.RongExtensionManager
import io.rong.imkit.conversationlist.model.BaseUiConversation
import io.rong.imkit.conversationlist.provider.PrivateConversationProvider
import io.rong.imkit.utils.RouteUtils
import io.rong.imlib.RongIMClient
import io.rong.imlib.model.Conversation
import io.rong.imlib.model.Message
import io.rong.imlib.model.MessageContent
import io.rong.imlib.model.UserInfo
import io.rong.push.RongPushClient
import io.rong.push.pushconfig.PushConfig


/**
 * @author chenxz
 * @date 2021/4/9
 * @desc
 */
class RYManager {

    companion object {
        val instance: RYManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            return@lazy RYManager()
        }
    }

    fun init() {
        val config = PushConfig.Builder()
            .build()
        RongPushClient.setPushConfig(config)

        RouteUtils.registerActivity(
            RouteUtils.RongActivityType.ConversationListActivity,
            ConversationListActivity::class.java
        )
        RouteUtils.registerActivity(RouteUtils.RongActivityType.ConversationActivity, ConversationActivity::class.java)

        // 连接状态
        RongIM.setConnectionStatusListener {
        }

        // 注册自定义会话模板
        val providerManager = RongConfigCenter.conversationListConfig().providerManager
        providerManager.replaceProvider(PrivateConversationProvider::class.java, CustomConversationProvider())

        // 注册自定义消息
        val messageContentList = mutableListOf<Class<out MessageContent>>()
        messageContentList.add(CustomMessageContent::class.java)
        RongIMClient.registerMessageType(messageContentList)
        RongConfigCenter.conversationConfig().addMessageProvider(CustomMessageItemProvider())

        // 注册自定义的输入区配置
        RongExtensionManager.getInstance().extensionConfig = MyExtensionConfig()

        // 注册自定义表情
        // RongExtensionManager.getInstance().registerExtensionModule(MyExtensionModule())

        // 会话列表的数据过滤
        RongConfigCenter.conversationListConfig().dataProcessor = object : DataProcessor<Conversation> {
            /**
             * 设置会话列表页支持的会话类型
             * @return 所支持的会话类型
             */
            override fun supportedTypes(): Array<Conversation.ConversationType> {
                return arrayOf(
                    Conversation.ConversationType.PRIVATE,
                    Conversation.ConversationType.GROUP,
                    Conversation.ConversationType.SYSTEM,
                    Conversation.ConversationType.APP_PUBLIC_SERVICE,
                    Conversation.ConversationType.PUBLIC_SERVICE
                )
            }

            /**
             * 对会话数据进行过滤。
             * <p>从数据库批量拉取到会话列表时和在线收到消息产生新会话时都会回调此方法</p>
             * @param data 待过滤的数据
             * @return 过滤完的数据。
             */
            override fun filtered(data: MutableList<Conversation>): MutableList<Conversation> {
                return data
            }

            /**
             * 某一会话类型是否聚合状态显示。
             * @param type 会话类型
             * @return 该会话类型是否聚合显示。
             */
            override fun isGathered(type: Conversation.ConversationType): Boolean {
                if (type == Conversation.ConversationType.SYSTEM) {
                    // 以配置系统会话聚合显示为例
                    return true
                }
                return false
            }
        }
        // 会话列表的事件监听
        RongConfigCenter.conversationListConfig().setBehaviorListener(object : ConversationListBehaviorListener {
            /**
             * 会话头像点击监听
             *
             * @param context          上下文。
             * @param conversationType 会话类型。
             * @param targetId         被点击的用户id。
             * @return  true 拦截事件, false 执行融云 SDK 内部默认处理逻辑
             */
            override fun onConversationPortraitClick(
                context: Context?,
                conversationType: Conversation.ConversationType?,
                targetId: String?
            ): Boolean {
                return false
            }

            /**
             * 会话头像长按监听
             *
             * @param context          上下文。
             * @param conversationType 会话类型。
             * @param targetId         被点击的用户id。
             * @return true 拦截事件, false 执行融云 SDK 内部默认处理逻辑
             */
            override fun onConversationPortraitLongClick(
                context: Context?,
                conversationType: Conversation.ConversationType?,
                targetId: String?
            ): Boolean {
                return false
            }

            /**
             * 会话列表中的 Item 长按监听
             *
             * @param context      上下文。
             * @param view         触发点击的 View。
             * @param conversation 长按时的会话条目
             * @return true 拦截事件, false 执行融云 SDK 内部默认处理逻辑
             */
            override fun onConversationLongClick(
                context: Context?,
                view: View?,
                conversation: BaseUiConversation?
            ): Boolean {
                return false
            }

            /**
             * 会话列表中的 Item 点击监听
             *
             * @param context      上下文。
             * @param view         触发点击的 View。
             * @param conversation 长按时的会话条目
             * @return true 拦截事件, false 执行融云 SDK 内部默认处理逻辑
             */
            override fun onConversationClick(
                context: Context?,
                view: View?,
                conversation: BaseUiConversation?
            ): Boolean {
                return false
            }
        })
        // 会话界面的事件监听
        RongConfigCenter.conversationConfig().conversationClickListener = object : ConversationClickListener {
            /**
             * 用户头像点击事件
             *
             * @param context          上下文。
             * @param conversationType 会话类型。
             * @param user             被点击的用户的信息。
             * @param targetId         会话 id
             * @return true 拦截事件; false 不拦截, 默认执行 SDK 内部逻辑
             */
            override fun onUserPortraitClick(
                context: Context?,
                conversationType: Conversation.ConversationType?,
                user: UserInfo?,
                targetId: String?
            ): Boolean {
                return false
            }

            /**
             * 用户头像长按事件
             *
             * @param context          上下文。
             * @param conversationType 会话类型。
             * @param user             被点击的用户的信息。
             * @param targetId         会话 id
             * @return true 拦截事件; false 不拦截, 默认执行 SDK 内部逻辑
             */
            override fun onUserPortraitLongClick(
                context: Context?,
                conversationType: Conversation.ConversationType?,
                user: UserInfo?,
                targetId: String?
            ): Boolean {
                return false
            }

            /**
             * 消息点击事件
             *
             * @param context 上下文。
             * @param view    触发点击的 View。
             * @param message 被点击的消息的实体信息。
             * @return true 拦截事件; false 不拦截, 默认执行 SDK 内部逻辑
             */
            override fun onMessageClick(context: Context?, view: View?, message: Message?): Boolean {
                return false
            }

            /**
             * 消息长按事件
             *
             * @param context 上下文。
             * @param view    触发点击的 View。
             * @param message 被点击的消息的实体信息。
             * @return true 拦截事件; false 不拦截, 默认执行 SDK 内部逻辑
             */
            override fun onMessageLongClick(context: Context?, view: View?, message: Message?): Boolean {
                return false
            }

            /**
             * 消息超链接内容点击事件
             *
             * @param context 上下文。
             * @param link  超链接文本
             * @param message 被点击的消息的实体信息。
             * @return true 拦截事件; false 不拦截, 默认执行 SDK 内部逻辑
             */
            override fun onMessageLinkClick(context: Context?, link: String?, message: Message?): Boolean {
                return false
            }

            /**
             * 当点击已读回执状态时执行
             *
             * @param context 上下文。
             * @param message 被点击消息的实体信息。
             * @return 如果用户自己处理了长按后的逻辑处理，则返回 true，否则返回 false，false 走融云默认处理方式。
             */
            override fun onReadReceiptStateClick(context: Context?, message: Message?): Boolean {
                return false
            }
        }
    }

    fun connect(token: String) {
        RongIM.connect(token, object : RongIMClient.ConnectCallback() {
            override fun onSuccess(s: String?) {
            }

            override fun onError(erroCode: RongIMClient.ConnectionErrorCode?) {
            }

            override fun onDatabaseOpened(status: RongIMClient.DatabaseOpenStatus?) {
            }
        })
    }

}