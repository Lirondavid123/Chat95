package com.example.chat95.chatactivity;

public class LocalDataBase {
    static AppDatabase instance = AppDatabase.getAppDatabase(getActivity().getApplicationContext());
    private static ConversationDAO myDAO;

    public final ConversationDAO myDAO = instance.ConversationDAO();

 public static void InsertConversationData(ConversationEntity con){
     myDAO.insert(con);
 }

}
