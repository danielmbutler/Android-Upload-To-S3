package com.dbtechprojects.uploadtos3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.options.StorageDownloadFileOptions
import com.dbtechprojects.uploadtos3.adapter.PictureAdapter
import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.coroutines.*
import java.io.File
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class GalleryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)



        Amplify.Storage.list(
                "",
                { result ->
                    GlobalScope.launch(Dispatchers.IO) {
                        val filesTxt = arrayListOf<String>()
                        val files = arrayListOf<String>()
                        result.getItems().forEach { item ->
                            Log.d("MyAmplifyApp", "Item: " + item.getKey())
                            filesTxt += item.key +" ${(item.size).div(1000)}KB"
                            files += item.key
                        }
                        withContext(Dispatchers.Main){
                            FileListTV.setText((filesTxt.toString()).replace("]","").replace("[",""))
                            FileListTV.visibility = View.VISIBLE

                            FileDwnBtn.visibility = View.VISIBLE

                            FileDwnBtn.setOnClickListener {
                                progressBar.visibility = View.VISIBLE
                                GlobalScope.launch(Dispatchers.IO){
                                    downloadfiles(files)
                                }

                            }
                        }

                    }



                },
                { error -> Log.e("MyAmplifyApp", "List failure", error) }
        )




    }

  fun deletefiles(file: S3File, pos: Int, list: ArrayList<S3File>){
        println(file)
        println(pos)
        Log.d("predelete", list.toString())

      suspend fun delfile(): String {
          return suspendCoroutine { continuation ->
              Amplify.Storage.remove(
                  file.origin,
                  { result -> Log.d("MyAmplifyApp", "Successfully removed: " + result.getKey())
                      continuation.resume("success")


                  },
                  { error -> Log.e("MyAmplifyApp", "Remove failure", error)

                  }
              )
          }
      }

      GlobalScope.launch(Dispatchers.IO){
          val del = delfile()
          if(del == "success"){
              Log.d("delete", del)
              withContext(Dispatchers.Main){
                  list.removeAt(pos)
                  populaterv(list)
              }
          }
      }


        }






    fun populaterv(list: ArrayList<S3File>){
        Log.d("downloadlist", list.toString())
        if (FileNameTV.visibility == View.VISIBLE){
            FileNameTV.visibility = View.GONE
        }

        if(progressBar.visibility == View.VISIBLE){
            progressBar.visibility = View.GONE
        }


        FileRecyclerView.apply {
            layoutManager = GridLayoutManager(this@GalleryActivity,3)
            adapter = PictureAdapter(this@GalleryActivity, list, this@GalleryActivity)
            (adapter as PictureAdapter).notifyDataSetChanged()
        }
    }

    fun downloadprogress(file: String){
        FileNameTV.visibility = View.VISIBLE
        FileNameTV.setText("${file} Downloaded")

    }

    private  fun downloadfiles(file: ArrayList<String>)  {

        val downloadFolder = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)

            val filepaths = arrayListOf<S3File>()
            file.size
            file.forEach { item-> val randomNumber = (1000..9999).random()

                Amplify.Storage.downloadFile(
                    item,
                    File("$downloadFolder/download$randomNumber.jpg"),
                    StorageDownloadFileOptions.defaultInstance(),
                    { progress ->
                        Log.d("MyAmplifyApp", "Fraction completed: ${progress.fractionCompleted}")
                    },
                    { result -> Log.d("MyAmplifyApp", "Successfully downloaded: ${result.getFile().name} Path: ${result.file.absolutePath}")
                        downloadprogress(result.getFile().name)
                        val fileobj = S3File(
                            path = result.file.absolutePath,
                            key = result.file.name, // downloaded filename
                            origin = item, //original filename
                        )
                        filepaths += fileobj
                        println("filelists${filepaths.size} ${file.size}")
                        if(filepaths.size == file.size){
                            populaterv(filepaths)
                        }
                    },
                    { error -> Log.d("MyAmplifyApp", "Download Failure", error) }
                )

            }


    }
}