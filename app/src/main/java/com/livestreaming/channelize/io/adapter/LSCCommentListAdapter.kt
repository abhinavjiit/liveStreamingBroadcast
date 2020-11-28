package com.livestreaming.channelize.io.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.livestreaming.channelize.io.R
import com.livestreaming.channelize.io.model.MessageCommentData
import com.squareup.picasso.Picasso
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

        if (holder is ViewHolder) {
            holder.apply {
                messageBodyTextView.text = commentList?.get(position)?.body
                userNameTextView.text = commentList?.get(position)?.userName
                if (!commentList?.get(position)?.userImage?.trim().isNullOrBlank()) {
                    nameTextView.visibility = View.GONE
                    userImageView.visibility = View.VISIBLE
                    try {
                        Picasso.get().load(commentList?.get(position)?.userImage?.trim())
                            .into(userImageView)
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
                            "null"
                        }
                    }
                    /*  val initials: String = nameParts?.get(0)?.get(0).toString() + nameParts?.also {
                          it.size > 2
                      }?.also {
                          it[1][0]
                      }*/

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