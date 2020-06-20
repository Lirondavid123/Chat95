package com.example.chat95.chatactivity;

import android.util.Log;

import com.example.chat95.data.PublicKey;

public class LocalDataBase {
    private static final String TAG = "LocalDataBase";
    public static AppDatabase instance;
    private static ConversationDAO myDAO;

    public static void InsertConversationData(ConversationEntity con) {
        myDAO.insert(con);
    }

    public static ConversationEntity retrieveConversationData(String conversationId) {
       return myDAO.loadConversationData(conversationId);
    }

    // TODO: 01/06/2020  
    public static void updateFinalConversationData(String conversationId, PublicKey foreignKey, String symmetricKey, boolean isApproved) {
        ConversationEntity oldConversationData=myDAO.loadConversationData(conversationId);
        myDAO.delete(oldConversationData);
        Log.d(TAG, "updateFinalConversationData: oldConversationData: "+oldConversationData);
        myDAO.insert(new ConversationEntity(conversationId
                ,oldConversationData.getMyE()
                ,oldConversationData.getMyN()
                ,oldConversationData.getD()
                ,oldConversationData.getP()
                ,oldConversationData.getQ()
                ,symmetricKey
                ,foreignKey.getE()
                ,foreignKey.getN(), isApproved));
    }
    public static void deleteConversationDetails(String conversationId){
        ConversationEntity conversationToDelete=myDAO.loadConversationData(conversationId);
        myDAO.delete(conversationToDelete);
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