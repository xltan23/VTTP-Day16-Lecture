package sg.edu.nus.iss.D16.models;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class Weather {
    private String main;
    private String description;
    private String icon;

    public String getMain() {
        return main;
    }
    public void setMain(String main) {
        this.main = main;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }

    // Creating weather object from JsonObject
    public static Weather create(JsonObject jo) {
        Weather w = new Weather();
        w.setMain(jo.getString("main"));
        w.setDescription(jo.getString("description"));
        w.setIcon(jo.getString("icon"));
        return w;
    }

    // Building the JsonObject
    public JsonObject toJson() {
        return Json.createObjectBuilder()
                .add("main",main)
                .add("description", description)
                .add("icon", icon)
                .build();
    }

    
}
