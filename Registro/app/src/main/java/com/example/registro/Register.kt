package com.example.registro

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class Register : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvGoToLogin: TextView
    private lateinit var sharedPreferences: SharedPreferences

    // Variables para estado de validación
    private var isNameValid = false
    private var isEmailValid = false
    private var isPhoneValid = false
    private var isPasswordValid = false
    private var isConfirmPasswordValid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        // Referencias a los views
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmailRegister)
        etPhone = findViewById(R.id.etPhone)
        etPassword = findViewById(R.id.etPasswordRegister)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvGoToLogin = findViewById(R.id.tvGoToLogin)

        // Agregar listeners para validación en tiempo real
        setupTextWatchers()

        // Evento del botón registrar
        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val password = etPassword.text.toString().trim()

            saveUser(name, email, phone, password)
        }

        // Ir al login
        tvGoToLogin.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupTextWatchers() {
        // Validador de nombre (solo letras)
        etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val name = s.toString().trim()
                isNameValid = validateName(name)
                updateRegisterButtonState()
            }
        })

        // Validador de email
        etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val email = s.toString().trim()
                isEmailValid = validateEmail(email)
                updateRegisterButtonState()
            }
        })

        // Validador de teléfono
        etPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val phone = s.toString().trim()
                isPhoneValid = validatePhone(phone)
                updateRegisterButtonState()
            }
        })

        // Validador de contraseña
        etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val password = s.toString()
                isPasswordValid = validatePassword(password)
                // También validar confirmación cuando cambia la contraseña
                val confirmPassword = etConfirmPassword.text.toString()
                isConfirmPasswordValid = validateConfirmPassword(password, confirmPassword)
                updateRegisterButtonState()
            }
        })

        // Validador de confirmación de contraseña
        etConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val password = etPassword.text.toString()
                val confirmPassword = s.toString()
                isConfirmPasswordValid = validateConfirmPassword(password, confirmPassword)
                updateRegisterButtonState()
            }
        })
    }

    private fun validateName(name: String): Boolean {
        return when {
            name.isEmpty() -> {
                etName.error = "El nombre es requerido"
                false
            }
            !name.matches(Regex("^[a-zA-ZáéíóúñÑÁÉÍÓÚ\\s]+$")) -> {
                etName.error = "El nombre solo puede contener letras y espacios"
                false
            }
            else -> {
                etName.error = null
                true
            }
        }
    }

    private fun validateEmail(email: String): Boolean {
        return when {
            email.isEmpty() -> {
                etEmail.error = "El correo es requerido"
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                etEmail.error = "Correo electrónico inválido"
                false
            }
            else -> {
                etEmail.error = null
                true
            }
        }
    }

    private fun validatePhone(phone: String): Boolean {
        return when {
            phone.isEmpty() -> {
                etPhone.error = "El teléfono es requerido"
                false
            }
            !phone.matches(Regex("^\\d{10}$")) -> {
                etPhone.error = "El teléfono debe tener exactamente 10 dígitos numéricos"
                false
            }
            else -> {
                etPhone.error = null
                true
            }
        }
    }

    private fun validatePassword(password: String): Boolean {
        return when {
            password.isEmpty() -> {
                etPassword.error = "La contraseña es requerida"
                false
            }
            password.length < 6 -> {
                etPassword.error = "La contraseña debe tener al menos 6 caracteres"
                false
            }
            else -> {
                etPassword.error = null
                true
            }
        }
    }

    private fun validateConfirmPassword(password: String, confirmPassword: String): Boolean {
        return when {
            confirmPassword.isEmpty() -> {
                etConfirmPassword.error = "Confirma tu contraseña"
                false
            }
            password != confirmPassword -> {
                etConfirmPassword.error = "Las contraseñas no coinciden"
                false
            }
            else -> {
                etConfirmPassword.error = null
                true
            }
        }
    }

    private fun updateRegisterButtonState() {
        val allValid = isNameValid && isEmailValid && isPhoneValid &&
                isPasswordValid && isConfirmPasswordValid

        btnRegister.isEnabled = allValid
        btnRegister.alpha = if (allValid) 1.0f else 0.5f
    }

    private fun saveUser(name: String, email: String, phone: String, password: String) {
        // Guardar datos del usuario
        sharedPreferences.edit().apply {
            putString("user_name", name)
            putString("user_email", email)
            putString("user_phone", phone)
            putString("user_password", password)
            apply()
        }

        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_LONG).show()

        // Regresar al login
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }
}