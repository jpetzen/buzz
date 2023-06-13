package si.uni_lj.fe.tnuv.oleae

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Observable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.rxbinding2.widget.RxTextView
import si.uni_lj.fe.tnuv.oleae.databinding.ActivityLoginBinding

@SuppressLint("CheckResult")
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Authentication
        auth= FirebaseAuth.getInstance()


        //Email Validation
        val usernameStream = binding.etEmail.editText?.let {
            RxTextView.afterTextChangeEvents(it)
            .skipInitialValue()
            .map{ event->
                val username = event.view().text.toString()
                    username.isEmpty()
            }
        }
        usernameStream?.subscribe{
            showTextMinimalAlert(it, "Email/Username")
        }

        //Password Validation
        val passwordStream = binding.etPassword.editText?.let {
            RxTextView.afterTextChangeEvents(it)
                .skipInitialValue()
                .map { event ->
                    val password = event.view().text.toString()
                    password.isEmpty()
                }
        }
        passwordStream?.subscribe{
            showTextMinimalAlert(it, "Password")
        }

        //Button enable true/false
        val invalidFieldStream = io.reactivex.Observable.combineLatest(
            usernameStream,
            passwordStream
        ) { usernameInvalid: Boolean, passwordInvalid: Boolean ->
            !usernameInvalid && !passwordInvalid
        }
        invalidFieldStream.subscribe{ isValid ->
            if (isValid) {
                binding.btnSignIn.isEnabled=true
                binding.btnSignIn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.darker_blue)
            }else{
                binding.btnSignIn.isEnabled=false
                binding.btnSignIn.backgroundTintList = ContextCompat.getColorStateList(this,android.R.color.darker_gray)
            }
        }

        //on click
        binding.btnSignIn.setOnClickListener{
            val email = binding.etEmail.editText.toString().trim()
            val password = binding.etPassword.editText.toString().trim()
            loginUser(email,password)
        }
        binding.tvSignUp.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.tvForgotPassword.setOnClickListener{
            startActivity(Intent(this,ResetPasswordActivity::class.java))
        }
    }
    private fun showTextMinimalAlert(isNotValid: Boolean, text: String){
        if(text == "Email/Username")
            binding.etEmail.error=if (isNotValid) "$text field can't be empty!" else null
        else if (text == "Password")
            binding.etPassword.error=if (isNotValid) "$text field can't be empty!" else null
    }
    private fun loginUser(email:String, password:String){
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){login->
                if (login.isSuccessful){
                    Intent(this,HomeActivity::class.java).also{
                        it.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                        Toast.makeText(this,"Login successful", Toast.LENGTH_SHORT).show()
                    }
                }else {
                    Toast.makeText(this,login.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }
}