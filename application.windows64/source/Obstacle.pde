class Obstacle{
  float radius;
  PVector position;
  
  Obstacle(){
    // Creates a randomly placed obstacle with a random radius
    radius = 30;
    position = new PVector(random(30, width-30),random(30, height-30));
  }
  
  void display(){
    ellipse(position.x, position.y, radius*2, radius*2);
  }
  
}