package com.example.project_dam

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
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
import com.bumptech.glide.Glide
import  android.Manifest
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDateTime

class Homepage : AppCompatActivity() {

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

    private var conferenceList: ArrayList<Conference> = arrayListOf()
    private val CAMERA_REQUEST_CODE = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_homepage)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets


        }

        findViewById<ImageView>(R.id.homepageUserImage).setOnClickListener {
            startActivity(Intent(this, userpage::class.java))
        }

        val usersharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val imageUrl = usersharedPreferences.getString("profileImageUrl", null) // Pega o caminho da imagem salvo

        // Use Glide to load the image
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.baseline_person_24) // Use baseline_person_24 as a placeholder
            .error(R.drawable.baseline_person_24) // Use baseline_person_24 as an error image if loading fails
            .into(findViewById<ImageView>(R.id.homepageUserImage)) // Load into the ImageView in the homepage

        val sharedPreferences = getSharedPreferences("SummitHub", Context.MODE_PRIVATE)
        val fullname = sharedPreferences.getString("fullname", "User") ?: "User"

        val firstName = extractFirstName(fullname)

        val textViewGreeting = findViewById<TextView>(R.id.textViewGreeting)
        textViewGreeting.text = "Welcome, $firstName!"

        fetchConference()

        val articalesbutton : ImageView = findViewById(R.id.articleIcon)
        articalesbutton.setOnClickListener {
            startActivity(Intent(this, Articales::class.java))

        }

        val scheduleicon: ImageView = findViewById(R.id.scheduleIcon)
        scheduleicon.setOnClickListener {
            startActivity(Intent(this, Schedule::class.java))
        }

        val mapicon: ImageView = findViewById(R.id.mapIcon)
        mapicon.setOnClickListener {
            startActivity(Intent(this, map::class.java))
        }

        val buttonOpenCamera: ImageView = findViewById(R.id.qrIcon)
        buttonOpenCamera.setOnClickListener {
            showCameraPermissionDialog()
        }
    }

    private fun extractFirstName(fullName: String): String {
        return fullName.split(" ").first()
    }

    private fun updateConferenceView(conference: Conference) {
        val conferenceNameTextView: TextView = findViewById(R.id.conferencenametv)
        val conferenceInfoTextView: TextView = findViewById(R.id.informationtv)

        conferenceNameTextView.text = conference.name
        conferenceInfoTextView.text = conference.information
    }

    private fun fetchConference() {
        val url = "http://10.0.2.2/summithub/api.php"
        val requestJson = JSONObject()
        requestJson.put("action", "getEntries")
        requestJson.put("table", "conferences")

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, requestJson,
            { response ->
                try {
                    val conferenceArray = response.getJSONArray("response")
                    if (conferenceArray.length() > 0) {
                        val conferenceData = conferenceArray.getJSONObject(0)
                        val conference = Conference(
                            id = conferenceData.getInt("id"),
                            name = conferenceData.getString("name"),
                            information = conferenceData.getString("information")
                        )
                        updateConferenceView(conference)
                    }
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
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
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

data class Conference(
    val id: Int,
    val name: String,
    val information: String
)