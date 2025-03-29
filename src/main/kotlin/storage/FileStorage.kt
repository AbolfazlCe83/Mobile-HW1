package storage

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import model.GitHubUser
import java.io.File

object FileStorage {
    private const val FILE_NAME = "users.json"

    fun loadUsers(): List<GitHubUser> {
        val file = File(FILE_NAME)
        if (!file.exists() || file.readText().isBlank()) {
            return emptyList()
        }

        return try {
            val json = file.readText()
            Gson().fromJson(json, object : TypeToken<List<GitHubUser>>() {}.type) ?: emptyList()
        } catch (e: Exception) {
            println("Error loading users: ${e.message}")
            emptyList()
        }
    }

    fun saveUsers(users: List<GitHubUser>) {
        val json = Gson().toJson(users)
        File(FILE_NAME).writeText(json)
    }
}
