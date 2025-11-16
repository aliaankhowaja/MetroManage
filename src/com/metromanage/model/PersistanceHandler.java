package com.metromanage.model;

import java.sql.Connection;

abstract class PersistanceHandler{
    protected String connectionString;
    protected Connection dbConnection;
    

    public abstract int save(Object obj);
    public abstract void delete(Object obj);
    public abstract Object find(int id);

}