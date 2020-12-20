package com.livestreaming.channelize.io.model

import com.google.gson.annotations.SerializedName

data class MessageCommentData(

    @SerializedName("id")
    var id: String? = null,

    @SerializedName("type")
    var type: String = "normal",

    @SerializedName("body")
    var body: String? = null,

    @SerializedName("conversationId")
    var conversationId: String? = null,

    @SerializedName("userName")
    var userName: String? = null,

    @SerializedName("userImage")
    var userImage: String? = null

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MessageCommentData

        if (id != other.id) return false
        if (type != other.type) return false
        if (body != other.body) return false
        if (conversationId != other.conversationId) return false
        if (userName != other.userName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + type.hashCode()
        result = 31 * result + (body?.hashCode() ?: 0)
        result = 31 * result + (conversationId?.hashCode() ?: 0)
        result = 31 * result + (userName?.hashCode() ?: 0)
        return result
    }

}
