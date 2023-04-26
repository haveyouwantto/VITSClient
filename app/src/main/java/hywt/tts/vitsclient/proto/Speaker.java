package hywt.tts.vitsclient.proto;

public class Speaker {
    public String name;
    public int id;

    public Speaker(String name, int id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String toString(){
        return String.format("[%d] %s",id,name);
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}