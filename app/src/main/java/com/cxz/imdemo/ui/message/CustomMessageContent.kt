package com.cxz.imdemo.ui.message

import android.os.Parcel
import android.os.Parcelable
import io.rong.common.ParcelUtils
import io.rong.imlib.DestructionTag
import io.rong.imlib.MessageTag
import io.rong.imlib.model.MessageContent
import org.json.JSONObject
import java.nio.charset.Charset

/**
 * @author chenxz
 * @date 2021/4/19
 * @desc 自定义消息
 */
@MessageTag(value = "RC:CUSTOM_MESSAGE", flag = MessageTag.ISCOUNTED or MessageTag.ISPERSISTED)
@DestructionTag
class CustomMessageContent : MessageContent {

    var content: String = ""
    var extra: String = ""

    constructor()

    constructor(content: String, extra: String) {
        this.content = content
        this.extra = extra
    }

    constructor(data: ByteArray) {
        val jsonStr = data.toString(Charset.defaultCharset())
        val jsonObj = JSONObject(jsonStr)
        if (jsonObj.has("content")) {
            this.content = jsonObj.getString("content")
        }
        if (jsonObj.has("extra")) {
            this.extra = jsonObj.getString("extra")
        }
        if (jsonObj.has("isBurnAfterRead")) {
            this.isDestruct = jsonObj.getBoolean("isBurnAfterRead")
        }
        if (jsonObj.has("burnDuration")) {
            this.destructTime = jsonObj.getLong("burnDuration")
        }
    }

    constructor(parcel: Parcel) : this() {
        content = ParcelUtils.readFromParcel(parcel)
        extra = ParcelUtils.readFromParcel(parcel)
        isDestruct = ParcelUtils.readIntFromParcel(parcel) == 1
        destructTime = ParcelUtils.readLongFromParcel(parcel)
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        ParcelUtils.writeToParcel(dest, content)
        ParcelUtils.writeToParcel(dest, extra)
        ParcelUtils.writeToParcel(dest, if (isDestruct) 1 else 0)
        ParcelUtils.writeToParcel(dest, destructTime)
    }

    override fun encode(): ByteArray {
        val jsonObj = JSONObject()
        jsonObj.put("content", content)
        jsonObj.put("extra", extra)
        jsonObj.put("isBurnAfterRead", isDestruct)
        jsonObj.put("burnDuration", destructTime)
        return jsonObj.toString().toByteArray(charset("UTF-8"))
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CustomMessageContent> {
        override fun createFromParcel(parcel: Parcel): CustomMessageContent {
            return CustomMessageContent(parcel)
        }

        override fun newArray(size: Int): Array<CustomMessageContent?> {
            return arrayOfNulls(size)
        }
    }
}