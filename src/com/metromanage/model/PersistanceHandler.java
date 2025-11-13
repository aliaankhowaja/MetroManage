package com.metromanage.model;

abstract class PersistanceHandler{
    private String connectionString;
    
    

    public abstract void save(Object obj);
    public abstract void delete(Object obj);
    public abstract Object find(String id);

}