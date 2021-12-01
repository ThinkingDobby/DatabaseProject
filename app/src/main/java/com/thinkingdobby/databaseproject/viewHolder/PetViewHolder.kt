package com.thinkingdobby.databaseproject.viewHolder

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.firebase.storage.FirebaseStorage
import com.thinkingdobby.databaseproject.R
import com.thinkingdobby.databaseproject.data.PetPost
import kotlinx.android.synthetic.main.pet_card.view.*
import java.lang.IllegalArgumentException


class PetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val pet_iv_background = itemView.pet_iv_background

    val pet_tv_location = itemView.pet_tv_location
    val pet_tv_breed = itemView.pet_tv_breed
    val pet_tv_name = itemView.pet_tv_name
    val pet_tv_lengthValue = itemView.pet_tv_lengthValue
    val pet_iv_sex = itemView.pet_iv_sex

    val pet_iv_belt = itemView.pet_iv_belt
    val pet_tv_belt = itemView.pet_tv_belt

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun bind(pet: PetPost, context: Context, mode: String) {
        if (mode == "FindPet") pet_tv_belt.text = "동물 찾음"

        pet_tv_location.text = pet.location
        pet_tv_breed.text = pet.breed
        pet_tv_name.text = pet.name
        pet_tv_lengthValue.text = pet.length

        if (pet.sex == "남") {
            pet_iv_sex.setImageResource(R.drawable.icon_gender_boy)
        } else if (pet.sex == "여") {
            pet_iv_sex.setImageResource(R.drawable.icon_gender_girl)
        }

        if  (pet.find) {
            pet_iv_belt.visibility = View.VISIBLE
            pet_tv_belt.visibility = View.VISIBLE
        } else {
            pet_iv_belt.visibility = View.INVISIBLE
            pet_tv_belt.visibility = View.INVISIBLE
        }

        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.setTint(Color.WHITE)   // 추후 수정
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        // 스토리지에서 이미지 받아오기
        val storageRef = FirebaseStorage.getInstance().getReference("images").child(pet.postId)

        storageRef.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Glide 이용하여 이미지뷰에 로딩
                try {
                    Glide.with(context)
                        .load(task.result)
                        .placeholder(circularProgressDrawable)
                        .transform(CenterCrop())
                        .into(pet_iv_background)
                } catch (e: IllegalArgumentException) {
                    Log.d("Glide Error", "from PetViewHolder")
                }
            } else {
                // URL을 가져오지 못하면 토스트 메세지
                Log.d("Image Load Error", "URL 불러오지 못함")
            }
        }
    }
}