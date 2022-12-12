package cat.ui.alt;

import java.awt.Color;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.lwjgl.input.Keyboard;

import cat.util.ColorUtil;
import cat.util.font.sigma.FontUtil;
import cat.util.font.sigma.TFontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public final class GuiAltLogin extends GuiScreen {

    private final String key = "1234"; 
    private final String generateURL = "http://localhost:3000/gen?key=" + key;
    private String username, password;
    private final GuiScreen previousScreen;
    private AltLoginThread thread;
    private GuiTextField combined;

    public GuiAltLogin(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    public static String generateAlt() throws MalformedURLException, IOException
	{
		URL url = new URL("http://localhost:3000/gen?key=1234");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();
		// Get page source code
//		String pageSource = new String(connection.getInputStream().readAllBytes());
		String pageSource = new String(connection.getInputStream().readAllBytes());
		if (!pageSource.contains("@"))
		{
			//System.out.println("No email address found");
			return null;
		}
		else
		{
			//System.out.println("Email address found");
            pageSource = pageSource.replace("\r", "").replace("\n", "");
			return pageSource;
		}
	}

    protected void actionPerformed(GuiButton button) {
        switch(button.id) {
            case 0:
				String alt = null;
				try {
					alt = generateAlt();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
                if (alt != null)
                {
                    String[] split = alt.split("\\|");
//                	System.out.println(split[0]);
//                    System.out.println(split[1]);
                    this.username = split[0];
                    this.password = split[1];
                    //this.thread = new AltLoginThread(this.username, this.password);
                    this.thread = new AltLoginThread(split[0], split[1]);
                    this.thread.start();
                }
                break;
            case 1:
                this.mc.displayGuiScreen(previousScreen);
        }

    }

    public void drawScreen(int x, int y, float z) {
        TFontRenderer font = FontUtil.fontSFLight42;
        String directLoginText = "Direct Login";
        String statusText = this.thread == null ? "Waiting..." : this.thread.getStatus();
        drawGradientRect(0, 0, this.width, this.height, new Color(0, 0, 69).getRGB(), ColorUtil.getEpicColor(10).getRGB());
        drawGradientRect(0, 0, this.width, this.height, new Color(0, 0, 69).getRGB(), ColorUtil.getEpicColor(10).getRGB());
        //this.combined.drawTextBox();
        font.drawString(directLoginText, this.width / 2f - font.getStringWidth(directLoginText)/2f, 15, -1);
        font.drawString(statusText, this.width / 2f - font.getStringWidth(statusText) / 2f, 30, -1);
       // this.drawCenteredString(this.mc.fontRendererObj, "Direct login for now", this.width / 2, 20, -1);
        //this.drawCenteredString(this.mc.fontRendererObj, this.thread == null ? EnumChatFormatting.WHITE + "Idling" : this.thread.getStatus(), this.width / 2 - 5, 33, -1);
        if (this.combined.getText().isEmpty()) {
            //font.drawString("Email:password / username", this.width / 2f - 96, 54.5f,-5592406);
            //this.drawString(this.mc.fontRendererObj, "Email : Password", this.width / 2 - 96, 56, -5592406);
        }
        font.drawString("Current name: " + mc.session.getUsername(), 10, 10, Color.GRAY.getRGB());
        //mc.fontRendererObj.drawString("Current Name: "+mc.session.getUsername(), 10, 10, Color.WHITE.getRGB());
        super.drawScreen(x, y, z);
    }

    public void initGui() {
        int var3 = this.height / 4 + 24;
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, var3 + 20, "Login"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, var3 + 20 + 24, "Back"));
        this.combined = new GuiTextField(var3, this.mc.fontRendererObj, this.width / 2 - 100, 50, 200, 20);
        this.combined.setMaxStringLength(200);
        Keyboard.enableRepeatEvents(true);
    }

    protected void keyTyped(char character, int key) {
        try {
            super.keyTyped(character, key);
        } catch (IOException var4) {
            var4.printStackTrace();
        }

        if (character == '\r') {
            this.actionPerformed((GuiButton)this.buttonList.get(0));
        }

        this.combined.textboxKeyTyped(character, key);
    }

    protected void mouseClicked(int x, int y, int button) {
        try {
            super.mouseClicked(x, y, button);
        } catch (IOException var5) {
            var5.printStackTrace();
        }
        this.combined.mouseClicked(x, y, button);
    }

    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    public void updateScreen() {
        this.combined.updateCursorCounter();
    }
}