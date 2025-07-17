package com.example.project_dam

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Schedule : AppCompatActivity() {

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

    private val CAMERA_REQUEST_CODE = 100
    private var sessionsList: ArrayList<Session> = arrayListOf()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_schedule)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fetchSessions()


        val homeIcon: ImageView = findViewById(R.id.homeIcon)
        homeIcon.setOnClickListener {
            val intent = Intent(this, Homepage::class.java)
            startActivity(intent)
        }

        val articalesbutton: ImageView = findViewById(R.id.articleIcon)
        articalesbutton.setOnClickListener {
            startActivity(Intent(this, Articales::class.java))

        }


        val buttonOpenCamera: ImageView = findViewById(R.id.qrIcon)
        buttonOpenCamera.setOnClickListener {
            showCameraPermissionDialog()
        }

        val usericon : ImageView = findViewById(R.id.homepageUserImage)
        usericon.setOnClickListener {
            startActivity(Intent(this, userpage::class.java))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchSessions() {
        val url = "http://10.0.2.2/summithub/api.php"
        val sendQuestionJsonRequest = JSONObject()
        sendQuestionJsonRequest.put("action", "getSchedule")

        val jsonArrayRequest = JsonObjectRequest(
            Request.Method.POST, url, sendQuestionJsonRequest,
            { response ->
                try {
                    val sessionjsonArray = response.getJSONArray("response")
                    val datetimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    //Log.d("API Response", "Number of sessions: ${sessionjsonArray.length()}")
                    for (i in 0 until sessionjsonArray.length()) {
                        val jsonObject = sessionjsonArray.getJSONObject(i)
                        val articlejsonArray = jsonObject.getJSONArray("articles")
                        val session = Session(
                            id = jsonObject.getInt("id"),
                            title = jsonObject.getString("title"),
                            datetime_start = LocalDateTime.parse(jsonObject.getString("datetime_start"), datetimeFormatter),
                            datetime_end = LocalDateTime.parse(jsonObject.getString("datetime_end"), datetimeFormatter),
                            room_name = jsonObject.optString("room_name"),
                            article1_title = articlejsonArray.getJSONObject(0).getString("title"),
                            article1_author = articlejsonArray.getJSONObject(0).getString("authors"),
                            article2_title = articlejsonArray.getJSONObject(1).getString("title"),
                            article2_author = articlejsonArray.getJSONObject(1).getString("authors")
                        )
                        sessionsList.add(session)
                        Log.d("Added Session", "Session ${session.title} added")
                    }
                    updateRecyclerView()
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error parsing JSON", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                error.printStackTrace()
                Toast.makeText(this, "Error fetching data: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )
        VolleySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest)
    }

    private fun updateRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.sessionsRecyclerView)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        val adapter = SessionListAdapter(sessionsList, object : SessionListAdapter.OnItemClickListener {
            override fun onItemClick(session: Session) {
                Toast.makeText(this@Schedule, "Clicked: ${session.title}", Toast.LENGTH_SHORT).show()
            }
        })
        recyclerView?.adapter = adapter
        adapter.notifyDataSetChanged()

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

class SessionListAdapter(private var sessionList: List<Session>, private val
listener: OnItemClickListener) :
    RecyclerView.Adapter<SessionListAdapter.SessionViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(session: Session)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            SessionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sessions_card, parent, false)
        return SessionViewHolder(view)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val session = sessionList[position]
        holder.bind(session)
    }
    override fun getItemCount(): Int = sessionList.size
    inner class SessionViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val dayTV: TextView = itemView.findViewById(R.id.daytv)
        val titleTV: TextView = itemView.findViewById(R.id.titletv)
        val timeTV: TextView = itemView.findViewById(R.id.timetv)
        val roomTV: TextView = itemView.findViewById(R.id.roomtv)
        val article1TV: TextView = itemView.findViewById(R.id.article1titletv)
        val author1TV: TextView = itemView.findViewById(R.id.author1tv)
        val article2TV: TextView = itemView.findViewById(R.id.article2titletv)
        val author2TV: TextView = itemView.findViewById(R.id.author2tv)

        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(sessionList[position])
            }
        }
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(session: Session) {
            val datetimeFormatterHours = DateTimeFormatter.ofPattern("HH:mm")
            val datetimeFormatterDays = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            dayTV.text = "${session.datetime_start.format(datetimeFormatterDays)}"
            titleTV.text = session.title
            timeTV.text = "${session.datetime_start.format(datetimeFormatterHours)} - ${session.datetime_end.format(datetimeFormatterHours)}"
            roomTV.text = "Room: ${session.room_name}"
            article1TV.text = session.article1_title
            author1TV.text = "Author: ${session.article1_author}"
            article2TV.text = session.article2_title
            author2TV.text = "Author: ${session.article2_author}"
        }
    }

    fun setFilteredList(sessionList: List<Session>) {
        this.sessionList = sessionList
        notifyDataSetChanged()
    }
}

data class Session(
    val id: Int,
    val title: String,
    val datetime_start: LocalDateTime,
    val datetime_end: LocalDateTime,
    val room_name: String?,
    val article1_title: String,
    val article1_author: String,
    val article2_title: String,
    val article2_author: String
)