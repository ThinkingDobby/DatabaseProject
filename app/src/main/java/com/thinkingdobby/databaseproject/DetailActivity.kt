package com.thinkingdobby.databaseproject

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.firebase.storage.FirebaseStorage
import com.thinkingdobby.databaseproject.data.PetPost
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

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

        if (pet.find) {
            detail_iv_belt.visibility = View.VISIBLE
            detail_tv_belt.visibility = View.VISIBLE
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
                Glide.with(this@DetailActivity)
                    .load(task.result)
                    .placeholder(circularProgressDrawable)
                    .transform(CenterCrop())
                    .into(detail_iv_pet)
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
    }
}
