package com.example.chat95.chatactivity;

import com.example.chat95.data.PublicKey;

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
    public static void updateFinalConversationData(String conversationId,PublicKey foreignKey,String symmetricKey) {
        ConversationEntity oldConversationData=myDAO.loadConversationData(conversationId);
        myDAO.delete(oldConversationData);
        myDAO.insert(new ConversationEntity(conversationId
                ,oldConversationData.getMyE()
                ,oldConversationData.getMyN()
                ,oldConversationData.getD()
                ,oldConversationData.getP()
                ,oldConversationData.getQ()
                ,symmetricKey
                ,foreignKey.getE()
                ,foreignKey.getN()));
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
