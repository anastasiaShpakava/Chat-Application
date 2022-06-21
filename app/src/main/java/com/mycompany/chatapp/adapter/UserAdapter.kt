package com.mycompany.chatapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mycompany.chatapp.R
import com.mycompany.chatapp.model.User
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(private val context: Context, private val users: List<User>) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: User = users[position]
        holder.username.text = user.username
        if (user.imageUrl.equals("default")) {
            holder.profileImage.setImageResource(R.mipmap.ic_launcher)
        } else {
            Glide.with(context).load(user.imageUrl).into(holder.profileImage)
        }
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var username: TextView = itemView.findViewById(R.id.username)
        var profileImage: CircleImageView = itemView.findViewById(R.id.profile_image)
    }
}