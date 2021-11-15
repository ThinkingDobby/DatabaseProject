package com.thinkingdobby.databaseproject.viewHolder

import android.opengl.Visibility
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.thinkingdobby.databaseproject.R
import com.thinkingdobby.databaseproject.data.PetPost
import kotlinx.android.synthetic.main.pet_card.view.*

class PetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val pet_iv_background = itemView.pet_iv_background  // 추후 구현

    val pet_tv_location = itemView.pet_tv_location
    val pet_tv_breed = itemView.pet_tv_breed
    val pet_tv_name = itemView.pet_tv_name
    val pet_tv_lengthValue = itemView.pet_tv_lengthValue
    val pet_iv_sex = itemView.pet_iv_sex

    val pet_iv_belt = itemView.pet_iv_belt
    val pet_tv_belt = itemView.pet_tv_belt

    fun bind(pet: PetPost) {
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
        }
    }
}