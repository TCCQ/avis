import javax.sound.sampled.*;
import java.io.*;


public class Input implements Runnable {
  public final int CHUNKSIZE = (int)Math.pow(2,10); //pow of 2
  public final int BUFFERSIZE = 4*CHUNKSIZE;
  private final double[] imag = new double[CHUNKSIZE/4];
  private final int max16 = 32768;
  
  private AudioFormat bformat = null;
  private TargetDataLine t = null;
  private byte[] buffer;
  private int lval, rval, newoffset, avail, offset;
  private final SharedStack stack;
  private final SharedStack raw;

  //hardwired. only make 1 at a time
  public Input (SharedStack pipe, SharedStack r) {
    for (int d = 0; d < imag.length; d++) {
       imag[d] = 0;
    }
    
    bformat = new AudioFormat(new AudioFormat.Encoding("PCM_SIGNED"), (float)44100.0, 16, 2, 4, (float)44100.0, true);
    try {
      DataLine.Info info = new DataLine.Info(TargetDataLine.class, bformat);
      System.out.println("Supported: " + AudioSystem.isLineSupported(info));
      t = (TargetDataLine) AudioSystem.getLine(info);
      t.open(bformat, BUFFERSIZE); 
    } catch (Exception e) {e.printStackTrace();}
    t.start();
    buffer = new byte[BUFFERSIZE]; //see line under t initialization
    stack = pipe;
    raw = r;
  }
  
  public void run() {
    while (true) { //come back, fix
      while ((avail = t.available()) < CHUNKSIZE/2){}
      
      t.read(buffer, 0, avail);
      avail = CHUNKSIZE;
      offset=0;
      double[] left = new double[avail/2];
      double[] right = new double[avail/2];
      double h;
      while (offset < avail*2) {
        h = hann(offset/4, avail/2);
        left[offset/4] = h*(bti(buffer[offset],buffer[offset+1])); 
        right[offset/4] = h*(bti(buffer[offset+2],buffer[offset+3]));
        offset+=4;
      } //<>//
      raw.pushs(left,true);
      raw.pushs(right,false);
      stack.pushs(FFTbase.fft(left, imag, true), true); //push left ear
      stack.pushs(FFTbase.fft(right, imag, true), false); //push right ear
      
      try {
          Thread.sleep(20);
      } catch (Exception e) {e.printStackTrace();}
    }
  }
  
  private static int bti (byte a, byte b) {
    int bi = Byte.toUnsignedInt(a) & 0b01111111;
    int out;
    int sign = Byte.toUnsignedInt(a) & 0b10000000; 
    out = sign << 24;
    
    if (sign != 0) {
      bi = ~bi;
      bi += 1;  
      int mask = (int)0b01111111;
      bi = bi & mask;
    }
    bi = bi << 8;
    
    out = out | bi;
    bi = Byte.toUnsignedInt(b);
    out = out | bi;
    if (sign != 0) {
      sign = sign << 24;
      out = ~out;
      //out -= 1; //unsure if nessiary?
      out = out & Integer.MAX_VALUE; 
      out = out | sign;
    }
    return out; 
  }
  
  private static double hann (int n, int N) {
    return (0.5*(1-Math.cos(2*Math.PI*((double)(n)/N))));
  }
}



 
