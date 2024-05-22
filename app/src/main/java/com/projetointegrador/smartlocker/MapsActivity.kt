package com.projetointegrador.smartlocker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val TAG = "MapsActivity"
    private lateinit var gMap:GoogleMap
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_maps)

        firestore = FirebaseFirestore.getInstance()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Request da permissão de local
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        // Enable My Location layer if permission granted
        gMap = googleMap



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            gMap.isMyLocationEnabled = true

            fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14f))
                }

                val layoutInflater = LayoutInflater.from(this)
                val linearLayout = findViewById<androidx.appcompat.widget.LinearLayoutCompat>(R.id.unity)

                firestore.collection("unidades de locação")
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            val lat = document.getDouble("lat")!!
                            val long = document.getDouble("long")!!
                            val nome = document.getString("nome")!!
                            val markerLocation = LatLng(lat, long)

                            val distance = FloatArray(1)
                            if (location != null) {
                                Location.distanceBetween(location.latitude, location.longitude, lat, long, distance)
                            }

                            if(distance[0] / 1000 <= 1000) {

                                val marker = gMap.addMarker(MarkerOptions().position(markerLocation).title("Unidade $nome"))

                                val itemContainer = layoutInflater.inflate(R.layout.unidade_container, linearLayout, false)

                                val title = itemContainer.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tvUnidade)
                                val subtitle = itemContainer.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.tvDisponivel)
                                val button = itemContainer.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btnAction)

                                title.text = "Unidade ${document.getString("nome")}"

                                val isClosed : Boolean = document.getBoolean("isClosed")!!

                                if (isClosed) {
                                    subtitle.text = "Confira os horários de funcionamento"
                                    button.text = "Fechado"
                                } else {

                                    var tA = document.getDouble("totalArmarios")!!.toInt()
                                    var disponivel:  Boolean = false
                                    for (i in 0 until tA){

                                        if (document.getBoolean("A$i")==false){
                                            disponivel = true
                                            break
                                        }
                                    }
                                    if (disponivel){
                                        subtitle.text = "Aberto"
                                        button.text = "Ir até lá"
                                        button.setOnClickListener {
                                            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(markerLocation, 16f)
                                            gMap.animateCamera(cameraUpdate)
                                            marker!!.showInfoWindow()

                                            val unidadeInfoFragment = UnidadeInfoFragment()
                                            val bundle = Bundle()
                                            bundle.putString("nomeUnidade", document.id)
                                            unidadeInfoFragment.arguments = bundle

                                            supportFragmentManager.beginTransaction()
                                                .add(R.id.main, unidadeInfoFragment)
                                                .commit()
                                        }
                                    }else{
                                        subtitle.text = "Sem armários disponíveis"
                                        button.text = "Lotado"
                                    }


                                }

                                linearLayout.addView(itemContainer)
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting documents.", exception)
                    }

            }
        } else {
            // Show rationale and request permission
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
                recreate()
            } else {
                // Permission denied
                //Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


}