package com.liberado.dao;

/*
 Youtuber using java DAO, short video
 https://www.youtube.com/watch?v=mj0Xu2uXFpQ
 */
/**
 * Created by admin on 21/06/2017.
 */

import java.sql.Connection;
import java.util.List;

import com.liberado.jdbc.PostgreSQLConnection;

public abstract class DAO<T> {

    public Connection connect = PostgreSQLConnection.getInstance();

    /**
     * Find and object by its id
     * @param id
     * @return
     */
    public abstract T find(long id);

    /**
     * Add an entry to the database
     * par rapport à un objet
     * @param obj
     */
    public abstract T create(T obj);

    /**
     * Update an entry in the database
     * @param obj
     */
    public abstract T update(T obj);

    /**
     * Delete an entry in the database
     * @param obj
     */
    public abstract void delete(T obj);

    /**
     * Return a list of all items in the database
     * @return all items
     */
    public abstract List<T> findAll();
}