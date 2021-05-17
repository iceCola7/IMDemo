package com.cxz.imdemo.ui.im.conversation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.cxz.imdemo.R
import io.rong.imkit.conversation.ConversationFragment
import io.rong.imkit.conversation.MessageListAdapter

/**
 * @author chenxz
 * @date 2021/4/19
 * @desc 自定义私聊界面
 */
class CustomConversationFragment : ConversationFragment() {

    private var mView: View? = null

    private var mVoiceToggleBtn: ImageView? = null
    private var mEditText: EditText? = null
    private var mEmojiToggleBtn: ImageView? = null
    private var mAddBtn: ImageView? = null
    private var mTextBanTip: TextView? = null

    override fun onResolveAdapter(): MessageListAdapter {
        return CustomMessageListAdapter(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mView = view

        val panelView = mRongExtension.inputPanel.rootView
        panelView.apply {
            mEditText = mRongExtension.inputPanel.editText
            mVoiceToggleBtn = findViewById(R.id.input_panel_voice_toggle)
            mEmojiToggleBtn = findViewById(R.id.input_panel_emoji_btn)
            mAddBtn = findViewById(R.id.input_panel_add_btn)
            mTextBanTip = findViewById(R.id.input_text_ban_tip)
        }

        handleBanStatus(false)

        mRongExtensionViewModel.inputModeLiveData.observe(viewLifecycleOwner) {

        }
    }

    /**
     * 是否封禁
     * @param isBan Boolean
     */
    private fun handleBanStatus(isBan: Boolean) {
        mView?.postDelayed({
            mRongExtensionViewModel.collapseExtensionBoard()
            if (isBan) {
                mVoiceToggleBtn?.isEnabled = false
                mEmojiToggleBtn?.isEnabled = false
                mAddBtn?.isEnabled = false
                mEditText?.visibility = View.GONE
                mTextBanTip?.visibility = View.VISIBLE

                mVoiceToggleBtn?.setImageResource(R.mipmap.im_icon_voice_toggle_grey)
                mEmojiToggleBtn?.setImageResource(R.mipmap.im_icon_emotion_toggle_grey)
                mAddBtn?.setImageResource(R.mipmap.im_icon_plugin_toggle_grey)
            } else {
                mVoiceToggleBtn?.isEnabled = true
                mEmojiToggleBtn?.isEnabled = true
                mAddBtn?.isEnabled = true
                mEditText?.visibility = View.VISIBLE
                mTextBanTip?.visibility = View.GONE

                mVoiceToggleBtn?.setImageResource(R.mipmap.im_icon_voice_toggle)
                mEmojiToggleBtn?.setImageResource(R.mipmap.im_icon_emotion_toggle)
                mAddBtn?.setImageResource(R.mipmap.im_icon_plugin_toggle)
            }
        }, 0)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun handleAttentionStatus(attentionType: Int) {
        mView?.postDelayed({
            mRongExtensionViewModel.collapseExtensionBoard()
            if (attentionType != 3) {
                mVoiceToggleBtn?.setOnTouchListener { v, event ->
                    if (event.action == MotionEvent.ACTION_UP) {
                        return@setOnTouchListener true
                    }
                    return@setOnTouchListener false
                }
                mAddBtn?.setOnTouchListener { v, event ->
                    if (event.action == MotionEvent.ACTION_UP) {
                        return@setOnTouchListener true
                    }
                    return@setOnTouchListener false
                }
                mVoiceToggleBtn?.setImageResource(R.mipmap.im_icon_voice_toggle_grey)
                mAddBtn?.setImageResource(R.mipmap.im_icon_plugin_toggle_grey)
            } else {
                mVoiceToggleBtn?.setOnTouchListener { v, event ->
                    return@setOnTouchListener false
                }
                mAddBtn?.setOnTouchListener { v, event ->
                    return@setOnTouchListener false
                }
            }
        }, 0)
    }

    /**
     * 处理底部控件的可用状态
     * @param isEnable Boolean 是否可用
     */
    fun handleEnableStatus(isEnable: Boolean) {
        mView?.postDelayed({
            mRongExtensionViewModel.collapseExtensionBoard()
            if (isEnable) {
                mVoiceToggleBtn?.isEnabled = true
                mEmojiToggleBtn?.isEnabled = true
                mAddBtn?.isEnabled = true
                mEditText?.isEnabled = true
                mVoiceToggleBtn?.setImageResource(R.mipmap.im_icon_voice_toggle)
                mEmojiToggleBtn?.setImageResource(R.mipmap.im_icon_emotion_toggle)
                mAddBtn?.setImageResource(R.mipmap.im_icon_plugin_toggle)
            } else {
                mVoiceToggleBtn?.isEnabled = false
                //mEmojiToggleBtn?.isEnabled = false
                mAddBtn?.isEnabled = false
                //mEditText?.isEnabled = false

                mVoiceToggleBtn?.setImageResource(R.mipmap.im_icon_voice_toggle_grey)
                //mEmojiToggleBtn?.setImageResource(R.mipmap.im_icon_emotion_toggle_grey)
                mAddBtn?.setImageResource(R.mipmap.im_icon_plugin_toggle_grey)
            }
        }, 0)
    }

}