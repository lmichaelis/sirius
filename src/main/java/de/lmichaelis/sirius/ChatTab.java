package de.lmichaelis.sirius;

import de.lmichaelis.sirius.filter.Filter;

import java.util.ArrayList;

public class ChatTab {
    private String name;
    private int tabColor;
    private int unreadNotificationCount;

    private Filter acceptFilter;

    private ArrayList<Filter> filters;


    public void processMessage(Message message) {

    }
}
