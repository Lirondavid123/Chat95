package com.example.chat95.utils;

public interface ConstantValues {
    //database root childs
    String EVENTS = "Events";
    String USERS = "users";
    String EVENTS_WITH_ATTENDINGS = "EventsWithAttending";
    String USERS_ATTENDING_TO_EVENTS = "UsersAttendingToEvents";
    String BENEFITS = "Benefits";
    String USERS_LIST = "UsersList";
    String USER_PHOTOS = "UserPhotos";
    String POSTS = "Posts";
    String CHAT_CONVERSATIONS = "ChatConversations";
    String CHAT_MESSAGES = "ChatMessages";

    //categories
    String CATEGORY = "eventCategory";
    String GENERAL = "General";
    String HELP_ME = "Help Me!";


    //events
    String EVENT_ID = "eventId";
    String EVENT_TITLE = "title";
    String EVENT_HOST_UID = "eventCreatorUid";
    String BEGIN_DATE = "beginDate";
    String BEGIN_TIME = "beginTime";
    String FINISH_DATE = "finishDate";
    String FINISH_TIME = "finishTime";
    String IS_CHECKED_IN = "isCheckedIn";

    //users
    String PROFILE_PHOTO = "profileImage";
    String USER_ID = "userId";
    String USER_FIRST_NAME = "userFirstName";
    String USER_LAST_NAME = "userLastName";


    //user levels
    String USER_LEVEL_1 = "Shy Socializer";
    String USER_LEVEL_2 = "Out Of The Shell Socializer";
    String USER_LEVEL_3 = "Academic Socializer";
    String USER_LEVEL_4 = "Socialized Ninja Turtle";
    String USER_LEVEL_5 = "Socialosaurus";

    long USER_LEVEL_2_POINTS = 200 ;
    long USER_LEVEL_3_POINTS =  800;
    long USER_LEVEL_4_POINTS =  2600;
    long USER_LEVEL_5_POINTS =  6000;

    //times in milliseconds
    int SECOND = 1000;
    int MINUTE = 60 * 1000;
    int HOUR = 3600 * 1000;

    String IS_HELP_EVENT = "isHelpEvent";
    float GEOFENCE_RADIUS = 70;
    String IS_COMPANY_EVENT = "companyManagmentEvent";
    String LOGGED_USER_ID = "loggedUserId";

    //chats
    String CHAT_ID = "chatId";
    String LIKES = "Likes";
    String IS_FINISHED = "finished";
    String APPROVED = "approved";

    //notifications
    String NOTIFICATIONS = "Notifications";

    //tpyes of notifications
    String EVENT = "Event";
    String NEW_CONVERSTION = "New Conversation";
    String NEW_RANK = "New rank";
    String RELATED_NAME = "relatedName";
    // likes also a type

    // notification bodies
    String EVENT_NOTIFICATION_BODY1 = "You have arrived to: \"";
    String EVENT_NOTIFICATION_BODY2 = "\".\n Have a good time!.\nReceived points: ";
    String NEW_CONVERSATION_NOTIFICATION_BODY1 = "Congratulation on connecting with ";
    String NEW_CONVERSATION_NOTIFICATION_BODY2 = "\nReceived points: ";
    String LIKE_NOTIFICATION_BODY = "People have liked your post";
    String NEW_RANK_BODY = "Congratulations on reaching the new level:\n";

}
