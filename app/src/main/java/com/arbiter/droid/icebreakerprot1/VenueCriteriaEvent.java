package com.arbiter.droid.icebreakerprot1;

import java.util.HashMap;

public class VenueCriteriaEvent {
    public final String searchQuery;
    public final HashMap<String,Boolean> criterias;
    public VenueCriteriaEvent(String searchQuery,HashMap<String,Boolean> criterias)
    {
        this.searchQuery = searchQuery;
        this.criterias = criterias;
    }
}
