package edu.ucsd.cse110.zoodata;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

public class Trails {
    public static List<Trail> fromJson(Reader infoReader) {
        var gson = new Gson();
        var type = new TypeToken<List<Trail>>(){}.getType();
        return gson.fromJson(infoReader, type);
    }

    public static void toJson(List<Trail> infos, Writer writer) throws IOException {
        var gson = new Gson();
        var type = new TypeToken<List<Trail>>(){}.getType();
        gson.toJson(infos, type, writer);
        writer.flush();
        writer.close();
    }
}
