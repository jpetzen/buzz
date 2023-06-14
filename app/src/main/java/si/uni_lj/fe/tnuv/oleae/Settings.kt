package si.uni_lj.fe.tnuv.oleae

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.auth.FirebaseAuth



class Settings : PreferenceFragmentCompat () {

    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPrefManager: SharedPrefManager

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        // Authentication
        auth = FirebaseAuth.getInstance()

        // Initialize SharedPrefManager
        sharedPrefManager = SharedPrefManager(requireContext())

        // Logout preference
        val logoutPreference = findPreference<Preference>("logout_preference")
        logoutPreference?.setOnPreferenceClickListener {
            // Sign out from Firebase
            auth.signOut()

            // Clear shared preferences
            sharedPrefManager.logout()

            Intent(requireContext(), MainActivity::class.java).also { intent ->
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                Toast.makeText(requireContext(), "Logout successful", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }
}