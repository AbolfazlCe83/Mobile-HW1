package main

import java.util.Scanner
import kotlin.system.exitProcess

object Menu {
    private val scanner = Scanner(System.`in`)

    fun showMenu() {
        while (true) {
            println("""
                1️⃣ Fetch GitHub user by username
                2️⃣ Show all stored users
                3️⃣ Search user by username
                4️⃣ Search repository by name
                5️⃣ Exit
            """.trimIndent())

            when (scanner.nextLine().trim()) {
                "1" -> {
                    print("Enter GitHub username: ")
                    val username = scanner.nextLine().trim()
                    GitHubUserManager.getUser(username) { showMenu() }
                    return
                }
                "2" -> GitHubUserManager.listUsers()
                "3" -> {
                    print("Enter Username: ")
                    val username = scanner.nextLine().trim()
                    GitHubUserManager.searchUserByUsername(username)
                }
                "4" -> {
                    print("Enter repository name: ")
                    val repoName = scanner.nextLine().trim()
                    GitHubUserManager.searchRepositoryByName(repoName)
                }
                "5" -> {
                    println("Exiting program... Goodbye!")
                    exitProcess(0)
                }
                else -> println("Invalid choice, try again!")
            }
        }
    }
}
