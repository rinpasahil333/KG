package com.rinpa.kg;

public class Constants {
    public interface ACTION {
        public static String MAIN_ACTION ="com.rinpa.kg.action.main";
        public static String PREV_ACTION ="com.rinpa.kg.action.prev";
        public static String PLAY_ACTION ="com.rinpa.kg.action.play";
        public static String NEXT_ACTION ="com.rinpa.kg.action.next";
        public static String STARTFOREGROUND_ACTION ="com.rinpa.kg.action.startforeground";
        public static String STOPFOREGROUND_ACTION ="com.rinpa.kg.action.stopforeground";
        public static String NOTIFICATION_CHANNEL_ID = "com.rinpa.kg";
        public static String channelName = "My Background Service";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE= 101;
    }
}
