package com.tbank;

import java.util.ArrayList;
// import java.util.HashMap;

public final class DB <T extends IDB> {
    
    private  final ArrayList<T> data = new ArrayList<>();

    public  T findById(String _id){
        for(T object: this.data){
            // System.out.println(object);
            if (object.getId().equals(_id)){
                return object;
            }
        }
        return null;
    }

    public  void create(T object){
        data.add(object);
    }

    public void update(T object){
        int index = data.indexOf(object);
        object.setlastUpdated();
        data.set(index, object);
    }

    public  ArrayList<T> getAll(){
        return data;
    }

    public  void delete(String _id){
        T object = findById(_id);
        if(object != null){
            data.remove(object);
        }
    }

    public  void deleteAll(){
        data.clear();
    }

    public boolean isEmpty(){
        return data.isEmpty();
    }

}
