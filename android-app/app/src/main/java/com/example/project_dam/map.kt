package com.example.project_dam

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONException

class map : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val point = LatLng(38.73240225798916, -9.160349629389367) // Default location
        mMap.addMarker(MarkerOptions().position(point).title("NovaIms"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 10f))
        getPlaces()
    }

    private fun getPlaces() {
        val url = getString(R.string.url) // URL to your rooms.php
        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    for (i in 0 until response.length()) {
                        val jresponse = response.getJSONObject(i)
                        val id = jresponse.getInt("id")
                        val name = jresponse.getString("name")
                        val address = jresponse.getString("address")
                        val latitude = jresponse.getDouble("latitude")
                        val longitude = jresponse.getDouble("longitude")
                        val marker = mMap.addMarker(
                            MarkerOptions()
                                .position(LatLng(latitude, longitude))
                                .title(name)
                                .snippet(address)
                        )
                        marker?.tag = id
                    }
                } catch (je: JSONException) {
                    Toast.makeText(this, "Parsing error: ${je.message}", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(this, "Volley error: ${error.toString()}", Toast.LENGTH_LONG).show()
            }
        )
        queue.add(jsonObjectRequest)
    }
}

