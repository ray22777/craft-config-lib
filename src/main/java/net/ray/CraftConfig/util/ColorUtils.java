package net.ray.CraftConfig.util;

import net.minecraft.util.Mth;

public class ColorUtils {
	public static float[] rgbToHsv(int r, int g, int b) {
		float rf = r / 255f, gf = g / 255f, bf = b / 255f;
		float max = Math.max(rf, Math.max(gf, bf)), min = Math.min(rf, Math.min(gf, bf));
		float delta = max - min;
		float h = delta == 0 ? 0 :
				max == rf ? (60 * ((gf - bf) / delta) + 360) % 360 :
						max == gf ? (60 * ((bf - rf) / delta) + 120) % 360 :
								(60 * ((rf - gf) / delta) + 240) % 360;
		float s = max == 0 ? 0 : delta / max;
		return new float[]{h, s, max};
	}

	public static int hsvToRgb(float h, float s, float v) {
		h = h % 360; if (h < 0) h += 360;
		float c = v * s, x = c * (1 - Math.abs((h / 60) % 2 - 1)), m = v - c;
		float[] rgb = h < 60 ? new float[]{c, x, 0} :
				h < 120 ? new float[]{x, c, 0} :
						h < 180 ? new float[]{0, c, x} :
								h < 240 ? new float[]{0, x, c} :
										h < 300 ? new float[]{x, 0, c} : new float[]{c, 0, x};
		int ri = (int) ((rgb[0] + m) * 255), gi = (int) ((rgb[1] + m) * 255), bi = (int) ((rgb[2] + m) * 255);
		return (Mth.clamp(ri, 0, 255) << 16) | (Mth.clamp(gi, 0, 255) << 8) | Mth.clamp(bi, 0, 255);
	}

}
