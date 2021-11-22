package com.thinkingdobby.databaseproject

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.thinkingdobby.databaseproject.adapter.PetAdapter
import com.thinkingdobby.databaseproject.data.PetPost
import kotlinx.android.synthetic.main.activity_find_pet.*

class FindPetActivity : AppCompatActivity() {

    private var mode: String? = "FindPet"
    private val postList = mutableListOf<PetPost>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_pet)

        window.apply {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                statusBarColor = Color.TRANSPARENT
            }
        }

        mode = intent.getStringExtra("mode") ?: "FindPet"

        if (mode == "FindPet") {
            findPet_tv_title.text = "주인이 찾고 있는 동물들"

            findPet_btn_toFindPet.setImageResource(R.drawable.bot_icon_find_pet_on)
            findPet_btn_toFindPerson.setImageResource(R.drawable.bot_icon_find_person_off)
            findPet_btn_toHome.setImageResource(R.drawable.bot_icon_home_off)
        } else if (mode == "FindPerson") {
            findPet_tv_title.text = "주인을 찾고 있는 동물들"

            findPet_btn_toFindPet.setImageResource(R.drawable.bot_icon_find_pet_off)
            findPet_btn_toFindPerson.setImageResource(R.drawable.bot_icon_find_person_on)
            findPet_btn_toHome.setImageResource(R.drawable.bot_icon_home_off)
        }

        findPet_btn_add.setOnClickListener {
            val intent = Intent(this, PostPetActivity::class.java)
            intent.putExtra("mode", mode)
            startActivity(intent)
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        }

        // 하단 메뉴
        findPet_btn_toFindPet.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)

            mode = "FindPet"
            intent.putExtra("mode", mode)
            startActivity(intent)
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        }

        findPet_btn_toFindPerson.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)

            mode = "FindPerson"
            intent.putExtra("mode", mode)
            startActivity(intent)
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        }

        findPet_btn_toHome.setOnClickListener {
//            val intent = Intent(this, HomeActivity::class.java)
//            startActivity(intent)
//            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
//            finish()
        }


        val layoutManager = LinearLayoutManager(this@FindPetActivity)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true

        findPet_rv_list.layoutManager = layoutManager
        findPet_rv_list.adapter = PetAdapter(this@FindPetActivity, postList, mode!!)

        try {
            FirebaseDatabase.getInstance().getReference(mode!!)
                .orderByChild("writeTime").addChildEventListener(object : ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        snapshot?.let { snapshot ->
                            val post = snapshot.getValue(PetPost::class.java)
                            post?.let {
                                if (previousChildName == null) {
                                    postList.add(it)
                                    findPet_rv_list.adapter?.notifyItemInserted(postList.size - 1)
                                } else {
                                    val prevIndex =
                                        postList.map { it.postId }.indexOf(previousChildName)
                                    postList.add(prevIndex + 1, post)
                                    findPet_rv_list.adapter?.notifyItemInserted(prevIndex + 1)
                                }
                            }
//                        if (postList.size != 0) list_tv_noPost.text = ""
                        }
                    }

                    override fun onChildChanged(
                        snapshot: DataSnapshot,
                        previousChildName: String?
                    ) {
                        snapshot?.let { snapshot ->
                            val post = snapshot.getValue(PetPost::class.java)
                            post?.let {
                                val prevIndex =
                                    postList.map { it.postId }.indexOf(previousChildName)
                                postList[prevIndex + 1] = post
                                findPet_rv_list.adapter?.notifyItemChanged(prevIndex + 1)
                            }
                        }
                    }

                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                        snapshot?.let {
                            val post = snapshot.getValue(PetPost::class.java)
                            post?.let { post ->
                                val existIndex = postList.map { it.postId }.indexOf(post.postId)
                                postList.removeAt(existIndex)
                                findPet_rv_list.adapter?.notifyItemRemoved(existIndex)
                                if (previousChildName == null) {
                                    postList.add(post)
                                    findPet_rv_list.adapter?.notifyItemChanged(postList.size - 1)
                                } else {
                                    val prevIndex =
                                        postList.map { it.postId }.indexOf(previousChildName)
                                    postList.add(prevIndex + 1, post)
                                    findPet_rv_list.adapter?.notifyItemChanged(prevIndex + 1)
                                }
                            }
                        }
                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {
                        snapshot?.let {
                            val post = snapshot.getValue(PetPost::class.java)
                            post?.let { post ->
                                val existIndex = postList.map { it.postId }.indexOf(post.postId)
                                postList.removeAt(existIndex)
                                findPet_rv_list.adapter?.notifyItemRemoved(existIndex)
                            }
//                        if (postList.size != 0) list_tv_noPost.text = ""
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        error?.toException()?.printStackTrace()
                    }
                })

//        if (postList.size == 0) list_tv_noPost.text = "게시물이 없습니다."
//        else list_tv_noPost.text = ""
        } catch (e: Exception) {
            Log.d("Load Error", e.toString())
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
    }
}
