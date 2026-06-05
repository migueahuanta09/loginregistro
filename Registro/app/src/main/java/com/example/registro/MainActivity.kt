package com.example.registro

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private lateinit var etContactName: EditText
    private lateinit var etContactPhone: EditText
    private lateinit var btnAddContact: Button
    private lateinit var btnClearFields: Button
    private lateinit var rvContacts: RecyclerView
    private lateinit var navContacts: TextView
    private lateinit var navRecents: TextView
    private lateinit var navFavorites: TextView
    private lateinit var btnLogout: TextView

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var contactAdapter: ContactAdapter

    private var contactsList = mutableListOf<Contact>()
    private var currentFilter = "all" // all, recents, favorites

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("ContactsPrefs", Context.MODE_PRIVATE)

        initViews()
        setupRecyclerView()
        loadContacts()
        setupListeners()
    }

    private fun initViews() {
        etContactName = findViewById(R.id.etContactName)
        etContactPhone = findViewById(R.id.etContactPhone)
        btnAddContact = findViewById(R.id.btnAddContact)
        btnClearFields = findViewById(R.id.btnClearFields)
        rvContacts = findViewById(R.id.rvContacts)
        navContacts = findViewById(R.id.navContacts)
        navRecents = findViewById(R.id.navRecents)
        navFavorites = findViewById(R.id.navFavorites)
        btnLogout = findViewById(R.id.btnLogout)
    }

    private fun setupRecyclerView() {
        contactAdapter = ContactAdapter(
            contacts = contactsList,
            onItemClick = { contact ->
                Toast.makeText(this, "Llamando a ${contact.name}", Toast.LENGTH_SHORT).show()
            },
            onFavoriteClick = { contact ->
                toggleFavorite(contact)
            }
        )
        rvContacts.layoutManager = LinearLayoutManager(this)
        rvContacts.adapter = contactAdapter
    }

    private fun setupListeners() {
        btnAddContact.setOnClickListener {
            addContact()
        }

        btnClearFields.setOnClickListener {
            clearFields()
        }

        navContacts.setOnClickListener {
            currentFilter = "all"
            updateNavigationStyle()
            filterContacts()
        }

        navRecents.setOnClickListener {
            currentFilter = "recents"
            updateNavigationStyle()
            filterContacts()
        }

        navFavorites.setOnClickListener {
            currentFilter = "favorites"
            updateNavigationStyle()
            filterContacts()
        }

        btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun addContact() {
        val name = etContactName.text.toString().trim()
        val phone = etContactPhone.text.toString().trim()

        if (validateContact(name, phone)) {
            val newContact = Contact(
                id = UUID.randomUUID().toString(),
                name = name,
                phone = phone,
                isFavorite = false
            )

            contactsList.add(0, newContact)
            saveContacts()
            filterContacts()
            clearFields()

            Toast.makeText(this, "Contacto agregado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateContact(name: String, phone: String): Boolean {
        return when {
            name.isEmpty() -> {
                etContactName.error = "El nombre es requerido"
                false
            }
            !name.matches(Regex("^[a-zA-ZáéíóúñÑÁÉÍÓÚ\\s]+$")) -> {
                etContactName.error = "El nombre solo debe contener letras"
                false
            }
            phone.isEmpty() -> {
                etContactPhone.error = "El teléfono es requerido"
                false
            }
            !phone.matches(Regex("^\\d{10}$")) -> {
                etContactPhone.error = "El teléfono debe tener 10 dígitos"
                false
            }
            else -> true
        }
    }

    private fun clearFields() {
        etContactName.text.clear()
        etContactPhone.text.clear()
        etContactName.requestFocus()
    }

    private fun toggleFavorite(contact: Contact) {
        val index = contactsList.indexOfFirst { it.id == contact.id }
        if (index != -1) {
            contactsList[index] = contact.copy(isFavorite = !contact.isFavorite)
            saveContacts()
            filterContacts()
        }
    }

    private fun filterContacts() {
        val filteredList = when (currentFilter) {
            "favorites" -> contactsList.filter { it.isFavorite }
            "recents" -> contactsList.sortedByDescending { it.dateAdded }.take(10)
            else -> contactsList
        }
        contactAdapter.updateContacts(filteredList)
    }

    private fun updateNavigationStyle() {
        // Resetear estilos
        navContacts.setTextColor(getColor(android.R.color.darker_gray))
        navRecents.setTextColor(getColor(android.R.color.darker_gray))
        navFavorites.setTextColor(getColor(android.R.color.darker_gray))

        // Aplicar estilo según selección
        when (currentFilter) {
            "all" -> navContacts.setTextColor(getColor(android.R.color.holo_blue_dark))
            "recents" -> navRecents.setTextColor(getColor(android.R.color.holo_blue_dark))
            "favorites" -> navFavorites.setTextColor(getColor(android.R.color.holo_blue_dark))
        }
    }

    private fun saveContacts() {
        val gson = Gson()
        val json = gson.toJson(contactsList)
        sharedPreferences.edit().putString("contacts", json).apply()
    }

    private fun loadContacts() {
        val gson = Gson()
        val json = sharedPreferences.getString("contacts", "")

        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<Contact>>() {}.type
            contactsList = gson.fromJson(json, type)
        } else {
            // Contactos de ejemplo
            contactsList = mutableListOf(
                Contact(UUID.randomUUID().toString(), "Ana García", "812345678", false),
                Contact(UUID.randomUUID().toString(), "Carlos Rodríguez", "699888777", true),
                Contact(UUID.randomUUID().toString(), "Elena Martínez", "655443322", false)
            )
        }
        filterContacts()
    }

    private fun logout() {
        sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
    }
}