package com.mycompany.chatapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.tabs.TabLayout

import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.mycompany.chatapp.R
import com.mycompany.chatapp.ui.fragments.ChatsFragment
import com.mycompany.chatapp.ui.fragments.ProfileFragment
import com.mycompany.chatapp.ui.fragments.UserFragment
import com.mycompany.chatapp.model.Chat
import com.mycompany.chatapp.model.User
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : AppCompatActivity() {

    private var userImage: CircleImageView? = null
    private var userName: TextView? = null

    private var firebaseUser: FirebaseUser? = null
    private var databaseReference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun init() {

        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        supportActionBar?.title = " "

        userImage = findViewById(R.id.profile_image)
        userName = findViewById(R.id.user_name)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        databaseReference =
            FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)

        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user: User? = dataSnapshot.getValue(User::class.java)
                userName?.text = user?.username
                if (user?.imageUrl.equals("default")) {
                    userImage?.setImageResource(R.mipmap.ic_launcher)
                } else {
                    Glide.with(applicationContext).load(user?.imageUrl).into(userImage!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })

        val tableLayout: TabLayout = findViewById(R.id.tab_layout)
        val viewPager: ViewPager = findViewById(R.id.view_pager)

        databaseReference = FirebaseDatabase.getInstance().getReference("Chats")
        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
                var unread: Int = 0
                for (snapshot: DataSnapshot in dataSnapshot.children) {
                    var chat: Chat? = snapshot.getValue(Chat::class.java)
                    if (chat?.receiver.equals(firebaseUser?.uid) && chat?.isseen == false) {
                        unread++
                    }
                }
                if (unread == 0) {
                    viewPagerAdapter.addFragment(ChatsFragment(), "Chats")
                } else {
                    viewPagerAdapter.addFragment(ChatsFragment(), "($unread) Chats")
                }
                viewPagerAdapter.addFragment(UserFragment(), "Users")
                viewPagerAdapter.addFragment(ProfileFragment(), "Profile")

                viewPager.adapter = viewPagerAdapter

                tableLayout.setupWithViewPager(viewPager)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(
                    Intent(
                        this,
                        StartActivity::class.java
                    ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                )
                return true
            }
        }
        return false
    }

    private fun status(status: String) {
        databaseReference =
            FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)

        val hashList = hashMapOf<String, Any>()
        hashList["status"] = status
        databaseReference?.updateChildren(hashList)
    }

    override fun onResume() {
        super.onResume()
        status("online")
    }

    override fun onPause() {
        super.onPause()
        status("offline")
    }

    class ViewPagerAdapter(supportFragmentManager: FragmentManager) :
        FragmentPagerAdapter(supportFragmentManager) {

        private val fragments: ArrayList<Fragment> = ArrayList()
        private val titles: ArrayList<String> = ArrayList()

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return titles[position]
        }
    }
}