package com.app.tawktest.adapter

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.util.Log
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
import com.app.tawktest.ui.UserProfileActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
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
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.user_list_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userListItem = userFilterList[position]
        holder.txtName.text = userListItem.name
//        holder.txtDetails.text = userListItem.type
//        Glide.with(context).load(userListItem.image).placeholder(R.mipmap.ic_launcher).skipMemoryCache(false).centerCrop().into(holder.userImg)
        Glide.with(context).asBitmap()
            .load(userListItem.image)
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .listener(object : RequestListener<Bitmap?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Bitmap?>,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any,
                    target: Target<Bitmap?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    if (position % 4 == 0) {
                        val refresh = Handler(Looper.getMainLooper())
                        refresh.post {
                            holder.userImg.setImageBitmap(invertImage(resource!!))
                        }
                    } else {
                        val refresh = Handler(Looper.getMainLooper())
                        refresh.post {
                            holder.userImg.setImageBitmap(resource)
                        }
                    }
                    return false
                }
            }).submit()

        if (userListItem.note.isNotEmpty()) {
            holder.noteImg.visibility = View.VISIBLE
        } else {
            holder.noteImg.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, UserProfileActivity::class.java)
            intent.putExtra("userData", userListItem as Serializable)
            context.startActivity(intent)
        }

    }

    fun invertImage(src: Bitmap): Bitmap? {
        // create new bitmap with the same attributes(width,height)
        //as source bitmap
        val bmOut = Bitmap.createBitmap(src.width, src.height, src.config)
        // color info
        var A: Int
        var R: Int
        var G: Int
        var B: Int
        var pixelColor: Int
        // image size
        val height = src.height
        val width = src.width

        // scan through every pixel
        for (y in 0 until height) {
            for (x in 0 until width) {
                // get one pixel
                pixelColor = src.getPixel(x, y)
                // saving alpha channel
                A = Color.alpha(pixelColor)
                // inverting byte for each R/G/B channel
                R = 255 - Color.red(pixelColor)
                G = 255 - Color.green(pixelColor)
                B = 255 - Color.blue(pixelColor)
                // set newly-inverted pixel to output image
                bmOut.setPixel(x, y, Color.argb(A, R, G, B))
            }
        }

        // return final bitmap
        return bmOut
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
                        if (row.name.toLowerCase().contains(
                                constraint.toString().toLowerCase()
                            ) or row.note.toLowerCase()
                                .contains(constraint.toString().toLowerCase())
                        ) {
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