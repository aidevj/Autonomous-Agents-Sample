import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Melendez_AutoAgents extends PApplet {

// Isis Melendez
// IGME 202-04

//Beginning to code autonomous agents
//Will implement inheritance with a Vehicle class and a Seeker class


Seeker s;
ArrayList<Obstacle> obs;
int obcount = 5;

public void setup() {
   
  // Initialize and populate array list of obstacles
  obs = new ArrayList<Obstacle>();
  for (int i = 0; i < obcount; i++){
    obs.add(new Obstacle());
  }
  
  s = new Seeker(width/2, height/2, 6, 2, 0.1f, obs);
}

public void draw() {
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
class Obstacle{
  float radius;
  PVector position;
  
  Obstacle(){
    // Creates a randomly placed obstacle with a random radius
    radius = 30;
    position = new PVector(random(30, width-30),random(30, height-30));
  }
  
  public void display(){
    ellipse(position.x, position.y, radius*2, radius*2);
  }
  
}
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
  public void calcSteeringForces() {
      
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
  public void display() {
      
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
  public abstract void calcSteeringForces();
  public abstract void display();

  //--------------------------------
  //Class methods
  //--------------------------------
  
  //Method: update()
  //Purpose: Calculates the overall steering force within calcSteeringForces()
  //         Applies movement "formula" to move the position of this vehicle
  //         Zeroes-out acceleration 
  public void update() {
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
  public void applyForce(PVector force) {
    acceleration.add(PVector.div(force, mass));
  }
  
  
  //--------------------------------
  //Steering Methods
  //--------------------------------
  
  //Method: seek(target's position vector)
  //Purpose: Calculates the steering force toward a target's position
  public PVector seek(PVector target){
    //write the code to seek a target!
    PVector desired = PVector.sub(target,position);
    desired.normalize();
    desired.mult(maxSpeed);
    
    PVector steer = PVector.sub(desired,velocity);
    //steer.limit(maxForce);
    return steer;
  }
  
  public PVector avoidObstacle(Obstacle ob, float safeDistance){
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
  public void settings() {  size(500, 500); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Melendez_AutoAgents" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
