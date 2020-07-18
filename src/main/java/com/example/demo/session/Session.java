package com.example.demo.session;

import java.util.ArrayList;
import java.util.List;

public final class Session {
    public static List<HandleSession> sessionList = new ArrayList<>();

    public static List<HandleSession> getSessionList() {
        return sessionList;
    }

    public static void setSessionList(List<HandleSession> sessionList) {
        Session.sessionList = sessionList;
    }

    public static String findById(String sessionId){
        for (HandleSession session:sessionList
             ) {
            if(session.getSessionId().equals(sessionId)){
                return session.getUserName();
            }
        }
        return null;
    }

    public static void removeBySessionId(String sessionId){
        for (HandleSession session:sessionList
        ) {
            if(session.getSessionId().equals(sessionId)){
                sessionList.remove(session);
                System.out.println("true");
                break;
            }
        }
    }
}
