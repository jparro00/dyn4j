/*
 * Copyright (c) 2010, William Bittle
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of dyn4j nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dyn4j.game2d.testbed.test;

import org.dyn4j.game2d.collision.Bounds;
import org.dyn4j.game2d.collision.RectangularBounds;
import org.dyn4j.game2d.dynamics.Fixture;
import org.dyn4j.game2d.dynamics.World;
import org.dyn4j.game2d.dynamics.joint.Joint;
import org.dyn4j.game2d.dynamics.joint.RevoluteJoint;
import org.dyn4j.game2d.geometry.Circle;
import org.dyn4j.game2d.geometry.Mass;
import org.dyn4j.game2d.geometry.Polygon;
import org.dyn4j.game2d.geometry.Rectangle;
import org.dyn4j.game2d.geometry.Vector;
import org.dyn4j.game2d.testbed.ContactCounter;
import org.dyn4j.game2d.testbed.Entity;
import org.dyn4j.game2d.testbed.Test;

/**
 * Tests the a motorized revolute joint.
 * @author William Bittle
 */
public class Motor extends Test {
	/* (non-Javadoc)
	 * @see test.Test#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Tests a motorized revolute joint.";
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.Test#initialize()
	 */
	@Override
	public void initialize() {
		// call the super method
		super.initialize();
		
		// setup the camera
		this.home();
		
		// set the bounds
		this.bounds = new Rectangle(16.0, 15.0);
		
		// create the world
		Bounds bounds = new RectangularBounds(this.bounds);
		this.world = new World(bounds);
		
		// setup the contact counter
		ContactCounter cc = new ContactCounter();
		this.world.setContactListener(cc);
		this.world.setStepListener(cc);
		
		// setup the bodies
		this.setup();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.Test#setup()
	 */
	@Override
	protected void setup() {
		// create the floor
		Rectangle floorRect = new Rectangle(15.0, 1.0);
		Entity floor = new Entity();
		floor.addFixture(new Fixture(floorRect));
		floor.setMassFromShapes(Mass.Type.INFINITE);
		// move the floor down a bit
		floor.translate(0.0, -4.0);
		this.world.add(floor);
		
		/*
		 * Make this configuration
		 * +------------------------+
		 * |                        |
		 * | .                    . |
		 * +-|--------------------|-+
		 *   0                    0
		 */
		
		// create a reusable rectangle
		Rectangle frameRect = new Rectangle(3.0, 0.175);
		Rectangle bodyRect = new Rectangle(3.2, 0.5);
		bodyRect.translate(0.0, 0.0875 + 0.25);
		Polygon cabinPoly = new Polygon(new Vector[] {
				new Vector(1.25, 0.0),
				new Vector(0.25, 0.35),
				new Vector(-0.5, 0.35),
				new Vector(-0.75, 0.0)
		});
		cabinPoly.translate(-0.25, 0.0875 + 0.5);
		
		// create a reusable circle
		Circle c = new Circle(0.35);
		
		Fixture fc1 = new Fixture(c);
		fc1.setDensity(2.0);
		
		Fixture fc2 = new Fixture(c);
		fc2.setDensity(1.0);
		fc2.setFriction(0.1);
		
		Entity body = new Entity();
		body.addFixture(new Fixture(frameRect));
		body.addFixture(new Fixture(bodyRect));
		body.addFixture(new Fixture(cabinPoly));
		body.setMassFromShapes();
		body.translate(-3.0, -3.1);
		
		Entity wheel1 = new Entity();
		wheel1.addFixture(fc1);
		wheel1.setMassFromShapes();
		wheel1.translate(-4.0, -3.1);
		
		Entity wheel2 = new Entity();
		wheel2.addFixture(fc2);
		wheel2.setMassFromShapes();
		wheel2.translate(-2.0, -3.1);
		
		this.world.add(body);
		this.world.add(wheel1);
		this.world.add(wheel2);
		
		// create a  fixed distance joint between the wheels
		Vector p1 = wheel1.getWorldCenter().copy();
		Vector p2 = wheel2.getWorldCenter().copy();
		
		// join them
		Joint j1 = new RevoluteJoint(wheel1, body, false, p1);
		this.world.add(j1);
		Joint j2 = new RevoluteJoint(wheel2, body, false, p2, true, -2.0 * Math.PI, 100.0);
		this.world.add(j2);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.Test#home()
	 */
	@Override
	public void home() {
		// set the scale
		this.scale = 64.0;
		// set the camera offset
		this.offset.set(0.0, 2.0);
	}
}