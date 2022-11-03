package com.example.ugd89_a_10681_project1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.annotation.SuppressLint
import android.hardware.Camera
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast

import android.graphics.Color
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {
    private var mCamera: Camera? = null
    private var mCameraView: CameraView? = null

    lateinit var proximitySensor: Sensor
    lateinit var sensorManager: SensorManager

    var proximitySensorEventListener: SensorEventListener? = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            //
        }

        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_PROXIMITY) {
                if (event.values[0]==0f) {
                    mCamera?.stopPreview()
                    mCamera?.release()
                    mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT)
                    mCameraView = CameraView(this@MainActivity, mCamera!!)
                    val change_camera = findViewById<View>(R.id.FLCamera) as FrameLayout
                    change_camera.addView(mCameraView)
                } else {
                    mCamera?.stopPreview()
                    mCamera?.release()
                    mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK)
                    mCameraView = CameraView(this@MainActivity, mCamera!!)
                    val change_camera = findViewById<View>(R.id.FLCamera) as FrameLayout
                    change_camera.addView(mCameraView)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // PROXIMITY CODE
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        if (proximitySensor == null) {
            Toast.makeText(this, "No proximity sensor found in device..", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            sensorManager.registerListener(
                proximitySensorEventListener,
                proximitySensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        // CAMERA CODE
        try {
            mCamera = Camera.open()
        } catch (e: Exception) {
            Log.d("Error", "Failed to get Camera" + e.message)
        }
        if (mCamera != null) {
            mCameraView = CameraView(this, mCamera!!)
            val camera_view = findViewById<View>(R.id.FLCamera) as FrameLayout
            camera_view.addView(mCameraView)
        }
        @SuppressLint("MissingInflatedId", "LocalSuppress") val imageClose =
            findViewById<View>(R.id.imgClose) as ImageButton
        imageClose.setOnClickListener { view: View? -> System.exit(0) }

    }

}