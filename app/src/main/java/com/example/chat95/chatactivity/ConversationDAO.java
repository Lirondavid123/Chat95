package com.example.chat95.chatactivity;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ConversationDAO {

    @Query("SELECT * FROM ConversationEntity")
    List<ConversationEntity> getAll();

    @Query("DELETE FROM ConversationEntity")
    void deleteAll();

    @Query("SELECT COUNT(conversationId) FROM ConversationEntity")
    int rowcount();

    @Query("SELECT * FROM ConversationEntity FE WHERE FE.conversationId = :conversationId")
    ConversationEntity loadConversationData(String conversationId);

    @Insert
    void insert(ConversationEntity user);

    @Delete
    void delete(ConversationEntity user);
}

