package edu.ucsd.cse110.zoodata;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

public class Exhibits {
    /**
     * Load ZooNode's from a JSON file (such as vertex_info.json).
     *
     * @param infoReader a reader from which to read the JSON input.
     * @return a list
     */
    public static List<Exhibit> fromJson(Reader infoReader) {
        var gson = new Gson();
        var type = new TypeToken<List<Exhibit>>() {
        }.getType();
        return gson.fromJson(infoReader, type);
    }

    public static void toJson(List<Exhibit> infos, Writer writer) throws IOException {
        var gson = new Gson();
        var type = new TypeToken<List<Exhibit>>(){}.getType();
        gson.toJson(infos, type, writer);
        writer.flush();
        writer.close();
    }
}
