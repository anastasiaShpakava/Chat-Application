package com.mycompany.chatapp.ui.adapter

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
import com.google.firebase.database.*
import com.mycompany.chatapp.ui.MessageActivity
import com.mycompany.chatapp.R
import com.mycompany.chatapp.model.Chat
import com.mycompany.chatapp.model.User
import com.mycompany.chatapp.utils.ActivityConstants
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(
    private val context: Context,
    private var users: List<User>,
    private val isChat: Boolean
) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    var lastMessage: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: User = users[position]
        holder.username.text = user.username
        if (user.imageUrl.equals(context.applicationContext.getString(R.string.default_value))) {
            holder.profileImage.setImageResource(R.mipmap.ic_launcher)
        } else {
            Glide.with(context).load(user.imageUrl).into(holder.profileImage)
        }
        if (isChat) {
            lastMessage(user.id!!, holder.lastMsg)
        } else {
            holder.lastMsg.visibility = View.GONE
        }

        if (isChat) {
            if (user.status.equals(context.applicationContext.getString(R.string.online))) {
                holder.imageOn.visibility = View.VISIBLE
                holder.imageOff.visibility = View.GONE
            } else {
                holder.imageOn.visibility = View.GONE
                holder.imageOff.visibility = View.VISIBLE
            }
        } else {
            holder.imageOn.visibility = View.GONE
            holder.imageOff.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {
            var intent = Intent(context, MessageActivity::class.java)
            intent.putExtra(ActivityConstants.USER_ID, user.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var username: TextView = itemView.findViewById(R.id.username)
        var profileImage: CircleImageView = itemView.findViewById(R.id.profile_image)
        var imageOn: CircleImageView = itemView.findViewById(R.id.img_on)
        var imageOff: CircleImageView = itemView.findViewById(R.id.img_off)
        var lastMsg: TextView = itemView.findViewById(R.id.last_msg)
    }

    private fun lastMessage(userid: String, lastMsg: TextView) {
        lastMessage = context.applicationContext.getString(R.string.default_value)
        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(ActivityConstants.CHATS)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    var chat: Chat? = dataSnapshot.getValue(Chat::class.java)
                    if (chat?.receiver.equals(userid) && chat?.sender.equals(firebaseUser?.uid)) {
                        lastMessage = chat?.message
                    }
                }
                when (lastMessage) {
                    context.applicationContext.getString(R.string.default_value) -> lastMsg.text =
                        context.applicationContext.getString(R.string.no_message)

                    else -> lastMsg.text = lastMessage
                }

                lastMessage = context.applicationContext.getString(R.string.default_value)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}