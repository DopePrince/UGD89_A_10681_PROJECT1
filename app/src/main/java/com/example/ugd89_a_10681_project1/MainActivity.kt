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

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationCompat

class MainActivity : AppCompatActivity(), SensorEventListener {
    private var mCamera: Camera? = null
    private var mCameraView: CameraView? = null

    lateinit var proximitySensor: Sensor
    lateinit var sensorManager: SensorManager

    private val CHANNEL_ID_1 = "channel_notification_01"
    private val notificationId1 = 101

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

        // ACCELEROMETER CODE
        setUpSensorStuff()

        // NOTIFICATION CODE
        createNotificationChannel()
    }

    // ACCELEROMETER CODE
    private fun setUpSensorStuff() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
    }

    // ACCELEROMETER CODE
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val sides = event.values[0]
            val upDown = event.values[1]

            if (upDown.toInt() == 0 && sides.toInt() == 0) Color.GREEN else Color.RED

            if(upDown.toInt() > 5 || sides.toInt() > 5){
                sendNotification()
            }
        }
    }

    // ACCELEROMETER CODE
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    // NOTIFICATION CODE
    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Notification Title"
            val descriptionText = "Notification Description"

            val channel1 = NotificationChannel(CHANNEL_ID_1, name, NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel1)
        }
    }

    // NOTIFICATION CODE
    private fun sendNotification(){
        val builder = NotificationCompat.Builder(this, CHANNEL_ID_1)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentText("Selamat anda sudah berhasil mengerjakan Modul 8 dan 9 ")
            .setContentTitle("Modul89_A_10681_PROJECT2")
            .setPriority(NotificationCompat.PRIORITY_LOW)

        with(NotificationManagerCompat.from(this)){
            notify(notificationId1, builder.build())
        }
    }

    // ACCELEROMETER CODE
    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }

}