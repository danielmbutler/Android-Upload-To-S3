package com.dbtechprojects.uploadtos3.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dbtechprojects.uploadtos3.GalleryActivity
import com.dbtechprojects.uploadtos3.R
import com.dbtechprojects.uploadtos3.S3File
import kotlinx.android.synthetic.main.item_dashboard_layout.view.*

open class PictureAdapter(
    private val context: Context,
    private var list: ArrayList<S3File>,
    private var Activity: GalleryActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_dashboard_layout,
                parent,
                false
            )
        )
    }



    /**
     * Binds each item in the ArrayList to a view
     *
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val file  = list[position]

        if (holder is MyViewHolder) {

            Glide.with(context).load(file.path).into(holder.itemView.iv_dashboard_item_image)


            holder.itemView.setOnClickListener {
                Toast.makeText(context, "Name: ${file.key}",
                    Toast.LENGTH_SHORT).show()

                val builder = AlertDialog.Builder(context)
                builder.setMessage("Do you want to delete this file from the bucket ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialog, id ->
                        Activity.deletefiles(file, position, list)
                    }

                    .setNegativeButton("No") { dialog, id ->
                        // Dismiss the dialog
                        dialog.dismiss()
                    }
                        val alert = builder.create()
                        alert.show()

            }

        }
        }

    override fun getItemCount():Int = list.size


}

    /**
     * Gets the number of items in the list
     */


    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)


