//Vehicle class
//Specific autonomous agents will inherit from this class 
//Abstract since there is no need for an actual Vehicle object
//Implements the stuff that each auto agent needs: movement, steering force calculations, and display

abstract class Vehicle {

  //--------------------------------
  //Class fields
  //--------------------------------
  //vectors for moving a vehicle
  PVector position;
  PVector velocity;
  PVector acceleration;

  //no longer need direction vector - will utilize forward and right
  //these orientation vectors provide a local point of view for the vehicle
  PVector forward;
  PVector right;

  //floats to describe vehicle movement and size
  float mass = 1;
  float radius;
  float maxSpeed;
  float maxForce;

  //--------------------------------
  //Constructor
  //Vehicle(x position, y position, radius, max speed, max force)
  //--------------------------------
  Vehicle(float x, float y, float r, float ms, float mf) {
    //Assign parameters to class fields
    position = new PVector(x, y);
    velocity = new PVector(0, 0);
    acceleration = new PVector(0, 0);
    radius = r;
    maxSpeed = ms;
    maxForce = mf;
    
    forward = new PVector(0, 0);
    right = new PVector(0, 0);
  }

  //--------------------------------
  //Abstract methods
  //--------------------------------
  //every sub-class Vehicle must use these functions
  abstract void calcSteeringForces();
  abstract void display();

  //--------------------------------
  //Class methods
  //--------------------------------
  
  //Method: update()
  //Purpose: Calculates the overall steering force within calcSteeringForces()
  //         Applies movement "formula" to move the position of this vehicle
  //         Zeroes-out acceleration 
  void update() {
    //calculate steering forces by calling calcSteeringForces()
    calcSteeringForces();
    //add acceleration to velocity, limit the velocity, and add velocity to position
    velocity.add(acceleration);
    velocity.limit(maxSpeed);
    position.add(velocity);
    
    //calculate forward and right vectors
    forward = velocity.copy();
    right = new PVector(forward.y, -forward.x);
    
    //reset acceleration
    acceleration.mult(0);
  }

  //Method: applyForce(force vector)
  //Purpose: Divides the incoming force by the mass of this vehicle
  //         Adds the force to the acceleration vector
  void applyForce(PVector force) {
    acceleration.add(PVector.div(force, mass));
  }
  
  
  //--------------------------------
  //Steering Methods
  //--------------------------------
  
  //Method: seek(target's position vector)
  //Purpose: Calculates the steering force toward a target's position
  PVector seek(PVector target){
    //write the code to seek a target!
    PVector desired = PVector.sub(target,position);
    desired.normalize();
    desired.mult(maxSpeed);
    
    PVector steer = PVector.sub(desired,velocity);
    //steer.limit(maxForce);
    return steer;
  }
  
  PVector avoidObstacle(Obstacle ob, float safeDistance){
    PVector steer = new PVector(0, 0);
      
    // VecToCenter vecroe
    //PVector vecToCenter = PVector.sub(ob.position, position);
    PVector vecToCenter = new PVector(ob.position.x-position.x, ob.position.y-position.y);
      
    // distance to the obstacle
    float distance = PVector.dist(position, ob.position);
      
    // Return a zero vector if the obstacle is too far to concern
    // Use safe distance to determine how large the "safe zone" is
    if (distance > safeDistance){
      return steer;
    }
      
    // Return a zero vector if the obstacle is behind us
    // If both the dot product of vecToCenter and forward are negative
    float dotProduct = vecToCenter.dot(forward);
    if (dotProduct < 0)
      return steer;
        
    // Use the dot product of the vector-to-obstacle center and the unit vector
    // to the right of the vehicle (right vector) to find the distance between the centers
    // of the vehicle and the obstacle
    // Compare this to the sum of the radii and return a zero vector if we can pass safely
    dotProduct = vecToCenter.dot(right);
    if (dotProduct > (radius + ob.radius))
      return steer;
      
    // If we get this far we are on a collision course and must steer away!
    // Use the sign of the dot product between the vector to center (vecToCenter) and the
    // vector to the right (right) to determine whether to steer left or right   
    // For each case calculate desired velocity using the right vector and maxSpeed
    PVector desiredVel = new PVector(0, 0);
    // if on the right of the vehicle, turn left
    if (dotProduct > 0)
      desiredVel = right.mult(-maxSpeed);
    // turn right
    else
      desiredVel = right.mult(maxSpeed);
      
    // Compute the force required to change current velocity to desired velocity
    steer = PVector.sub(desiredVel, velocity);
      
    // Consider multiplying this force by safeDistance/dist to increase the relative weight
    // of the steering force when obstacles are closer
    steer.mult(safeDistance / distance);
      return steer;
  }    
}