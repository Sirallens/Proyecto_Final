package com.worksu.unlimited.app2

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.SwitchCompat
import android.util.Log
import android.widget.CompoundButton
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener
{


    private var refreshSwitch: Boolean = true
    private val username: String = "ben"
    private val password: String = "benpass"
    private val sender: String = "ANDROID"

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        switchRain.isClickable = false
        switchMotion.isClickable = false


        switchPhoto.setOnClickListener {

            update()

        }



        switchSprinkle.setOnClickListener {

            update()

        }



        swipeLayout.setOnRefreshListener {

            update()
            swipeLayout.isRefreshing = false

        }
    }









    override fun onRefresh() {
        update()
        swipeLayout.isRefreshing = false
    }


    private fun update()
    {
        // Get status of SW1 and LED1
        val Sprinkle_status = if (switchSprinkle.isChecked) 1 else 0
        val Photo_status = if (switchPhoto.isChecked) 1 else 0

        val url = "https://unlimitedphelotelia.000webhostapp.com/scripts/sync_app_data.php"
        val jsonObject = JSONObject()
        jsonObject.put("username", "ben")
        jsonObject.put("password", "benpass")
        jsonObject.put("SW1", Sprinkle_status)
        jsonObject.put("SW2", Photo_status)
        jsonObject.put("sender", "ANDROID")
        Toast.makeText(this, jsonObject.toString(), Toast.LENGTH_LONG).show()

        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url,
                jsonObject,   Response.Listener {response ->

                    if (response["success"] == 1) {

                       // Set the statuses to the ones received
                        //refreshSwitch = false
                        switchRain.isChecked = (response.get("rain") == 1)
                        switchMotion.isChecked = (response.get("motion") == 1)
                        switchPhoto.isChecked = false
                        //refreshSwitch = true

                        if((switchMotion.isChecked == true || switchRain.isChecked == true) && switchSprinkle.isChecked == true)
                        {
                            showDialog()
                            update()
                        }
                    }
                    else
                    {
                        Toast.makeText(this, "It failed...",
                                Toast.LENGTH_LONG).show()
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()

                })



        Volley.newRequestQueue(this).add(jsonObjectRequest)


    }


    private fun showDialog() {

        val popup: AlertDialog.Builder = AlertDialog.Builder(this)
        popup.setTitle("Warning!")
        popup.setMessage("Either movement or rain is detected. \n Do you still want to activate the Sprinkle?")
        popup.setPositiveButton("Yes I do.", {dialog, which ->
            Toast.makeText(this, "You clicked OK", Toast.LENGTH_LONG).show()

        })
        popup.setNegativeButton("No.", { dialog, which -> switchSprinkle.isChecked = false
            Toast.makeText(this, "You clicked 'No', activation has been cancelled. ", Toast.LENGTH_LONG).show()
        })
        popup.show()
    }

    private fun showDialog2() {

        val popup: AlertDialog.Builder = AlertDialog.Builder(this)
        popup.setTitle("Warning!")
        popup.setMessage("Movement is detected. \n Do you want to turn off  Sprinkle?")
        popup.setPositiveButton("Yes I do.", {dialog, which -> switchSprinkle.isChecked = false
            Toast.makeText(this, "You clicked OK. Sprinkle turned off", Toast.LENGTH_LONG).show()

        })
        popup.setNegativeButton("No.", { dialog, which -> switchSprinkle.isChecked = false
            Toast.makeText(this, "You clicked 'No'. ", Toast.LENGTH_LONG).show()
        })
        popup.show()
    }


























}
