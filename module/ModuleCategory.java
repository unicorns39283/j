package cat.module;

public enum ModuleCategory {
    COMBAT("Combat"),
    LEGIT("Legit"),
    MOVEMENT("Movement"),
    MISC("Misc"),
    PLAYER("Player"),
    RENDER("Render");
   //WORLD("World");
    public String displayName;
    public boolean showContent;
    ModuleCategory(String displayName) {
        this.displayName = displayName;
        this.showContent = false;
    }
}
