package com.diaryapp.EventHandler;

import java.util.List;

public class EventGroup {
    private String name;

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public EventGroup(){
    }

    public EventGroup(String name){
        this.setName(name);
    }

    public void save(DbHandler db) {
        if (db != null) {
            db.addGroup(this.name);
        }
    }

    public void update(DbHandler db, String newName) {
        if (db != null) {
            db.updateGroup(this.name, newName);
        }
    }

    public void delete(DbHandler db) {
        if (db != null) {
            db.deleteGroup(this.name);
        }
    }

    public List<Event> getAllEvents (DbHandler db) {
        return db.getEventsByGroupName(this.name);
    }

}
