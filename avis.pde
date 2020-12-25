Thread t;
SharedStack s,r;
float  freq, mag;
final int STEP = 10;

void left(float[] arr) {
  float x = STEP;
  pushMatrix();
  translate(200,400);
  fill(0);
  rect(-10,-800,350,100);
  fill(0,0,255);
  
  beginShape();
  curveVertex(0,0);
  curveVertex(0,0);
  for (float a : arr) {
     curveVertex(x,-1*((a)+1));
     x+=STEP;
  }
  curveVertex(x,0);
  curveVertex(x,0);
  endShape();
  popMatrix();
}

void right(float[] arr) {
  float x = STEP;
  pushMatrix();
  translate(200,600);
  fill(0);
  rect(-10,-100,350,800);
  fill(255,0,0);
  
  beginShape();
  curveVertex(0,0);
  curveVertex(0,0);
  for (float a : arr) {
     curveVertex(x,((a)+1));
     x+=STEP;
  }
  curveVertex(x,0);
  curveVertex(x,0);
  endShape();
  popMatrix();
}

void setup() {
  size(1000,1000); 
  s = new SharedStack();
  r = new SharedStack();
  Input i = new Input(s,r);
  t = new Thread(i);
  t.start();
  rectMode(CORNERS);
  textSize(32);
  textAlign(LEFT,TOP);
  background(0);
}

void fr () {
  fill(0);
  rect(0,0,200,100);
  fill(0,255,0);
  text((int)frameRate,10,10);
}

void leftRaw (double[] arr) {
  pushMatrix();
  translate(0,300);
  fill(0);
  rect(0,-100,780,100);
  fill(0,255,255);
  int x = 10;
  for (int i = 0; i < arr.length; i+= 2) {
    circle(x,((float)arr[i]/32768)*100,5);
    x+=3;
  }
  popMatrix();
}

void rightRaw (double[] arr) {
  pushMatrix();
  translate(0,600);
  fill(0);
  rect(0,-100,780,100);
  fill(255,128,0);
  int x = 10;
  for (int i = 0; i < arr.length; i+= 2) {
    circle(x,((float)arr[i]/32768)*100,5);
    x+=3;
  }
  popMatrix();
}

void draw() {
  fr();
  SharedStack.element se = s.pops();
  SharedStack.element re = r.pops();
  if (se == null || re == null) {
    try {
      Thread.sleep(20); 
    } catch (Exception e) {e.printStackTrace();}
    return;
  }
  
  pushMatrix();
  //scale(0.75);
  rotate(-PI/2);
  translate(-1100,0);
  double[] d = se.item;
  if (d != null) {
    int index;
    float[] arr = new float[d.length/16]; //all 0s (hopefully)
    
    for (int i = 0; i < d.length/4; i+=2) { //d.len/2 is full spec. we want only part, (up till 10000Hz) (half is mirrored, and every 2 indixies is 1 mag., we only want 1/2 the spec.)
      mag = (float)Math.sqrt(Math.pow(d[i],2) + Math.pow(d[i+1],2));
      mag = (mag < 0)? 0:mag;//safety
      mag /= 128;
      arr[i/4] += mag; //condense again, each visual point is 2 freqs.
    }
    if (se.left) left(arr);   
    else right(arr);
  }
  popMatrix();
  
  pushMatrix();
  scale(0.75);
  translate(275,-100);
  double[] rd = re.item;
  if (rd != null) {
    if (re.left) leftRaw(rd);
    else rightRaw(rd);
  }
  popMatrix();
}
