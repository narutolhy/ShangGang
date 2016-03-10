package com.sg.spring.controller.util;

import com.sg.sql.model.Harbor;

import java.util.List;

/**
 * Created by qml_moon on 28/12/15.
 */
public class Coordinates {

	public static double[] meridian = new double[]{123, 123, 122,122,123,123,0,0};
	public static void ToWGS(List<Harbor> data, double l) {
		for (Harbor h : data) {
//			double x = h.getLongitude() / 1000000.0;
			double y = h.getLatitude() - 500000.0;

//			double bf = 9.04353692458*x-0.00001007623*Math.pow(x,2.0)-0.00074438304*Math.pow(x,3.0)-0.00000463064*Math.pow(x,4.0)+0.00000505846*Math.pow(x,5.0)-0.00000016754*Math.pow(x,6.0);
//			double hbf = bf * Math.PI/ 180.0;

			double sa = 6378137.0;
			double sb = 6356752.3142;
			double se2 = 0.006694379990;
			double sep2 = 0.00673949674223;
			double hbf = calculateBf(sa, 298.257223563, h.getLongitude());
			double bf = hbf * 180 / Math.PI;

			double w1 = Math.sin(hbf);
			double w2 = 1.0 - se2 * Math.pow(w1, 2);
			double w = Math.sqrt(w2);
			double mf = sa*(1.0-se2)/Math.pow(w, 3);
			double w3 = Math.cos(hbf);

			double w4 = Math.pow(sa, 2)*Math.pow(w3, 2) + Math.pow(sb, 2)*Math.pow(w1, 2);
			double nf = Math.pow(sa, 2) / Math.sqrt(w4);

			double ynf = y/nf;
			double vf = nf/mf;
			double tf = Math.tan(hbf);

			double yf2 = sep2 * Math.pow(w3,  2);
			double resY = bf - 1.0/2.0 * vf * tf * (Math.pow(ynf, 2)-1.0/12.0*(5.0+3.0*Math.pow(tf, 2)+yf2-9.0*yf2*Math.pow(tf, 2))*Math.pow(ynf, 4))*180.0/Math.PI;
			double resX =1.0/w3*ynf*(1.0-1.0/6.0*(1.0+2.0*Math.pow(tf, 2)+yf2)*Math.pow(ynf, 2)+1.0/120.0*(5.0+28.0*Math.pow(tf, 2)+24.0*Math.pow(tf, 2)+6.0*yf2+8.0*yf2*Math.pow(tf, 2))*Math.pow(ynf, 4))*180.0/Math.PI + l;
			h.setLongitude(resX);
			h.setLatitude(resY);
		}
	}

	private static double calculateBf(double m_pWGS84a, double m_pWGS84f, double x) {
		double currf = 1 / m_pWGS84f;   //扁率
		double currb = m_pWGS84a * (1 - currf);   //b,短半轴
		double e2 = (m_pWGS84a * m_pWGS84a - currb * currb) / (m_pWGS84a * m_pWGS84a);   //e的平的平方
		double ee2 = (m_pWGS84a * m_pWGS84a - currb * currb) / (currb * currb);   //e’的平方
		double e4 = e2 * e2;
		double e6 = e2 * e2 * e2;
		double e8 = Math.pow(e2, 4);
		double e10 = Math.pow(e2, 5);
		double e12 = Math.pow(e2, 6);
		double e14 = Math.pow(e2, 7);
		double e16 = Math.pow(e2, 8);
		double c0 = 1 + e2 / 4 + 7 * e4 / 64 + 15 * e6 / 256 + 579 * e8 / 16384 + 1515 * e10 / 65536 + 16837 * e12 / 1048576 + 48997 * e14 / 4194304 + 9467419 * e16 / 1073741824;
		c0 = m_pWGS84a / c0;
		double b0 = x / c0;
		double d1 = 3 * e2 / 8 + 45 * e4 / 128 + 175 * e6 / 512 + 11025 * e8 / 32768 + 43659 * e10 / 131072 + 693693 * e12 / 2097152 + 10863435 * e14 / 33554432;
		double d2 = -21 * e4 / 64 - 277 * e6 / 384 - 19413 * e8 / 16384 - 56331 * e10 / 32768 - 2436477 * e12 / 1048576 - 196473 * e14 / 65536;
		double d3 = 151 * e6 / 384 + 5707 * e8 / 4096 + 53189 * e10 / 163840 + 4599609 * e12 / 655360 + 15842375 * e14 / 1048576;
		double d4 = -1097 * e8 / 2048 - 1687 * e10 / 640 - 3650333 * e12 / 327680 - 114459079 * e14 / 27525120;
		double d5 = 8011 * e10 / 1024 + 874457 * e12 / 98304 + 216344925 * e14 / 3670016;
		double d6 = -682193 * e12 / 245760 - 46492223 * e14 / 1146880;
		double d7 = 36941521 * e14 / 3440640;
		double bf = b0 + Math.sin(2 * b0) * (d1 + Math.sin(b0) * Math.sin(b0) * (d2 + Math.sin(b0) * Math.sin(b0) * (d3 + Math.sin(b0) * Math.sin(b0) * (d4 + Math.sin(b0) * Math.sin(b0) * (d5 + Math.sin(b0) * Math.sin(b0) * (d6 + d7 * Math.sin(b0) * Math.sin(b0)))))));
		return bf;
	}
}
