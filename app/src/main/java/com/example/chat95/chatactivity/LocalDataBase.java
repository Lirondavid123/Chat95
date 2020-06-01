package com.example.chat95.chatactivity;

import java.util.List;

public class LocalDataBase {
    public static AppDatabase instance;
    private static ConversationDAO myDAO;

    public static void InsertConversationData(ConversationEntity con) {
        myDAO.insert(con);
    }

    public static ConversationEntity retrieveConversationData(String conversationId) {
       return myDAO.loadConversationData(conversationId);
    }

    // TODO: 01/06/2020  
    public static void updateConversationData(ConversationEntity conversationEntity) {
        ConversationEntity oldConversationData=myDAO.loadConversationData(conversationEntity.getConversationId());

    }

    public static AppDatabase getInstance() {
        return instance;
    }

    public static void setInstance(AppDatabase instance) {
        LocalDataBase.instance = instance;
    }

    public static ConversationDAO getMyDAO() {
        return myDAO;
    }

    public static void setMyDAO(ConversationDAO myDAO) {
        LocalDataBase.myDAO = myDAO;
    }
}
