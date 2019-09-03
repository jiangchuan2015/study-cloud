package chuan.study.cloud.util;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
public final class CaptchaUtils {
    private static final String CAPTCHA_NUMBERS = "0123456789";
    private static final Random RANDOM = new Random();

    /**
     * 使用系统默认字符源生成验证码
     *
     * @param size 验证码长度
     */
    public static String createText(int size) {
        return createText(CAPTCHA_NUMBERS, size);
    }

    /**
     * 使用指定源生成验证码
     *
     * @param str  验证码字符源
     * @param size 验证码长度
     */
    public static String createText(String str, int size) {
        final String text = StringUtils.isBlank(str) ? CAPTCHA_NUMBERS : str;
        int length = text.length();
        Random random = new Random(System.currentTimeMillis());
        StringBuilder captchaBuilder = new StringBuilder(size * 2);
        IntStream.range(0, size).forEach(idx -> captchaBuilder.append(text.charAt(random.nextInt(length - 1))));
        return captchaBuilder.toString();
    }

    /**
     * 生成验证码图片
     *
     * @param text   验证码字符
     * @param width  验证码图片宽度
     * @param height 验证码图片调试
     * @return 验证码图片
     */
    public static BufferedImage createImage(String text, int width, int height) {
        int length = text.length();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Random rand = new Random();
        Graphics2D gd = image.createGraphics();
        gd.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 设置边框色
        gd.setColor(Color.GRAY);
        gd.fillRect(0, 0, width, height);
        Color c = getRandColor(200, 250);
        // 设置背景色
        gd.setColor(c);
        gd.fillRect(0, 2, width, height - 4);

        //绘制干扰线
        Random random = new Random();
        // 设置线条的颜色
        gd.setColor(getRandColor(160, 200));
        for (int i = 0; i < 20; i++) {
            int x = random.nextInt(width - 1);
            int y = random.nextInt(height - 1);
            int xl = random.nextInt(6) + 1;
            int yl = random.nextInt(12) + 1;
            gd.drawLine(x, y, x + xl + 40, y + yl + 20);
        }

        // 添加噪点
        float yawpRate = 0.05f;
        int area = (int) (yawpRate * width * height);
        for (int i = 0; i < area; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int rgb = getRandomIntColor();
            image.setRGB(x, y, rgb);
        }

        // 使图片扭曲
        shear(gd, width, height, c);

        gd.setColor(getRandColor(100, 160));
        int fontSize = height - 4;
        gd.setFont(new Font("Algerian", Font.ITALIC, fontSize));
        char[] chars = text.toCharArray();
        for (int i = 0; i < length; i++) {
            AffineTransform affine = new AffineTransform();
            affine.setToRotation(Math.PI / 4 * rand.nextDouble() * (rand.nextBoolean() ? 1 : -1), (width / length) * i + fontSize / 2, height / 2);
            gd.setTransform(affine);
            gd.drawChars(chars, i, 1, ((width - 10) / length) * i + 5, height / 2 + fontSize / 2 - 5);
        }
        gd.dispose();
        /*ImageIO.write(image, "jpg", outputStream);*/
        return image;
    }

    private static Color getRandColor(int fc, int bc) {
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + RANDOM.nextInt(bc - fc);
        int g = fc + RANDOM.nextInt(bc - fc);
        int b = fc + RANDOM.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    private static int getRandomIntColor() {
        int[] rgb = getRandomRgb();
        int color = 0;
        for (int c : rgb) {
            color = color << 8;
            color = color | c;
        }
        return color;
    }

    private static int[] getRandomRgb() {
        int[] rgb = new int[3];
        for (int i = 0; i < 3; i++) {
            rgb[i] = RANDOM.nextInt(255);
        }
        return rgb;
    }

    private static void shear(Graphics g, int w1, int h1, Color color) {
        shearX(g, w1, h1, color);
        shearY(g, w1, h1, color);
    }

    private static void shearX(Graphics graphic, int width, int height, Color color) {
        int period = RANDOM.nextInt(2);
        int phase = RANDOM.nextInt(2);
        int frames = 1;
        for (int i = 0; i < height; i++) {
            double d = (double) (period >> 1)
                    * Math.sin((double) i / (double) period
                    + (6.2831853071795862D * (double) phase)
                    / (double) frames);
            graphic.copyArea(0, i, width, 1, (int) d, 0);
            graphic.setColor(color);
            graphic.drawLine((int) d, i, 0, i);
            graphic.drawLine((int) d + width, i, width, i);
        }
    }

    private static void shearY(Graphics graphic, int width, int height, Color color) {
        int period = RANDOM.nextInt(40) + 10;
        int frames = 20;
        int phase = 7;
        for (int i = 0; i < width; i++) {
            double d = (double) (period >> 1)
                    * Math.sin((double) i / (double) period
                    + (6.2831853071795862D * (double) phase)
                    / (double) frames);
            graphic.copyArea(i, 0, 1, height, 0, (int) d);
            graphic.setColor(color);
            graphic.drawLine(i, (int) d, i, 0);
            graphic.drawLine(i, (int) d + height, i, height);
        }
    }
}
