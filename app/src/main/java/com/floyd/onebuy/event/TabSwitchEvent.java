package com.floyd.onebuy.event;

import java.util.Map;

/**
 * Created by floyd on 16-4-20.
 */
public class TabSwitchEvent {
    public int tabId;
    public Map<String, Object> data;

    public TabSwitchEvent(int k, Map<String, Object> data) {
        this.tabId = k;
        this.data = data;
    }
}
