package com.example.project_dam

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDateTime
import java.util.Locale

class Articales : AppCompatActivity() {

    class VolleySingleton private constructor(context: Context) {
        companion object {
            @Volatile
            private var INSTANCE: VolleySingleton? = null
            fun getInstance(context: Context) =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: VolleySingleton(context).also {
                        INSTANCE = it
                    }
                }
        }

        val requestQueue: RequestQueue by lazy {
            // applicationContext is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            Volley.newRequestQueue(context.applicationContext)
        }

        fun <T> addToRequestQueue(req: com.android.volley.Request<T>) {
            requestQueue.add(req)
        }
    }

    private var articleList: ArrayList<Article> = arrayListOf()
    private val CAMERA_REQUEST_CODE = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_articales)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fetchArticles()

        val searchEditText: EditText = findViewById(R.id.searchEditText)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })


        val homeIcon: ImageView = findViewById(R.id.homeIcon)
        homeIcon.setOnClickListener {
            val intent = Intent(this, Homepage::class.java)
            startActivity(intent)
        }

        val buttonOpenCamera: ImageView = findViewById(R.id.qrIcon)
        buttonOpenCamera.setOnClickListener {
            showCameraPermissionDialog()
        }

        val scheduleicon: ImageView = findViewById(R.id.scheduleIcon)
        scheduleicon.setOnClickListener {
            startActivity(Intent(this, Schedule::class.java))
        }

        val usericon : ImageView = findViewById(R.id.homepageUserImage)
        usericon.setOnClickListener {
            startActivity(Intent(this, userpage::class.java))
        }
    }

    private fun fetchArticles() {
        val url = "http://10.0.2.2/summithub/api.php"
        val requestJson = JSONObject()
        requestJson.put("action", "getEntries")
        requestJson.put("table", "articles")

        val jsonArrayRequest = JsonObjectRequest(
            Request.Method.POST, url, requestJson,
            { response ->
                try {
                    val articlesArray = response.getJSONArray("response")
                    for (i in 0 until articlesArray.length()) {
                        val article = articlesArray.getJSONObject(i)
                        articleList.add(Article(
                            id = article.getInt("id"),
                            title = article.getString("title"),
                            authors = article.getString("authors"),
                            abstract = article.getString("abstract"),
                            pdf = article.getString("pdf")
                        ))
                    }
                    updateRecyclerView(articleList)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error parsing JSON: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                error.printStackTrace()
                Toast.makeText(this, "Error fetching data: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )
        VolleySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest)
    }

    private fun filter(text: String) {
        val filteredList = arrayListOf<Article>()
        for (item in articleList) {
            if (item.title.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))) {
                filteredList.add(item)
            }
        }
        updateRecyclerView(filteredList)
    }

    private fun updateRecyclerView(filteredArticles: List<Article>) {
        val recyclerView = findViewById<RecyclerView>(R.id.articlesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ArticleListAdapter(articleList)
        recyclerView.adapter = ArticleListAdapter(filteredArticles)
        recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun showCameraPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Camera Permission")
            .setMessage("Do you accept to open the camera?")
            .setPositiveButton("Yes") { dialog, which ->
                checkCameraPermission()
            }
            .setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
        } else {
            Toast.makeText(this, "Camera not supported", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            // Handle the camera result here if needed
            Toast.makeText(this, "Camera opened successfully", Toast.LENGTH_SHORT).show()
        }
    }
}



class ArticleListAdapter(private var articles: List<Article>) :
    RecyclerView.Adapter<ArticleListAdapter.ArticleViewHolder>() {

    fun updateArticles(newArticles: List<Article>) {
        articles = newArticles
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.articles_card, parent, false)
        return ArticleViewHolder(view)
    }


    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        holder.titleTV.text = articles[position].title

        // Set the click listener to launch the details activity
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, articledetail::class.java).apply {
                putExtra("article_id", article.id)
                putExtra("title", article.title)
                putExtra("authors", articles[position].authors)
                putExtra("abstract", articles[position].abstract)
                putExtra("pdf", articles[position].pdf)

            }
            context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int = articles.size

    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTV: TextView = itemView.findViewById(R.id.articletitletv)
    }
}


data class Article(
    val id: Int,
    val title: String,
    val authors: String,
    val abstract: String,
    val pdf: String
)