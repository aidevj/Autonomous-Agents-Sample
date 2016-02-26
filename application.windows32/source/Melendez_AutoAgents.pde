// Isis Melendez
// IGME 202-04

//Beginning to code autonomous agents
//Will implement inheritance with a Vehicle class and a Seeker class


Seeker s;
ArrayList<Obstacle> obs;
int obcount = 5;

void setup() {
  size(500, 500); 
  // Initialize and populate array list of obstacles
  obs = new ArrayList<Obstacle>();
  for (int i = 0; i < obcount; i++){
    obs.add(new Obstacle());
  }
  
  s = new Seeker(width/2, height/2, 6, 2, 0.1, obs);
}

void draw() {
  background(255);
  
   // Display obstacles
  for (int i = 0; i < obcount; i++){
    obs.get(i).display();
  }
  
  // Draw an ellipse at the mouse location
  ellipse(mouseX, mouseY, 20, 20);
  
  //update the seeker - done for you
  s.update();
  s.display();
  
}