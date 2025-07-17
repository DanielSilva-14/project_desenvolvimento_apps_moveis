package com.example.project_dam

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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

class articledetail : AppCompatActivity() {

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

    private lateinit var commentEditText: EditText
    private lateinit var submitButton: Button

    private lateinit var questionsAdapter: QuestionListAdapter
    private lateinit var questionsRecyclerView: RecyclerView

    private val CAMERA_REQUEST_CODE = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_articledetail) // Make sure this layout file contains your TextView

        val titleTextView: TextView = findViewById(R.id.articleTitleTextView) // The ID must match the TextView ID in your layout
        val authorsTextView: TextView = findViewById(R.id.articleAuthorsTextView)
        val abstractTextView: TextView = findViewById(R.id.articleAbstractTextView)

        // Get the intent that started this activity and extract the string
        intent?.let {
            val title = it.getStringExtra("title")
            val authors = it.getStringExtra("authors")
            val abstract = it.getStringExtra("abstract")
            val pdfUrl = it.getStringExtra("pdf")

            titleTextView.text = title
            authorsTextView.text = authors
            abstractTextView.text = abstract

            val pdfTextView: TextView = findViewById(R.id.pdftv)
            pdfTextView.setOnClickListener {
                // Use the previously captured pdfUrl here
                val pdfIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://10.0.2.2/summithub/articles/${pdfUrl}"))
                startActivity(pdfIntent)
            }

        } ?: run {
            Toast.makeText(this, "No article data provided", Toast.LENGTH_SHORT).show()
        }

        val commentEditText = findViewById<EditText>(R.id.multilinecomment)
        val submitButton = findViewById<Button>(R.id.buttonsubmit)
        submitButton.setOnClickListener {
            val content = commentEditText.text.toString()
            if (content.isNotEmpty()) {
                sendComment(content)
            } else {
                Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        initCommentsViews()
        fetchQuestions()

        val backTextView = findViewById<TextView>(R.id.backTextView)
        backTextView.setOnClickListener {
            finish() // Closes the current activity and returns to the previous one
        }

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

    private fun initViews() {
        commentEditText = findViewById(R.id.multilinecomment)
        submitButton = findViewById(R.id.buttonsubmit)
    }

    private fun setupListeners() {
        submitButton.setOnClickListener {
            val content = commentEditText.text.toString()
            if (content.isNotEmpty()) {
                sendComment(content)
            } else {
                Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendComment(content: String) {
        val url = "http://10.0.2.2/summithub/api.php"
        val requestJson = JSONObject().apply {
            put("action", "sendQuestion")
            put("user_id", getUserID())  // Fetch the user ID stored in SharedPreferences or passed through intent
            put("article_id", getArticleID())  // Fetch the article ID passed through intent
            put("content", content)
        }

        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, requestJson,
            { response ->
                Toast.makeText(this, response.getString("message"), Toast.LENGTH_LONG).show()
                commentEditText.text.clear()
            },
            { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            })

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    private fun getUserID(): Int {
        // Retrieve user ID from SharedPreferences or intent
        return this.getSharedPreferences("SummitHub", Context.MODE_PRIVATE).getInt("user_id", 0)
    }

    private fun getArticleID(): Int {
        // Retrieve article ID from intent
        return intent.getIntExtra("article_id", 0)
    }

    private fun initCommentsViews() {
        questionsRecyclerView = findViewById(R.id.commentsRecyclerView)
        questionsRecyclerView.layoutManager = LinearLayoutManager(this)
        questionsAdapter = QuestionListAdapter(ArrayList())
        questionsRecyclerView.adapter = questionsAdapter
    }

    private fun fetchQuestions() {
        val articleId = intent.getIntExtra("article_id", 0)
        val url = "http://10.0.2.2/summithub/api.php"
        val requestJson = JSONObject().apply {
            put("action", "getQuestions")
            put("article_id", articleId)
        }

        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, requestJson,
            { response ->
                val questions = ArrayList<Question>()
                val questionsArray = response.getJSONArray("response")
                for (i in 0 until questionsArray.length()) {
                    val question = questionsArray.getJSONObject(i)
                    questions.add(Question(
                        id = question.getInt("id"),
                        user_id = question.getInt("user_id"),
                        username = question.getString("username"),
                        article_id = question.getInt("article_id"),
                        content = question.getString("content")
                    ))
                }
                questionsAdapter.updateQuestions(questions)
            },
            { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }
}

class QuestionListAdapter(private var questions: List<Question>) : RecyclerView.Adapter<QuestionListAdapter.QuestionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comments_card, parent, false)
        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val question = questions[position]
        holder.bind(question)
    }

    override fun getItemCount() = questions.size

    fun updateQuestions(newQuestions: List<Question>) {
        questions = newQuestions
        notifyDataSetChanged()
    }

    class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(question: Question) {
            itemView.findViewById<TextView>(R.id.commenttv).text = question.content
            itemView.findViewById<TextView>(R.id.usertv).text = question.username
        }
    }
}

data class Question(
    val id: Int,
    val user_id: Int,
    val username: String,
    val article_id: Int,
    val content: String
)








