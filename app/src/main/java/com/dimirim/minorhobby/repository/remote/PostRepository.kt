package com.dimirim.minorhobby.repository.remote

import com.dimirim.minorhobby.models.Post
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object PostRepository {
    private val posts = Firebase.firestore.collection("posts")

    suspend fun addPost(post: Post): DocumentReference {
        return posts.add(post).await()
    }

    suspend fun getPostById(id: String): Post? {
        return posts.document(id).get().await().toObject(Post::class.java)
    }

    suspend fun getPostsByHobby(hobbyId: String): List<Post> {
        return posts.whereEqualTo("hobby", hobbyId)
            .get().await().toObjects(Post::class.java)
    }

    suspend fun getPostsByTags(tagIds: List<String>, containsAll: Boolean = false): List<Post> {
        return if (containsAll) {
            getAllPosts().filter { it.tags.containsAll(tagIds) }
        } else {
            posts.whereArrayContainsAny("tags", tagIds)
                .get().await().toObjects(Post::class.java)
        }
    }

    suspend fun getPostsByAuthor(userId: String): List<Post> {
        return posts.whereEqualTo("author", userId)
            .get().await().toObjects(Post::class.java)
    }

    suspend fun getAllPosts(): List<Post> {
        return posts.get().await().toObjects(Post::class.java)
    }

    suspend fun Post.addLike() {
        posts.document(this.id).update("likes", this.likes + 1).await()
        this.likes++
    }

    suspend fun Post.addView() {
        posts.document(this.id).update("views", this.likes + 1).await()
        this.views++
    }

    suspend fun Post.update(field: String, value: Any): Post {
        posts.document(this.id).update(field, value)
        return getPostById(this.id)!!
    }
}
