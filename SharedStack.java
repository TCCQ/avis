public class SharedStack {

  private final int MAXSIZE = 20; //no idea
  public int sizel, sizer;
  private boolean turn;
  private element topl, topr;
  public SharedStack () {
    sizel = 0;
    sizer = 0;
    topl = null;
    topr = null;
    turn = false;
  }

  public void pushs (double[] i, boolean l) {
    element a = new element();
    a.left = l;
    a.item = i;
    
    if (l) {
      a.next = topl;
      topl = a;
      sizel++;
      if (sizel > MAXSIZE) {
        int x = 0;
        element e = topl;
        while (x < MAXSIZE/2) {
          e = e.next;
          x++;
        }
        e.next = null;
        sizel = MAXSIZE/2;
      }
    } else {
      a.next = topr;
      topr = a;
      sizer++;
      if (sizer > MAXSIZE) {
        int x = 0;
        element e = topr;
        while (x < MAXSIZE/2) {
          e = e.next;
          x++;
        }
        e.next = null;
        sizer = MAXSIZE/2;
      }  
    }  
  }

  public element pops () {
    element hold;
    if (turn) {
      turn = false;
      if (sizel == 0) return null;
      hold = topl;
      topl = hold.next;
      sizel--;
      return hold;       
    } else {
      turn = true;
      if (sizer == 0) return null;
      hold = topr;
      topr = hold.next;
      sizer--;
      return hold; 
    }
    
  }

  public class element {
    boolean left;
    double[] item;
    element next; 
  }
}
