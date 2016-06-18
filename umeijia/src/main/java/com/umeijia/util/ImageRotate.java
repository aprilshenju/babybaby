package com.umeijia.util;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageRotate {

//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//			String img_path = "E:\\2015021819434939.jpg";
//			try {
//				BufferedImage src = ImageIO.read(new File(img_path));
//				BufferedImage dest = Rotate(src, 90);
//				ImageIO.write(dest, "jpg",new File("E:\\window1.jpg"));
//				System.out.println("width:"+src.getWidth()+"  height:"+src.getHeight());
//				System.out.println("width:"+dest.getWidth()+"  height:"+dest.getHeight());
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//	}

	public static BufferedImage Rotate(Image src, int angel) {
		int src_width = src.getWidth(null);
		int src_height = src.getHeight(null);
		// calculate the new image size
		Rectangle rect_des = CalcRotatedSize(new Rectangle(new Dimension(
				src_width,src_height)), angel);

		BufferedImage res = null;
		res = new BufferedImage(rect_des.width, rect_des.height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = res.createGraphics();
		// transform
		g2.translate((rect_des.width - src_width) / 2,
				(rect_des.height - src_height) / 2);
		g2.rotate(Math.toRadians(angel), src_width / 2, src_height / 2);

		g2.drawImage(src, null, null);
		
		return res;
	}

	public static Rectangle CalcRotatedSize(Rectangle src, int angel) {
		// if angel is greater than 90 degree, we need to do some conversion
		if (angel >= 90) {
			if(angel / 90 % 2 == 1){
				int temp = src.height;
				src.height = src.width;
				src.width = temp;
			}
			angel = angel % 90;
		}

		double r = Math.sqrt(src.height * src.height + src.width * src.width) / 2;
		double len = 2 * Math.sin(Math.toRadians(angel) / 2) * r;
		double angel_alpha = (Math.PI - Math.toRadians(angel)) / 2;
		double angel_dalta_width = Math.atan((double) src.height / src.width);
		double angel_dalta_height = Math.atan((double) src.width / src.height);

		int len_dalta_width = (int) (len * Math.cos(Math.PI - angel_alpha
				- angel_dalta_width));
		int len_dalta_height = (int) (len * Math.cos(Math.PI - angel_alpha
				- angel_dalta_height));
		int des_width = src.width + len_dalta_width * 2;
		int des_height = src.height + len_dalta_height * 2;
		return new java.awt.Rectangle(new Dimension(des_width, des_height));
	}
	
//	
//	public static BufferedImage rotateImage(final BufferedImage bufferedimage,
//            final int degree) {
//        int w = bufferedimage.getWidth();
//        int h = bufferedimage.getHeight();
//        int type = bufferedimage.getColorModel().getTransparency();
//        BufferedImage img;
//        Graphics2D graphics2d;
//        (graphics2d = (img = new BufferedImage(w, h, type))
//                .createGraphics()).setRenderingHint(
//                RenderingHints.KEY_INTERPOLATION,
//                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//        graphics2d.rotate(Math.toRadians(degree), w / 2, h / 2);
//        graphics2d.drawImage(bufferedimage, 0, 0, null);
//        graphics2d.dispose();
//        return img;
//    }

	
}
