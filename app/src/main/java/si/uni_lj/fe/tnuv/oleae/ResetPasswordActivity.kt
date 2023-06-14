package si.uni_lj.fe.tnuv.oleae

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.rxbinding2.widget.RxTextView
import si.uni_lj.fe.tnuv.oleae.databinding.ActivityResetPasswordBinding

@SuppressLint("CheckResult")
class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Email Validation
        val emailStream = binding.etEmail.editText?.let {
            RxTextView.afterTextChangeEvents(it)
                .skipInitialValue()
                .map { event ->
                    val email = event.view().text.toString()
                    email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                }
        }
        emailStream?.subscribe {
            showEmailValidAlert(it)
        }

        // Reset password
        binding.btnResetPwd.setOnClickListener {
            val email = binding.etEmail.editText?.text.toString().trim()

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this) { reset ->
                    if (reset.isSuccessful) {
                        Intent(this, LoginActivity::class.java).also {
                            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(it)
                            Toast.makeText(
                                this,
                                "Check your email for password reset.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(this, reset.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Click
        binding.tvBackLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun showEmailValidAlert(isNotValid: Boolean) {
        if (isNotValid) {
            binding.etEmail.error = "Email invalid"
            binding.btnResetPwd.isEnabled = false
            binding.btnResetPwd.backgroundTintList =
                ContextCompat.getColorStateList(this, android.R.color.darker_gray)
        } else {
            binding.etEmail.error = null
            binding.btnResetPwd.isEnabled = true
            binding.btnResetPwd.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.darker_blue)
        }
    }
}
