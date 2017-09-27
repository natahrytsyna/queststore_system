package pl.coderampart.model;

public class Group{

    protected String ID;
    protected String name;

    public Group(){
        this.ID = null;
        this.name = null;
    }

    public Group(String name){
        this.ID = null;
        this.name = name;
    }

    public String toString(){
        String groupData = "\nID: " + this.getID()
                         + "\nname: " + this.getName();

        return groupData;
    }

    public String getID(){ return this.ID; }
    public String getName(){ return this.name; }
}