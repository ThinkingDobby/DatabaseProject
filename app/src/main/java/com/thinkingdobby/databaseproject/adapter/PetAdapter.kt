package com.thinkingdobby.databaseproject.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thinkingdobby.databaseproject.R
import com.thinkingdobby.databaseproject.data.PetPost
import com.thinkingdobby.databaseproject.viewHolder.PetViewHolder

class PetAdapter(val context: Context, val dataList: MutableList<PetPost>) : RecyclerView.Adapter<PetViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val view = LayoutInflater.from(parent?.context)
            .inflate(R.layout.pet_card, parent, false)
        return PetViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        holder?.bind(dataList[position])
    }
}