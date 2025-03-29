package api

import model.GitHubUser
import model.Repository
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubService {
    @GET("users/{username}")
    fun getUser(@Path("username") username: String): Call<GitHubUser>

    @GET("users/{username}/repos")
    fun getRepositories(@Path("username") username: String): Call<List<Repository>>
}
