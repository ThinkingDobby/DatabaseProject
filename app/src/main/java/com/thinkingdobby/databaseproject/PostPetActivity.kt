package com.thinkingdobby.databaseproject

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.thinkingdobby.databaseproject.data.PetPost
import kotlinx.android.synthetic.main.activity_post_pet.*
import java.text.SimpleDateFormat
import java.util.*

class PostPetActivity : AppCompatActivity() {

    private var lostTime = SimpleDateFormat("yyyy년 MM월 dd일").format(Date())
    private var sex = "남"
    private var pickImageFromAlbum = 0
    private var uriPhoto: Uri? = Uri.parse("android.resource://com.thinkingdobby.databaseproject/drawable/card_background_sample")

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

        postPet_btn_back.setOnClickListener {
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            finish()
        }

        postPet_btn_gallery.setOnClickListener { loadImage() }

        postPet_btn_selectLostTime.setOnClickListener {
            val today = GregorianCalendar()
            val year: Int = today.get(Calendar.YEAR)
            val month: Int = today.get(Calendar.MONTH)
            val date: Int = today.get(Calendar.DATE)
            val dlg = DatePickerDialog(this, object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                    lostTime = "${year}년 ${month+1}월 ${dayOfMonth}일"
                    postPet_et_lostTime.setText(lostTime)
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
            val ref = FirebaseDatabase.getInstance().getReference("FindPet").push()

            val post = PetPost()
            post.postId = ref.key!!
            post.writeTime = ServerValue.TIMESTAMP
            post.writer = "temp"

            post.location = postPet_et_location.text.toString()
            post.lostTime = lostTime
            post.breed = postPet_et_breed.text.toString()
            post.name = postPet_et_name.text.toString()
            post.sex = postPet_et_sex.text.toString()
            post.length = postPet_et_length.text.toString()

            post.info = postPet_et_info.text.toString()
            post.stat = postPet_et_stat.text.toString()

            post.find = false

            // Firebase Database Upload
            ref.setValue(post)

            // Firebase Storage Upload
            imageUpload(post.postId)
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
            } else {
                finish()
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
