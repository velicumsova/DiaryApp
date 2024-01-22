package com.diaryapp.EventHandler;

import com.diaryapp.EventHandler.DB.DbHandler;

import java.util.List;

/**
 * Класс для управления определенной группой.
 */
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

    /**
     * Метод для добавления группы в базу данных.
     * @param db объект DbHandler.
     */
    public void save(DbHandler db) {
        db.addGroup(this.name);
    }

    /**
     * Метод для обновления имени группы.
     * @param db объект DbHandler.
     * @param newName новое имя группы.
     */
    public void update(DbHandler db, String newName) {
        db.updateGroup(this.name, newName);
    }

    /**
     * Метод для удаления группы из базы данных.
     * Удаляются также и все связанные с группой события!.
     * @param db объект DbHandler.
     */
    public void delete(DbHandler db) {
        db.deleteGroup(this.name);
    }

    /**
     * Метод для добавления события в группу.
     * @param db объект DbHandler.
     * @param event событие, которое нужно добавить.
     */
    public void addEvent (DbHandler db, Event event) {
        event.setGroup(this.name);
        event.add(db);
    }

    /**
     * Метод для получения всех событий этой группы.
     * @param db объект DbHandler.
     * @return список объектов класса Event.
     */
    public List<Event> getAllEvents (DbHandler db) {
        return db.getEventsByGroupName(this.name);
    }

}
