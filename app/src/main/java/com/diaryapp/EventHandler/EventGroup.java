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
        db.addGroup(this.name);
    }

    public void update(DbHandler db, String newName) {
        db.updateGroup(this.name, newName);
    }

    public void delete(DbHandler db) {
        db.deleteGroup(this.name);
    }

    public void addEvent (DbHandler db, Event event) {
        event.setGroup(this.name);
        event.update(db);
    }

    public List<Event> getAllEvents (DbHandler db) {
        return db.getEventsByGroupName(this.name);
    }

}
