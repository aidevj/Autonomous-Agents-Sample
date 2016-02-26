//Seeker class
//Creates an inherited Seeker object from the Vehicle class
//Due to the abstract nature of the vehicle class, the Seeker
//  must implement the following methods:  
//  calcSteeringForces() and display()

class Seeker extends Vehicle {

  //---------------------------------------
  //Class fields
  //---------------------------------------

  //seeking target
  //set to null for now
  PVector target = null;
  
  //PShape to draw this seeker object
  PShape body;

  //overall steering force for this Seeker accumulates the steering forces
  //  of which this will be applied to the vehicle's acceleration
  PVector steeringForce;
  
  float safeDistance = 100;
  ArrayList<Obstacle> obs;


  //---------------------------------------
  //Constructor
  //Seeker(x position, y position, radius, max speed, max force)
  //---------------------------------------
  Seeker(float x, float y, float r, float ms, float mf, ArrayList<Obstacle> o) {
      
    //call the super class' constructor and pass in necessary arguments
    super(x, y, r, ms, mf);

    //instantiate steeringForce vector to (0, 0)
    target = new PVector(0, 0);
    steeringForce = new PVector(0, 0);

    //PShape initialization
    //draw the seeker "pointing" toward 0 degrees
    body = createShape();
    body.beginShape();
    body.vertex(-10, -5);
    body.vertex(-10, 5);
    body.vertex(10, 0);
    body.endShape(CLOSE);
    
    obs = o;      
  }


  //--------------------------------
  //Abstract class methods
  //--------------------------------
  
  //Method: calcSteeringForces()
  //Purpose: Based upon the specific steering behaviors this Seeker uses
  //         Calculates all of the resulting steering forces
  //         Applies each steering force to the acceleration
  //         Resets the steering force
  void calcSteeringForces() {
      
    //get the steering force returned from calling seek
    //This seeker's target (for now) is the mouse
    PVector seekingForce = seek(new PVector(mouseX, mouseY));

    //add the above seeking force to this overall steering force
    steeringForce.add(seekingForce);

    //limit this seeker's steering force to a maximum force
    steeringForce.limit(maxForce);

    //apply this steering force to the vehicle's acceleration
    super.applyForce(steeringForce);

    //reset the steering force to 0
    steeringForce.mult(0);
    
    // Call avoidObstacle for each obstacle
    for (int i = 0; i < obs.size(); i++){
      steeringForce.add(avoidObstacle(obs.get(i), safeDistance));
    }
  }
  

  //Method: display()
  //Purpose: Finds the angle this seeker should be heading toward
  //         Draws this seeker as a triangle pointing toward 0 degreed
  //         All Vehicles must implement display
  void display() {
      
    //calculate the direction of the current velocity - this is done for you
    float angle = velocity.heading();   

    //draw this vehicle's body PShape using proper translation and rotation
    pushMatrix();
      translate(position.x, position.y);
      rotate(angle);
      shape(body, 0, 0);
    popMatrix();
  }
  
  //--------------------------------
  //Class methods
  //--------------------------------
  
  
  
}