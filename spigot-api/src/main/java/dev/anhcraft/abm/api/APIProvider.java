package dev.anhcraft.abm.api;

public class APIProvider {
    private static APIProvider provider;
    private BattleAPI api;

    public static BattleAPI get() {
        if(provider == null){
            throw new UnsupportedOperationException("API is not ready yet!");
        }
        return provider.api;
    }

    public void set(BattleAPI api) {
        this.api = api;
    }
}
