package com.mp1;

public class Global {
	public static final long TIMEOUT = 2000;
	public static final int SERVER_LOSS_PROBABILITY = 50;
	public static final int CLIENT_LOSS_PROBABILITY = 50;
	public static final int BUFFERLIMIT = 8;
	public static final long CLIENT_DELAY = 500;
	public static final long SERVER_DELAY = 250;
	
	
	public static byte[] concat(byte[] a, byte[] b) {
		   int aLen = a.length;
		   int bLen = b.length;
		   byte[] c= new byte[aLen+bLen];
		   System.arraycopy(a, 0, c, 0, aLen);
		   System.arraycopy(b, 0, c, aLen, bLen);
		   return c;
	}
	
}

            e.printStackTrace();
        }
        return imageString;
    }
}
