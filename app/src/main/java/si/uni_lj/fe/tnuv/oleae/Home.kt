package si.uni_lj.fe.tnuv.oleae

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.getSystemService
import si.uni_lj.fe.tnuv.oleae.notifications.ClassificationNotificationService
import javax.inject.Inject

class Home : Fragment() {
    @Inject
    lateinit var bluetoothAdapter: BluetoothAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        setContent {
                    onBluetoothStateChanged = {
                        showBluetoothDialog()
                    }

            }
        }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                ClassificationNotificationService.CLASSIFICATION_CHANNEL_ID,
                "Classification",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Used for detection notification"

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onStart() {
        super.onStart()
        showBluetoothDialog()
    }

    private var isBluetoothDialogAlreadyShown = false
    private fun showBluetoothDialog(){
        if(!bluetoothAdapter.isEnabled){
            if(!isBluetoothDialogAlreadyShown){
                val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startBluetoothIntentForResult.launch(enableBluetoothIntent)
                isBluetoothDialogAlreadyShown = true
            }
        }
    }

    private val startBluetoothIntentForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            isBluetoothDialogAlreadyShown = false
            if(result.resultCode != Activity.RESULT_OK){
                showBluetoothDialog()
            }
        }

}