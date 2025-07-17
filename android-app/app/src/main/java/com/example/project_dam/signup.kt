package com.example.project_dam

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class signup : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val fullnameEditText: EditText = findViewById(R.id.fullname)
        val emailEditText: EditText = findViewById(R.id.email)
        val usernameEditText: EditText = findViewById(R.id.username)
        val passwordEditText: EditText = findViewById(R.id.password)
        val textViewError: TextView = findViewById(R.id.errormessage)
        val signInTextView: TextView = findViewById(R.id.backtosignin)
        val signUpButton: Button = findViewById(R.id.buttonsignup)

        // Handling window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        signInTextView.setOnClickListener {
            val intent = Intent(this, signin::class.java)
            startActivity(intent)
        }

        signUpButton.setOnClickListener {
            textViewError.visibility = View.GONE

            val fullname = fullnameEditText.text.toString()
            val email = emailEditText.text.toString()
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            val queue = Volley.newRequestQueue(applicationContext)
            val url = "http://10.0.2.2/summithub/register.php"

            val stringRequest = object : StringRequest(
                Request.Method.POST, url,
                Response.Listener { response ->
                    if (response.equals("success", ignoreCase = true)) {
                        Toast.makeText(applicationContext, "Registration successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(applicationContext, Homepage::class.java)
                        startActivity(intent)
                    } else {
                        textViewError.text = response
                        textViewError.visibility = View.VISIBLE
                    }
                },
                Response.ErrorListener { error ->
                    textViewError.text = error.localizedMessage
                    textViewError.visibility = View.VISIBLE
                }) {
                override fun getParams(): Map<String, String>? {
                    val paramV: MutableMap<String, String> = HashMap()
                    paramV["fullname"] = fullname
                    paramV["email"] = email
                    paramV["username"] = username
                    paramV["password"] = password
                    return paramV
                }
            }
            queue.add(stringRequest)
        }
    }
}

