
package io.bega.valuebar.colors;

import android.graphics.Color;

public class WhiteToBlueFormatter implements BarColorFormatter {

    @Override
    public int getColor(float value, float maxVal, float minVal) {
        
        float hsv[] = new float[] { 1f, ((120f * ((maxVal-minVal) - (value-minVal))) / (maxVal-minVal)), 1f };

        return Color.HSVToColor(hsv);
        
//        return Color.rgb((int) ((255 * value) / maxVal), (int) ((255 * (maxVal - value)) / maxVal),
//                0);
    }
}
