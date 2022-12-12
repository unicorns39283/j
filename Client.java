package cat;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.lwjgl.opengl.Display;

import com.google.common.eventbus.EventBus;

import cat.client.ConfigManager;
import cat.client.HWID;
import cat.command.CommandManager;
import cat.events.EventManager;
import cat.module.ModuleManager;
import cat.module.modules.render.ClickGUI;
import cat.ui.GuiMain;
import cat.ui.clickgui.ClickGui;
import cat.ui.notifications.NotificationManager;
import cat.ui.notifications.NotificationType;
import cat.util.ClientUtils;

public class BlueZenith {
    private static final String[] devs = {"b37a1f28a445269e1b861df5ddf35c29742aa4764d32173c166ad9b2a2fc7546"};
    public static String currentServerIP;
    public static String name = "t";
    public static String version = "b1.0";
    public static boolean useExperimentalEventBus = true;
    public static EventManager eventManager;
    public static EventBus eventBus;
    public static ModuleManager moduleManager;
    public static CommandManager commandManager;
    public static GuiMain guiMain;
    public static ExecutorService executorService = Executors.newSingleThreadExecutor();
    public static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private static long startTime;
    private static boolean isDeveloper = false;

    private BlueZenith() {}

    public static void start() {
        startTime = System.currentTimeMillis();
        HWID.getHWID();
        isDeveloper = Arrays.stream(devs).anyMatch(hwid -> hwid.equalsIgnoreCase(HWID.hwid));
        ClientUtils.getLogger().info("Starting " + name + " " + version);
        if(useExperimentalEventBus)
        eventBus = new EventBus();
        eventManager = new EventManager();
        ClientUtils.getLogger().info("Started event manager.");
        moduleManager = new ModuleManager();
        ClientUtils.getLogger().info("Loaded " + moduleManager.getModules().size() + " modules.");
        commandManager = new CommandManager();
        ClientUtils.getLogger().info("Loaded " + commandManager.commands.size() + " commands.");
        hook();
        ClientUtils.getLogger().info("Added a shutdown hook.");
        ConfigManager.load("default", false, false);
        ConfigManager.loadBinds();
        ClientUtils.getLogger().info("Loaded the default config and binds.");
        guiMain = new GuiMain();
        ClickGUI.clickGui = new ClickGui();
        ConfigManager.loadClickGUIPanels();
        ClientUtils.getLogger().info("Created ClickGUI.");
        Display.setTitle(name + " | 1.8.9 | " + version + (isDeveloper ? " | Developer" : " Beta"));
        ClientUtils.getLogger().info("Started in " + (System.currentTimeMillis() - startTime) + " ms.");
        NotificationManager.addNoti("Started in " + (System.currentTimeMillis() - startTime) + " ms.", "", NotificationType.SUCCESS, 5000);
    }

    private static void hook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            moduleManager.getModule(ClickGUI.class).setState(false);
            eventManager.shutdown();
            ConfigManager.save("default");
            ConfigManager.saveBinds();
            ConfigManager.saveClickGUIPanels();
        }));
    }

    public static void register(Object listener) {
        if(useExperimentalEventBus)
            eventBus.register(listener);
        else eventManager.registerListener(listener);
    }

    public static void unregister(Object listener) {
        if(useExperimentalEventBus)
            eventBus.unregister(listener);
        else eventManager.unregisterListener(listener);
    }
}
