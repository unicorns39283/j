package cat.ui.alt;

import java.net.Proxy;
import java.lang.reflect.Field;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;

public final class AltLoginThread extends Thread {
    private final String password;
    private final String username;
    private String status;
    private final Minecraft mc = Minecraft.getMinecraft();

    public AltLoginThread(String username, String password) {
        super("Alt Login Thread");
        this.username = username;
        this.password = password;
        this.status = EnumChatFormatting.GRAY + "Waiting...";
    }

    public AltLoginThread(String username) {
        super("Alt Login Thread");
        this.username = username;
        this.status = EnumChatFormatting.GRAY + "Waiting...";
        this.password = "NONE";
    }

//    private Session createSession(String username, String password) {
//        YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
//        YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) service.createUserAuthentication(Agent.MINECRAFT);
//        auth.setUsername(username);
//        auth.setPassword(password);
//
//        try {
//            auth.logIn();
//            return new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");
//        } catch (AuthenticationException var6) {
//            var6.printStackTrace();
//            return null;
//        }
//    }
    
    private Session createSession(String username, String password) throws MicrosoftAuthenticationException
    {
    	MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
    	MicrosoftAuthResult result = authenticator.loginWithCredentials("sprungkhouyit@hotmail.com", "V3cneg82");
    	return new Session(result.getProfile().getName(), result.getProfile().getId(), result.getAccessToken(), "microsoft");
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void run()
    {
        this.status = EnumChatFormatting.YELLOW + "Logging in...";
        Session auth = null;
        
        try 
        {
			auth = this.createSession(this.username, this.password);
		} catch (MicrosoftAuthenticationException e1) {
			e1.printStackTrace();
		}
        if (auth == null)
        {
            this.status = EnumChatFormatting.RED + "Login failed!";
        }
        else
        {
        	this.status = EnumChatFormatting.GREEN + "Logged in. (" + auth.getUsername() + ")";
            try
            {
                Field field = null;
                try
                {
                    field = Minecraft.getMinecraft().getClass().getDeclaredField("session");
                } catch (Exception e) 
                {
                    field = Minecraft.getMinecraft().getClass().getDeclaredField("field_178752_a");
                    e.printStackTrace();
                }

                field.setAccessible(true);
                field.set(Minecraft.getMinecraft(), auth);
            } catch (NoSuchFieldException | IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void loginWithCracked(String username) {
        this.mc.session = new Session(username, "", "", "mojang");
        this.status = EnumChatFormatting.GREEN + "Logged in as " + this.username + ".";
    }
}
