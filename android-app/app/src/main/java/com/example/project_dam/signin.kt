package com.example.project_dam

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject



class signin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        val usernameEditText: EditText = findViewById(R.id.username)
        val passwordEditText: EditText = findViewById(R.id.password)
        val textViewError: TextView = findViewById(R.id.errormessage)
        val signInButton: Button = findViewById(R.id.buttonsignin)

        val sharedPreferences = getSharedPreferences("SummitHub", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
        val isLoggedIn = sharedPreferences.getBoolean("logged", false)

        if (isLoggedIn) {
            startActivity(Intent(this, Homepage::class.java))
            finish()
        }


        signInButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()

            //startActivity(intent)
            val password = passwordEditText.text.toString().trim()

            textViewError.visibility = View.GONE
            val queue = Volley.newRequestQueue(this)
            val url = "http://10.0.2.2/summithub/login.php"

            val stringRequest = object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    try {
                        val jsonObject = JSONObject(response)
                        val status = jsonObject.getString("status")
                        val message = jsonObject.getString("message")
                        if (status == "success") {
                            val fullname = jsonObject.getString("fullname")
                            val email = jsonObject.getString("email")
                            val apiKey = jsonObject.getString("apiKey")
                            val user_id = jsonObject.getInt("user_id")

                            with(sharedPreferences.edit()) {
                                putString("logged", "true")
                                putString("fullname", fullname)
                                putString("email", email)
                                putString("username", username)
                                putString("apiKey", apiKey)
                                putInt("user_id", user_id)
                                apply()
                            }

                            startActivity(Intent(this@signin, Homepage::class.java))
                            finish()
                        } else {
                            textViewError.text = message
                            textViewError.visibility = View.VISIBLE
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        textViewError.text = "Error parsing the response"
                        textViewError.visibility = View.VISIBLE
                    }
                },
                Response.ErrorListener { error ->
                    textViewError.text = error.localizedMessage ?: "Network error"
                    textViewError.visibility = View.VISIBLE
                }) {
                override fun getParams(): Map<String, String> {
                    return hashMapOf("username" to username, "password" to password)
                }
            }
            queue.add(stringRequest)
        }

        findViewById<TextView>(R.id.signup).setOnClickListener {
            startActivity(Intent(this, signup::class.java))
        }




    }
}

