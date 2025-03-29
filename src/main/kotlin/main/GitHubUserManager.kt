package main

import api.RetrofitClient
import model.GitHubUser
import model.Repository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import storage.FileStorage

object GitHubUserManager {
    private var users: MutableList<GitHubUser> = FileStorage.loadUsers().toMutableList()

    fun getUser(username: String, onComplete: () -> Unit) {
        val existingUser = users.find { it.login == username }
        if (existingUser != null) {
            println("User found in cache: $existingUser")
            onComplete()
            return
        }

        println("Fetching user from API...")

        RetrofitClient.instance.getUser(username).enqueue(object : Callback<GitHubUser> {
            override fun onResponse(call: Call<GitHubUser>, response: Response<GitHubUser>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        fetchRepositories(user, onComplete)
                    }
                } else {
                    println("Error fetching user: ${response.message()} (Code: ${response.code()})")
                    onComplete()
                }
            }

            override fun onFailure(call: Call<GitHubUser>, t: Throwable) {
                println("API request failed: ${t.message}")
                onComplete()
            }
        })
    }

    private fun fetchRepositories(user: GitHubUser, onComplete: () -> Unit) {
        RetrofitClient.instance.getRepositories(user.login).enqueue(object : Callback<List<Repository>> {
            override fun onResponse(call: Call<List<Repository>>, response: Response<List<Repository>>) {
                if (response.isSuccessful) {
                    user.repositories = response.body() ?: emptyList()
                    users.add(user)
                    FileStorage.saveUsers(users)
                    println("Fetched from API: $user")
                } else {
                    println("Error fetching repositories: ${response.message()} (Code: ${response.code()})")
                }
                onComplete()
            }

            override fun onFailure(call: Call<List<Repository>>, t: Throwable) {
                println("Failed to fetch repositories: ${t.message}")
                onComplete()
            }
        })
    }


    fun listUsers() {
        if (users.isEmpty()) {
            println("No users found in storage.")
        } else {
            users.forEach { println("$it\n") }
        }
    }

    fun searchUserByUsername(username: String) {
        users.find { it.login.equals(username, true) }?.let {
            println("User found: $it")
        } ?: println("User not found.")
    }

    fun searchRepositoryByName(repoName: String) {
        val foundRepos = users.flatMap { it.repositories }.filter { it.name.equals(repoName, true) }
        if (foundRepos.isNotEmpty()) {
            foundRepos.forEach { repo ->
                val owner = users.find { user -> user.repositories.contains(repo) }?.login ?: "Unknown"

                println("Repository Name: ${repo.name}")
                println("Owner: $owner")
                println("Description: ${repo.description ?: ""}")
            }
        } else {
            println("No repositories found with that name.")
        }
    }
}
