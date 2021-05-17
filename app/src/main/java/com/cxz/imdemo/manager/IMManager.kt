package com.cxz.imdemo.manager

import android.app.Application
import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import com.cxz.imdemo.ui.im.conversation.ConversationActivity
import com.cxz.imdemo.ui.im.conversationlist.ConversationListActivity
import com.cxz.imdemo.ui.im.message.CustomConversationProvider
import com.cxz.imdemo.ui.im.message.CustomMessageContent
import com.cxz.imdemo.ui.im.message.CustomMessageItemProvider
import com.cxz.imdemo.ui.im.message.CustomPrivateConversationProvider
import com.cxz.imdemo.ui.im.plugin.MyExtensionConfig
import io.rong.imkit.IMCenter
import io.rong.imkit.config.ConversationClickListener
import io.rong.imkit.config.ConversationListBehaviorListener
import io.rong.imkit.config.DataProcessor
import io.rong.imkit.config.RongConfigCenter
import io.rong.imkit.conversation.extension.RongExtensionManager
import io.rong.imkit.conversationlist.model.BaseUiConversation
import io.rong.imkit.conversationlist.provider.PrivateConversationProvider
import io.rong.imkit.feature.mention.IExtensionEventWatcher
import io.rong.imkit.userinfo.RongUserInfoManager
import io.rong.imkit.userinfo.UserDataProvider
import io.rong.imkit.userinfo.model.GroupUserInfo
import io.rong.imkit.utils.RouteUtils
import io.rong.imlib.IRongCoreEnum
import io.rong.imlib.RongIMClient
import io.rong.imlib.chatroom.base.RongChatRoomClient
import io.rong.imlib.model.*
import io.rong.push.PushEventListener
import io.rong.push.PushType
import io.rong.push.RongPushClient
import io.rong.push.notification.PushNotificationMessage
import io.rong.push.pushconfig.PushConfig

/**
 * @author chenxz
 * @date 2021/4/9
 * @desc IM 管理类
 */
class IMManager private constructor() {

    private lateinit var context: Context

    private val appKey = "3argexb63f7he"

