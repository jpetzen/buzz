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
import si.uni_lj.fe.tnuv.oleae.databinding.ActivityRegisterBinding

@SuppressLint("CheckResult")
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth:FirebaseAuth

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Authentication
        auth= FirebaseAuth.getInstance()


        //Username Validation
        val usernameStream = binding.etUserName.editText?.let {
            RxTextView.afterTextChangeEvents(it)
                .skipInitialValue()
                .map { event ->
                    val usernameStream = event.view().text.toString()
                        usernameStream.length < 6
                        usernameStream.isEmpty()
                }
        }
        usernameStream?.subscribe {
            showTextMinimalAlert(it, "UserName")
        }

        //Email Validation
        val emailStream = binding.etEmail.editText?.let{
            RxTextView.afterTextChangeEvents(it)
            .skipInitialValue()
            .map { event ->
                val email=event.view().text.toString()
                    email.isEmpty()
                    !Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }

        }
        emailStream?.subscribe{
            showEmailValidAlert(it)
        }

        //Password Validation
        val passwordStream = binding.etPassword.editText?.let{
            RxTextView.afterTextChangeEvents(it)
                .skipInitialValue()
                .map{event->
                    val password = event.view().text.toString()
                        password.isEmpty()
                        password.length < 6
                }
        }
        passwordStream?.subscribe{
            showTextMinimalAlert(it, "Password")
        }

        //Button enable true/false
        val invalidFieldsStream = io.reactivex.Observable.combineLatest(
            emailStream,
            usernameStream,
            passwordStream
        ) { usernameInvalid: Boolean, emailInvalid: Boolean, passwordInvalid: Boolean ->
            !usernameInvalid && !emailInvalid && !passwordInvalid
        }
        invalidFieldsStream.subscribe{ isValid ->
            if (isValid) {
                binding.btnRegister.isEnabled=true
                binding.btnRegister.backgroundTintList = ContextCompat.getColorStateList(this, R.color.darker_blue)
            }else{
                binding.btnRegister.isEnabled=false
                binding.btnRegister.backgroundTintList = ContextCompat.getColorStateList(this,android.R.color.darker_gray)
            }
        }

        //on click
        binding.btnRegister.setOnClickListener{
            val email = binding.etEmail.editText.toString().trim()
            val password = binding.etPassword.editText.toString().trim()
            registerUser(email,password)
        }
        binding.tvHaveAccount.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    //private fun showNameExistAlert(isNotValid:Boolean){
    //    binding.etf.error=if(isNotValid)"Name already in use" else null
    //}
    private fun showTextMinimalAlert(isNotValid: Boolean, text: String){
        if (text == "UserName")
            binding.etUserName.error=if (isNotValid) "$text is not valid" else null
        else if (text == "Password")
            binding.etPassword.error=if (isNotValid) "$text is not valid" else null
        else if (text == "email")
            binding.etEmail.error=if (isNotValid) "$text is not valid" else null
    }

    private fun showEmailValidAlert(isNotValid:Boolean){
        binding.etEmail.error=if (isNotValid) "Email is not valid" else null
    }
    private fun registerUser(email:String, password:String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){
                if(it.isSuccessful){
                    startActivity(Intent(this,LoginActivity::class.java))
                    Toast.makeText(this, "Successful register",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, it.exception?.message,Toast.LENGTH_SHORT).show()
                }
            }
    }
}