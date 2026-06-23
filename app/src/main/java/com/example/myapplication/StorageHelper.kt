package com.example.myapplication

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


object StorageHelper {

    private const val PREF = "APP_DATA"

    /* ───────── SAVE USER ───────── */
    fun saveUser(
        context: Context,
        username: String,
        password: String,
        name: String,
        age: String,
        contacts: List<Contact>
    ) {
        val pref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val editor = pref.edit()

        val contactsArray = JSONArray()
        contacts.forEach {
            val obj = JSONObject()
            obj.put("name", it.name)
            obj.put("phone", it.phone)
            contactsArray.put(obj)
        }

        editor.putString("username", username)
        editor.putString("password", password)
        editor.putString("name", name)
        editor.putString("age", age)
        editor.putString("contacts", contactsArray.toString())

        editor.apply()
    }

    /* ───────── LOGIN VALIDATION ───────── */
    fun validateLogin(context: Context, username: String, password: String): Boolean {

        val pref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)

        val savedUser = pref.getString("username", "")
        val savedPass = pref.getString("password", "")

        val isValid = username == savedUser && password == savedPass

        if (isValid) {
            saveLoginTime(context)
        }

        return isValid
    }

    /* ───────── SAVE LOGIN TIME ───────── */
    private fun saveLoginTime(context: Context) {

        val pref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val editor = pref.edit()

        val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

        editor.putString("last_login_date", date)
        editor.putString("last_login_time", time)

        editor.apply()
    }

    /* ───────── GET USER INFO ───────── */
    fun getName(context: Context): String {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getString("name", "User") ?: "User"
    }

    fun getAge(context: Context): String {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getString("age", "0") ?: "0"
    }

    /* ───────── GET CONTACTS ───────── */
    fun getContacts(context: Context): List<Contact> {

        val pref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val data = pref.getString("contacts", "[]") ?: "[]"

        val list = mutableListOf<Contact>()

        val array = JSONArray(data)

        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(
                Contact(
                    obj.getString("name"),
                    obj.getString("phone")
                )
            )
        }

        return list
    }

    /* ───────── SAVE HISTORY ───────── */
    fun saveHistory(context: Context, helperName: String, phone: String) {

        val pref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val old = pref.getString("history", "") ?: ""

        val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

        val user = getName(context)

        val newEntry = """
👤 User: $user
🧑 Helper: $helperName
📞 Phone: $phone
📅 Date: $date
⏰ Time: $time
-------------------------
"""

        pref.edit().putString("history", old + "\n" + newEntry).apply()
    }

    /* ───────── GET HISTORY ───────── */
    fun getHistory(context: Context): String {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getString("history", "") ?: ""
    }

    /* ───────── GET LOGIN INFO ───────── */
    fun getLastLogin(context: Context): String {

        val pref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)

        val date = pref.getString("last_login_date", "")
        val time = pref.getString("last_login_time", "")

        return "Last Login: $date $time"
    }
}