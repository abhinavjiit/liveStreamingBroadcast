package com.livestreaming.channelize.io.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.livestreaming.channelize.io.ImageLoader
import com.livestreaming.channelize.io.R
import com.livestreaming.channelize.io.model.MessageCommentData
import kotlinx.android.synthetic.main.adapter_comment_list_item_layout.view.*

class LSCCommentListAdapter(private val context: Context, private val commentList: ArrayList<MessageCommentData>?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_comment_list_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return commentList?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.apply {
                messageBodyTextView.text = commentList?.get(position)?.body
                userNameTextView.text = commentList?.get(position)?.userName
                if (!commentList?.get(position)?.userImage?.trim().isNullOrBlank()) {
                    nameTextView.visibility = View.GONE
                    userImageView.visibility = View.VISIBLE
                    try {
                        ImageLoader.showImage(
                            commentList?.get(position)?.userImage,
                            userImageView,
                            context.resources.getDimensionPixelSize(R.dimen.dimen_30),
                            context.resources.getDimensionPixelSize(R.dimen.dimen_30)
                        )
                    } catch (e: Exception) {
                        userImageView.setImageResource(R.drawable.smiley)
                    }
                } else {
                    val nameParts = commentList?.get(position)?.userName?.split(" ")?.toTypedArray()

                    val initials = when (nameParts?.size) {
                        1 -> {
                            nameParts[0][0].toString()
                        }
                        2, 3, 4 -> {
                            nameParts[0][0].toString() + nameParts[1][0]
                        }
                        else -> {
                            context.resources.getString(R.string.null_user_name_string)
                        }
                    }
                    nameTextView.visibility = View.VISIBLE
                    userImageView.visibility = View.GONE
                    nameTextView.text = initials
                }
            }
        }
    }

    class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        internal val userImageView: ImageView = mView.userImageView
        internal val userNameTextView: TextView = mView.userNameTextView
        internal val messageBodyTextView: TextView = mView.messageBodyTextView
        internal val nameTextView: TextView = mView.nameTextView
    }

}