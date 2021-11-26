package com.thinkingdobby.databaseproject

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.thinkingdobby.databaseproject.data.PetPost
import com.thinkingdobby.databaseproject.functions.getMyId
import kotlinx.android.synthetic.main.activity_detail.*
import java.lang.IllegalArgumentException

class DetailActivity : AppCompatActivity() {

    private var find = false

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        overridePendingTransition(R.anim.fadein, R.anim.fadeout)

        window.apply {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                statusBarColor = Color.TRANSPARENT
            }
        }

        val bundle = intent.extras
        val pet = bundle!!.getParcelable<PetPost>("selectedPet")!!
        val mode = intent.getStringExtra("mode") ?: "FindPet"

        val pref = getSharedPreferences("basic", MODE_PRIVATE)
        val nowId = pref.getString("writerId", "temp")

        if (mode == "FindPerson") {
            detail_tv_location.text = "발견장소"
            detail_tv_time.text = "발견일시"
        } else {
            detail_tv_belt.text = "동물 찾음"
        }

        find = pet.find
        if (find) {
            detail_iv_belt.visibility = View.VISIBLE
            detail_tv_belt.visibility = View.VISIBLE
        }

        if (pet.writer == nowId) {
            detail_btn_change.visibility = View.VISIBLE
            detail_btn_edit.visibility = View.VISIBLE
            detail_btn_delete.visibility = View.VISIBLE
        }

        val storageRef = FirebaseStorage.getInstance().getReference("images").child(pet.postId)

        val circularProgressDrawable = CircularProgressDrawable(this@DetailActivity)
        circularProgressDrawable.setTint(Color.WHITE)   // 추후 수정
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        storageRef.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Glide 이용하여 이미지뷰에 로딩
                try {
                    Glide.with(this@DetailActivity)
                        .load(task.result)
                        .placeholder(circularProgressDrawable)
                        .transform(CenterCrop())
                        .into(detail_iv_pet)
                } catch (e: IllegalArgumentException) {
                    Log.d("Glide Error", "from DetailActivity")
                }
            } else {
                // URL을 가져오지 못하면 토스트 메세지
                Toast.makeText(
                    this@DetailActivity,
                    task.exception!!.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        detail_et_location.text = pet.location
        detail_et_time.text = pet.time
        detail_et_info.text = pet.info
        detail_et_name.text = pet.name
        detail_et_breed.text = pet.breed
        detail_et_sex.text = pet.sex
        detail_et_length.text = pet.length
        detail_et_stat.text = pet.stat

        detail_btn_back.setOnClickListener {
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            finish()
        }

        detail_btn_change.setOnClickListener {
            if (find) {
                find = false
                detail_iv_belt.visibility = View.INVISIBLE
                detail_tv_belt.visibility = View.INVISIBLE
            } else {
                find = true
                detail_iv_belt.visibility = View.VISIBLE
                detail_tv_belt.visibility = View.VISIBLE
            }
            FirebaseDatabase.getInstance().getReference("$mode/${pet.postId}/find").setValue(find)
        }

        detail_btn_edit.setOnClickListener {
            val intent = Intent(this, PostPetActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable("selectedPet", pet)
            intent.putExtras(bundle)
            intent.putExtra("mode", mode)
            intent.putExtra("edit", "yes")
            startActivity(intent)
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        }

        detail_btn_delete.setOnClickListener {
            val ref = FirebaseDatabase.getInstance().getReference(mode).child(pet.postId)
            val builder = AlertDialog.Builder(this@DetailActivity)
            builder.setTitle("글을 삭제할까요?")

            builder.setPositiveButton("아니오") { _, which ->
            }

            builder.setNegativeButton("예") {_, which ->
                if (pet.writer == nowId) {
                    ref.removeValue()
                    storageRef.delete()
                    Toast.makeText(this@DetailActivity, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            builder.create().show()
        }

        detail_btn_writerInfo.setOnClickListener {
            val intent = Intent(this, WriterDetailActivity::class.java)
            intent.putExtra("writerId", pet.writer)
            startActivity(intent)
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
    }
}
