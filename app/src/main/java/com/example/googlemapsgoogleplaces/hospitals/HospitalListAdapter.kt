package com.example.googlemapsgoogleplaces.hospitals

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.example.googlemapsgoogleplaces.R
import com.example.googlemapsgoogleplaces.commons.inflate
import com.example.googlemapsgoogleplaces.data.models.Hospital
import kotlinx.android.synthetic.main.hospital_item.view.*

class HospitalListAdapter(private val viewActions: ViewActions) : RecyclerView.Adapter<HospitalListAdapter.SimpleViewHolder>() {
    interface ViewActions {
        fun onItemSelected(latitude: Double, longitude: Double)
    }

    private val hospitals = ArrayList<Hospital>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
        return SimpleViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return hospitals.size
    }

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        holder.bind(hospitals[position])
    }

    fun addOrders(hospitals: List<Hospital>) {
        this.hospitals.addAll(hospitals)
        notifyDataSetChanged()
    }

    fun clearAndAddOrders(hospitals: List<Hospital>) {
        this.hospitals.clear()
        this.hospitals.addAll(hospitals)
        notifyDataSetChanged()
    }


    inner class SimpleViewHolder(private val viewGroup: ViewGroup) :
            RecyclerView.ViewHolder(viewGroup.inflate(R.layout.hospital_item)) {

        @SuppressLint("SetTextI18n")
        fun bind(item: Hospital) = with(itemView) {
            hospital_name.text = item.name
            hospital_phone.text = item.phone
            hospital_address.text = item.address

            itemView.setOnClickListener {
                viewActions.onItemSelected(
                        latitude = item.latitude,
                        longitude = item.longitude
                )
            }
        }
    }

}