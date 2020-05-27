package com.example.chat95.chatactivity;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface ConversationDAO {

    @Query("SELECT * FROM ConversationEntity")
    List<ConversationEntity> getAll();

    @Query("DELETE FROM ConversationEntity")
    void deleteAll();

    @Query("SELECT COUNT(uid) FROM ConversationEntity")
    int rowcount();

    @Query("SELECT * FROM ConversationEntity FE WHERE FE.uid = :userId")
    ConversationEntity loadFriendById(int userId);

    @Insert
    void insert(ConversationEntity user);

    @Delete
    void delete(ConversationEntity user);
}

