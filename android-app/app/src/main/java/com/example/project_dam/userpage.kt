package com.example.project_dam

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import  android.Manifest
import android.content.pm.PackageManager
import android.provider.MediaStore
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class userpage : AppCompatActivity() {

    private val CAMERA_REQUEST_CODE = 100
    private lateinit var userimage: ImageView
    private lateinit var editimage: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userpage) // Make sure this layout has the button

        userimage = findViewById(R.id.userimage)
        editimage = findViewById(R.id.editimage)

        editimage.setOnClickListener {

            ImagePicker.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080,
                    1080
                )    //Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }


        val fullnameTextView: TextView = findViewById(R.id.fullname)
        val useremail: TextView = findViewById(R.id.useremail)
        val username: TextView = findViewById(R.id.username)

        // Retrieve the full name from SharedPreferences
        val sharedPreferences = getSharedPreferences("SummitHub", Context.MODE_PRIVATE)
        val fullname = sharedPreferences.getString("fullname", "User")
        val email = sharedPreferences.getString("email", "User")
        val uname = sharedPreferences.getString("username", "User")

        // Set the retrieved name to the TextView
        fullnameTextView.text = fullname
        useremail.text = email
        username.text = uname


        val buttonLogout: Button = findViewById(R.id.buttonlogout)
        buttonLogout.setOnClickListener {
            val sharedPreferences = getSharedPreferences("SummitHub", Context.MODE_PRIVATE)
            sharedPreferences.edit().apply {
                putBoolean("logged", false)
                apply()
            }

            Toast.makeText(this, "You have been logged out.", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, signin::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

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

        val scheduleicon: ImageView = findViewById(R.id.scheduleIcon)
        scheduleicon.setOnClickListener {
            startActivity(Intent(this, Schedule::class.java))
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        userimage.setImageURI(data?.data)
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

   


}
