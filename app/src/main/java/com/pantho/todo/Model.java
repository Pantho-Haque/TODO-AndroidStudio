package com.pantho.todo;

public class Model {
    String task, des, id;

    private Model(){}

    public Model(String task, String des, String id ) {
        this.task = task;
        this.des = des;
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
