package com.mycompany.chatapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mycompany.chatapp.MessageActivity
import com.mycompany.chatapp.R
import com.mycompany.chatapp.model.Chat
import com.mycompany.chatapp.model.User
import de.hdodenhof.circleimageview.CircleImageView

class MessageAdapter(
    private val context: Context,
    private val chatList: List<Chat>,
    private val imageUrl: String
) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    companion object {
        const val MSG_TYPE_LEFT = 0
        const val MSG_TYPE_RIGHT = 1
    }

    var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == MSG_TYPE_RIGHT) {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_item_right, parent, false)
            return ViewHolder(itemView)
        } else {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_item_left, parent, false)
            return ViewHolder(itemView)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatItem: Chat = chatList[position]
        holder.showMessage.text = chatItem.message
        if (imageUrl == "default") {
            holder.profileImage.setImageResource(R.mipmap.ic_launcher)
        } else {
            Glide.with(context).load(imageUrl).into(holder.profileImage)
        }
        if (position == chatList.size - 1) {
            if (chatItem.isseen!!) {
                holder.txtSeen.text = "Seen"
            } else {
                holder.txtSeen.text = "Delivered"
            }
        } else {
            holder.txtSeen.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var showMessage: TextView = itemView.findViewById(R.id.show_message)
        var profileImage: CircleImageView = itemView.findViewById(R.id.profile_image)
        var txtSeen: TextView = itemView.findViewById(R.id.txt_seen)
    }

    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        if (chatList[position].sender.equals(firebaseUser?.uid)) {
            return MSG_TYPE_RIGHT
        } else {
            return MSG_TYPE_LEFT
        }
    }
}