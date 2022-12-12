package cat.util;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {
    private static final Pattern COLOR_PATT = Pattern.compile("(?i)§[0-9A-FK-OR]");
    
    public static Color getColorAlpha(Color color, int alpha) 
    {
    	return getColorAlpha(color.getRGB(), alpha);
    }
    public static Color getColorAlpha(int color, int alpha) 
    {
    	Color color2 = new Color((new Color(color)).getRed(), (new Color(color)).getGreen(), (new Color(color)).getBlue(), alpha);
        return color2;
    }
    
    public static Color rainbow(float g, float m){
        double delay = (Math.abs(System.currentTimeMillis() / 20L) / 100.0 + 6.0F * ((g * m) + 2.55) / 60) % 1;
        final double n3 = 1.0 - delay;
        return Color.getHSBColor((float) n3, 0.6f, 1);
    }
    public static Color getMainColor(){
        return new Color(199,21,133);
    }
    public static Color getBackgroundColor(){
        return new Color(153,50,204);
    }
    public static Color getEpicColor(int secs){
        Color color = getBackgroundColor();
        Color color2 = getMainColor();
        double delay = Math.abs(System.currentTimeMillis() / 20L) / 100.0 + 6.0F * ((secs * 2) + 2.55) / 60;
        if (delay > 1.0) {
            final double n2 = delay % 1.0;
            delay = (((int) delay % 2 == 0) ? n2 : (1.0 - n2));
        }
        final double n3 = 1.0 - delay;
        return new Color((int) (color.getRed() * n3 + color2.getRed() * delay), (int) (color.getGreen() * n3 + color2.getGreen() * delay), (int) (color.getBlue() * n3 + color2.getBlue() * delay), (int) (color.getAlpha() * n3 + color2.getAlpha() * delay));
    }
    public static String removeGay(String text){
        return text.replaceAll(COLOR_PATT.pattern(), "");
    }
    public static String getLastColor(String text){
        Matcher m = COLOR_PATT.matcher(text);
        return m.find() ? m.group(m.groupCount()) : "§r";
    }
    public static String getFirstColor(String text){
        Matcher m = COLOR_PATT.matcher(text);
        return m.find() ? m.group(1) : "§r";
    }
    
    public static Color blendColors(float[] fractions, Color[] colors, float progress) 
    {
    	if (fractions.length == colors.length) 
    	{
    		int[] indices = getFractionIndices(fractions, progress);
    		float[] range = { fractions[indices[0]], fractions[indices[1]] };
    		Color[] colorRange = { colors[indices[0]], colors[indices[1]] };
    		float max = range[1] - range[0];
    		float value = progress - range[0];
    		float weight = value / max;
    		Color color = blend(colorRange[0], colorRange[1], (1.0F - weight));
    		return color;
    	} 
    	throw new IllegalArgumentException("Fractions and colours must have equal number of elements");	
    }
    
    public static int[] getFractionIndices(float[] fractions, float progress) 
    {
    	int[] range = new int[2];
        int startPoint;
        for (startPoint = 0; startPoint < fractions.length && fractions[startPoint] <= progress; startPoint++);
        if (startPoint >= fractions.length)
        	startPoint = fractions.length - 1; 
        range[0] = startPoint - 1;
        range[1] = startPoint;
        return range;
    }
    
    public static Color blend(Color color1, Color color2, double ratio) 
    {
    	float r = (float)ratio;
        float ir = 1.0F - r;
        float[] rgb1 = new float[3];
        float[] rgb2 = new float[3];
        color1.getColorComponents(rgb1);
        color2.getColorComponents(rgb2);
        Color color3 = new Color(rgb1[0] * r + rgb2[0] * ir, rgb1[1] * r + rgb2[1] * ir, rgb1[2] * r + rgb2[2] * ir);
        return color3;
    }
    
}
