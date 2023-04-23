package hywt.tts.vitsclient;

public class Speaker {
    public String name;
    public int id;

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