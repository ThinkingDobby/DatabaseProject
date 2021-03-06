package com.thinkingdobby.databaseproject.adapter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.thinkingdobby.databaseproject.DetailActivity
import com.thinkingdobby.databaseproject.R
import com.thinkingdobby.databaseproject.data.PetPost
import com.thinkingdobby.databaseproject.viewHolder.PetViewHolder
import kotlinx.android.synthetic.main.pet_card.view.*

class PetAdapter(val context: Context, private val dataList: MutableList<PetPost>, private val mode: String) : RecyclerView.Adapter<PetViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.pet_card, parent, false)
        return PetViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        holder.bind(dataList[position], context, mode)
        try {
            holder.itemView.pet_btn_info.setOnClickListener {
                val intent = Intent(context, DetailActivity::class.java)
                val bundle = Bundle()
                bundle.putParcelable("selectedPet", dataList[position])
                intent.putExtras(bundle)
                intent.putExtra("mode", mode)
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            Log.d("infoClick", e.toString())
        }
    }
}