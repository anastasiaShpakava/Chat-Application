package com.mycompany.chatapp

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
import com.mycompany.chatapp.fragments.ChatsFragment
import com.mycompany.chatapp.fragments.ProfileFragment
import com.mycompany.chatapp.fragments.UserFragment
import com.mycompany.chatapp.model.User
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : AppCompatActivity() {

    private var userImage: CircleImageView? = null
    private var userName: TextView? = null

    private var firebaseUser: FirebaseUser? = null
    private var databaseReference: DatabaseReference? = null

    private val fragments: ArrayList<Fragment>?=null
    private val titles: ArrayList<String>?=null


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
                    Glide.with(this@MainActivity).load(user?.imageUrl).into(userImage!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })

        val tableLayout: TabLayout = findViewById(R.id.tab_layout)
        val viewPager: ViewPager = findViewById(R.id.view_pager)

        val viewPagerAdapter= ViewPagerAdapter(supportFragmentManager, fragments!!,titles!!)
        viewPagerAdapter.addFragment(ChatsFragment(),"Chats")
        viewPagerAdapter.addFragment(UserFragment(),"Users")
        viewPagerAdapter.addFragment(ProfileFragment(),"Profile")

        viewPager.adapter=viewPagerAdapter

        tableLayout.setupWithViewPager(viewPager)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, StartActivity::class.java))
                finish()
                return true
            }
        }
        return false
    }

    class ViewPagerAdapter(
        fragmentManager: FragmentManager,
        private val fragments: ArrayList<Fragment>,
        private val titles: ArrayList<String>
    ) : FragmentPagerAdapter(fragmentManager) {

        override fun getCount(): Int {
          return fragments.size
        }

        override fun getItem(position: Int): Fragment {
           return fragments[position]
        }

        fun addFragment(fragment:Fragment, title:String){
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }
    }
}