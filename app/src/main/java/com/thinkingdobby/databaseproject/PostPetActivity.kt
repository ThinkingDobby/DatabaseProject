package com.thinkingdobby.databaseproject

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.thinkingdobby.databaseproject.data.PetPost
import com.thinkingdobby.databaseproject.functions.getMyId
import kotlinx.android.synthetic.main.activity_post_pet.*
import kotlinx.android.synthetic.main.activity_post_pet.findPet_tv_title
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.*

class PostPetActivity : AppCompatActivity() {

    private var time = SimpleDateFormat("yyyy년 MM월 dd일").format(Date())
    private var sex = "남"
    private var pickImageFromAlbum = 0
    private var uriPhoto: Uri? = Uri.parse("android.resource://com.thinkingdobby.databaseproject/drawable/card_background_sample")

    private var mode: String? = "FindPet"
    private var postId = "temp"
    private var imageChanged = false

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_pet)

        // request permission
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        // request permission

        window.apply {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                statusBarColor = Color.TRANSPARENT
            }
        }

        postPet_et_time.text = time

        mode = intent.getStringExtra("mode")

        if (mode == "FindPerson") {
            postPet_tv_location.text = "발견장소"
            postPet_et_location.hint = "발견장소를 입력하세요"

            postPet_tv_time.text = "발견일시"
        }

        val edit = intent.getStringExtra("edit") ?: "no"
        if (edit == "yes") {
            val bundle = intent.extras
            val pet = bundle!!.getParcelable<PetPost>("selectedPet")!!
            postId = pet.postId

            findPet_tv_title.text = "포스트 수정"
            postPet_tv_post.text = "포스트 수정"

            postPet_et_location.setText(pet.location)
            postPet_et_time.text = pet.time
            postPet_et_info.setText(pet.info)
            postPet_et_name.setText(pet.name)
            postPet_et_breed.setText(pet.breed)
            postPet_et_sex.text = pet.sex
            postPet_et_length.setText(pet.length)
            postPet_et_stat.setText(pet.stat)

            val storageRef = FirebaseStorage.getInstance().getReference("images").child(pet.postId)

            val circularProgressDrawable = CircularProgressDrawable(this@PostPetActivity)
            circularProgressDrawable.setTint(Color.WHITE)   // 추후 수정
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()

            storageRef.downloadUrl.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Glide 이용하여 이미지뷰에 로딩
                    try {
                        Glide.with(this@PostPetActivity)
                            .load(task.result)
                            .placeholder(circularProgressDrawable)
                            .transform(CenterCrop())
                            .into(postPet_iv_pet)
                    } catch (e: IllegalArgumentException) {
                        Log.d("Glide Error", "from PostPetActivity")
                    }
                } else {
                    // URL을 가져오지 못하면 토스트 메세지
                    Toast.makeText(
                        this@PostPetActivity,
                        task.exception!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            postPet_iv_pet.setImageResource(R.drawable.card_background_sample)
        }

        postPet_btn_back.setOnClickListener {
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            finish()
        }

        postPet_btn_gallery.setOnClickListener { loadImage() }

        postPet_btn_selectTime.setOnClickListener {
            val today = GregorianCalendar()
            val year: Int = today.get(Calendar.YEAR)
            val month: Int = today.get(Calendar.MONTH)
            val date: Int = today.get(Calendar.DATE)
            val dlg = DatePickerDialog(this, object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                    time = "${year}년 ${month+1}월 ${dayOfMonth}일"
                    postPet_et_time.setText(time)
                }
            }, year, month, date)
            dlg.show()
        }

        postPet_btn_selectSex.setOnClickListener {
            val builder = AlertDialog.Builder(this@PostPetActivity)
            builder.setTitle("성별을 고르세요.")
            val sexes = arrayOf("남", "여")
            builder.setSingleChoiceItems(sexes, -1) { _, which: Int ->
                sex = sexes[which]
            }

            builder.setPositiveButton("확인") { _, which ->
                postPet_et_sex.text = sex
            }

            builder.setNegativeButton("취소") {_, which -> }

            builder.create().show()
        }


        postPet_btn_post.setOnClickListener {
            ProgressDialog.show(this, "", "업로드 중입니다...", true)

            // Firebase Database Upload
            var ref = FirebaseDatabase.getInstance().getReference(mode!!).push()

            val post = PetPost()
            post.postId = if (edit == "no") ref.key!! else postId
            post.writeTime = ServerValue.TIMESTAMP
            post.writer = getMyId(this)

            post.location = postPet_et_location.text.toString()
            post.time = time
            post.breed = postPet_et_breed.text.toString()
            post.name = postPet_et_name.text.toString()
            post.sex = postPet_et_sex.text.toString()
            post.length = postPet_et_length.text.toString()

            post.info = postPet_et_info.text.toString()
            post.stat = postPet_et_stat.text.toString()

            post.find = false

            // Firebase Database Upload
            if (edit == "no") ref.setValue(post)
            // Firebase Update
            else FirebaseDatabase.getInstance().getReference("$mode/$postId").setValue(post)

            // Firebase Storage Upload
            if (imageChanged) imageUpload(post.postId)
            else {
                Toast.makeText(this@PostPetActivity, "업로드 되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun loadImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(Intent.createChooser(intent, "Load Picture"), pickImageFromAlbum)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickImageFromAlbum) {
            if (resultCode == Activity.RESULT_OK) {
                uriPhoto = data?.data
                postPet_iv_pet.setImageURI(uriPhoto)
                imageChanged = true
            }
        }
    }

    private fun imageUpload(id: String) {
        val storageRef = FirebaseStorage.getInstance().reference.child("images").child(id)

        storageRef.putFile(uriPhoto!!).addOnSuccessListener {
            Toast.makeText(this@PostPetActivity, "업로드 되었습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fadein, R.anim.fadeout)
    }
}
