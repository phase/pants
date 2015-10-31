package pants.client;

import org.bukkit.plugin.java.*;

public class ExampleImplementation extends JavaPlugin{
    
    @Override
    public void onLoad() {
        PantsClient.onLoad();
    }
}
