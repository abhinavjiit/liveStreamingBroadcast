package com.livestreaming.channelize.io.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.livestreaming.channelize.io.R
import com.livestreaming.channelize.io.model.MessageCommentData
import kotlinx.android.synthetic.main.adapter_comment_list_item_layout.view.*

class LSCCommentListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var commentList: ArrayList<MessageCommentData>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_comment_list_item_layout, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (commentList.isNullOrEmpty())
            0
        else
            commentList?.size!!
    }

    fun setListData(list: ArrayList<MessageCommentData>?) {
        commentList = list

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    }

    class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {

        internal val userImageView: ImageView = mView.userImageView
        internal val userNameTextView: TextView = mView.userNameTextView
        internal val messageBodyTextView: TextView = mView.messageBodyTextView
    }
}