    companion object {
        val instance: IMManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            return@lazy IMManager()
        }
    }

    var kickedOffline = MutableLiveData<Boolean>()

    var messageRouter = MutableLiveData<Message>()

    fun init(application: Application) {
        this.context = application.applicationContext

        // 初始化推送
        initPush()

        initPhrase()

        // 调用 RongIM 初始化
        initRongIM(application)

        // IM 配置
        initIMConfig()

        // 初始化用户和群组信息内容提供者
        initInfoProvider(context)

        // 初始化自定义消息和消息模版
        initMessageAndTemplate()

        // 初始化扩展模块
        initExtensionModules(context)

        // 初始化连接状态变化监听
        initConnectStateChangeListener()

        // 初始化消息监听
        initOnReceiveMessage(context)

        // 初始化聊天室监听
        initChatRoomActionListener()

        // 缓存连接
        cacheConnectIM()

        RongExtensionManager.getInstance().addExtensionEventWatcher(object : IExtensionEventWatcher {
            override fun onTextChanged(
                context: Context?,
                type: Conversation.ConversationType?,
                targetId: String?,
                cursorPos: Int,
                count: Int,
                text: String?
            ) {
            }

            override fun onSendToggleClick(message: Message?) {
            }

            override fun onDeleteClick(
                type: Conversation.ConversationType?,
                targetId: String?,
                editText: EditText?,
                cursorPos: Int
            ) {
            }

            override fun onDestroy(type: Conversation.ConversationType?, targetId: String?) {
            }
        })

    }

    /**
     * 缓存登录
     */
    private fun cacheConnectIM() {
        if (RongIMClient.getInstance().currentConnectionStatus == RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED) {
            return
        }
        // 获取缓存的用户信息

        // 获取融云token
        val rongToken = ""
        // 连接融云
        connectIM(rongToken)
    }

    /**
     * 初始化推送
     */
    private fun initPush() {
        val config = PushConfig.Builder()
            .build()
        RongPushClient.setPushConfig(config)
        RongPushClient.setPushEventListener(object : PushEventListener {
            override fun preNotificationMessageArrived(
                context: Context?,
                pushType: PushType?,
                pushNotificationMessage: PushNotificationMessage?
            ): Boolean {
                return false
            }

            override fun afterNotificationMessageArrived(p0: Context?, p1: PushType?, p2: PushNotificationMessage?) {
            }

            override fun onNotificationMessageClicked(
                context: Context?,
                pushType: PushType?,
                pushNotificationMessage: PushNotificationMessage?
            ): Boolean {
                return false
            }

            override fun onThirdPartyPushState(pushType: PushType?, action: String?, resultCode: Long) {
            }
        })
    }

    private fun initPhrase() {
        val phraseList = mutableListOf<String>()
        RongConfigCenter.featureConfig().enableQuickReply {
            return@enableQuickReply phraseList
        }
    }

    /**
     * 调用初始化 IM
     * @param application Application
     */
    private fun initRongIM(application: Application) {
        /*
         * 如果是连接到私有云需要在此配置服务器地址
         * 如果是公有云则不需要调用此方法
         */
        // RongIMClient.setServerInfo(BuildConfig.SEALTALK_NAVI_SERVER, BuildConfig.SEALTALK_FILE_SERVER)

        // 初始化 SDK，在整个应用程序全局，只需要调用一次。建议在 Application 继承类中调用。
        IMCenter.init(application, appKey, true)
    }

    /**
     * IM 配置
     */
    private fun initIMConfig() {
        // 将私聊，群组加入消息已读回执
        val types = arrayOf(
            Conversation.ConversationType.PRIVATE,
            Conversation.ConversationType.GROUP,
            Conversation.ConversationType.ENCRYPTED
        )
        RongConfigCenter.featureConfig().enableReadReceipt(*types)

        // 配置会话列表界面相关内容
        initConversationList()
        // 配置会话界面
        initConversation()

        RongConfigCenter.featureConfig().enableDestruct(true)
    }

    /**
     * 配置会话列表界面相关内容
     */
    private fun initConversationList() {
        RouteUtils.registerActivity(
            RouteUtils.RongActivityType.ConversationListActivity,
            ConversationListActivity::class.java
        )
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
    }

    /**
     * 配置会话界面
     */
    private fun initConversation() {
        RouteUtils.registerActivity(RouteUtils.RongActivityType.ConversationActivity, ConversationActivity::class.java)
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

        // RongConfigCenter.gatheredConversationConfig().setConversationTitle(Conversation.ConversationType.SYSTEM, R.string.seal_conversation_title_system)
    }

    /**
     * 初始化用户和群组信息内容提供者
     * @param context Context
     */
    private fun initInfoProvider(context: Context) {
        // 获取用户信息
        RongUserInfoManager.getInstance().setUserInfoProvider(UserDataProvider.UserInfoProvider { id ->
            return@UserInfoProvider null
        }, true)

        // 获取群组信息
        RongUserInfoManager.getInstance().setGroupInfoProvider(UserDataProvider.GroupInfoProvider { groupId ->
            return@GroupInfoProvider null
        }, true)
    }

    /**
     * 更新 IMKit 显示的用户信息
     * @param userId String
     * @param userName String
     * @param portraitUri String
     */
    private fun updateUserInfoCache(userId: String, userName: String, portraitUri: Uri) {
        val oldUserInfo = RongUserInfoManager.getInstance().getUserInfo(userId)
        if (oldUserInfo == null ||
            (oldUserInfo.name != userName || oldUserInfo.portraitUri == null || oldUserInfo.portraitUri != portraitUri)
        ) {
            val userInfo = UserInfo(userId, userName, portraitUri)
            RongUserInfoManager.getInstance().refreshUserInfoCache(userInfo)
        }
    }

    /**
     * 更新 IMKit 显示用群组信息
     * @param groupId String
     * @param groupName String
     * @param portraitUri Uri
     */
    private fun updateGroupInfoCache(groupId: String, groupName: String, portraitUri: Uri) {
        val oldGroupInfo = RongUserInfoManager.getInstance().getGroupInfo(groupId)
        if (oldGroupInfo == null ||
            (oldGroupInfo.name != groupName || oldGroupInfo.portraitUri == null || oldGroupInfo.portraitUri != portraitUri)
        ) {
            val groupInfo = Group(groupId, groupName, portraitUri)
            RongUserInfoManager.getInstance().refreshGroupInfoCache(groupInfo)
        }
    }

    /**
     * 更新 IMKit 显示用群组成员信息
     * @param groupId String
     * @param userId String
     * @param nickName String
     */
    fun updateGroupMemberInfoCache(groupId: String, userId: String, nickName: String) {
        val oldGroupUserInfo = RongUserInfoManager.getInstance().getGroupUserInfo(groupId, userId)
        if (oldGroupUserInfo == null || oldGroupUserInfo.nickname != nickName) {
            val groupMemberInfo = GroupUserInfo(groupId, userId, nickName)
            RongUserInfoManager.getInstance().refreshGroupUserInfoCache(groupMemberInfo)
        }
    }

    /**
     * 初始化自定义消息和消息模版
     */
    private fun initMessageAndTemplate() {
        // 注册自定义会话模板
        RongConfigCenter.conversationListConfig().providerManager.apply {
            replaceProvider(PrivateConversationProvider::class.java, CustomConversationProvider())
            replaceProvider(PrivateConversationProvider::class.java, CustomPrivateConversationProvider())
        }
        // 注册自定义消息
        val messageContentList = mutableListOf<Class<out MessageContent>>()
        messageContentList.add(CustomMessageContent::class.java)
        RongIMClient.registerMessageType(messageContentList)
        RongConfigCenter.conversationConfig().apply {
            addMessageProvider(CustomMessageItemProvider())
        }
    }

    /**
     * 初始化扩展模块
     * @param context Context
     */
    private fun initExtensionModules(context: Context) {
        // 注册自定义的输入区配置
        RongExtensionManager.getInstance().extensionConfig = MyExtensionConfig()
        // 注册自定义表情
        // RongExtensionManager.getInstance().registerExtensionModule(MyExtensionModule())
    }

    /**
     * 初始化连接状态变化监听
     */
    private fun initConnectStateChangeListener() {
        // 连接状态
        IMCenter.getInstance().addConnectionStatusListener {
            when (it) {
                RongIMClient.ConnectionStatusListener.ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT -> {
                    // 被踢出时
                    kickedOffline.postValue(true)
                }
                RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED -> {
                    // 连接成功
                }
            }
        }
    }

    /**
     * 初始化消息监听
     * @param context Context
     */
    private fun initOnReceiveMessage(context: Context) {
        IMCenter.getInstance().addOnReceiveMessageListener(object : RongIMClient.OnReceiveMessageWrapperListener() {
            override fun onReceived(message: Message?, left: Int, hasPackage: Boolean, isOffline: Boolean): Boolean {
                messageRouter.postValue(message)
                return false
            }
        })
    }

    /**
     * 初始化聊天室监听
     */
    private fun initChatRoomActionListener() {
        RongChatRoomClient.setChatRoomAdvancedActionListener(object :
            RongChatRoomClient.ChatRoomAdvancedActionListener {
            override fun onJoining(roomId: String?) {
            }

            override fun onJoined(roomId: String?) {
            }

            override fun onReset(roomId: String?) {
            }

            override fun onQuited(roomId: String?) {
            }

            override fun onDestroyed(roomId: String?, type: IRongCoreEnum.ChatRoomDestroyType?) {
            }

            override fun onError(roomId: String?, errorCode: IRongCoreEnum.CoreErrorCode?) {
            }
        })
    }

    /**
     * 连接 IM 服务
     * @param token String
     * @param autoConnect Boolean 是否在连接失败时无限时间自动重连
     * @param success Function1<String, Unit>?
     * @param errorCallback Function0<Unit>?
     */
    fun connectIM(
        token: String,
        autoConnect: Boolean = true,
        success: ((String) -> Unit)? = null,
        errorCallback: (() -> Unit)? = null
    ) {
        if (autoConnect) {
            connectIM(token, 0, success, errorCallback)
        } else {
            connectIM(token, 10, success, errorCallback)
        }
    }

    /**
     * 连接 IM 服务
     * @param token String
     * @param timeOut Int 自动重连超时时间 单位秒
     * @param success Function1<String, Unit>?
     * @param errorCallback Function0<Unit>?
     */
    private fun connectIM(
        token: String,
        timeOut: Int,
        success: ((String) -> Unit)? = null,
        errorCallback: (() -> Unit)? = null
    ) {
        IMCenter.getInstance().connect(token, timeOut, object : RongIMClient.ConnectCallback() {
            override fun onSuccess(s: String?) {
                success?.invoke(s ?: "")
            }

            override fun onError(erroCode: RongIMClient.ConnectionErrorCode) {
                errorCallback?.invoke()
            }

            override fun onDatabaseOpened(status: RongIMClient.DatabaseOpenStatus?) {
            }
        })
    }

    /**
     * 退出
     */
    fun logout() {
        IMCenter.getInstance().disconnect()
    }

}