package com.app.tawktest.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.tawktest.R
import com.app.tawktest.localDataBase.User
import com.app.tawktest.response.UserListItem
import com.app.tawktest.ui.UserProfileActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import java.io.Serializable

class UserListAdapter(var context: Context, var userArrayList: ArrayList<User>) :
    RecyclerView.Adapter<UserListAdapter.ViewHolder>(), Filterable {

    var userFilterList = ArrayList<User>()
    init {
        userFilterList = userArrayList
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtName: TextView = itemView.findViewById(R.id.txtName)
        val txtDetails: TextView = itemView.findViewById(R.id.txtDetails)
        val userImg: ImageView = itemView.findViewById(R.id.userImg)
        val noteImg: ImageView = itemView.findViewById(R.id.noteImg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_list_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userListItem = userFilterList[position]
        holder.txtName.text = userListItem.name
//        holder.txtDetails.text = userListItem.type
        Glide.with(context).load(userListItem.image).placeholder(R.mipmap.ic_launcher).skipMemoryCache(false).centerCrop().into(holder.userImg)

        if (userListItem.note.isNotEmpty()){
            holder.noteImg.visibility = View.VISIBLE
        }else{
            holder.noteImg.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context, UserProfileActivity::class.java)
            intent.putExtra("userData", userListItem as Serializable)
            context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int {
        return userFilterList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                userFilterList = if (charSearch.isEmpty()) {
                    userArrayList as ArrayList<User>
                } else {
                    val resultList = ArrayList<User>()
                    for (row in userArrayList) {
                        if (row.name.toLowerCase().contains(constraint.toString().toLowerCase())) {
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = userFilterList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                userFilterList = results?.values as ArrayList<User>
                notifyDataSetChanged()
            }
        }
    }
}