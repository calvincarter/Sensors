package com.example.sensor;

public class CF {
	
	// Used for EulerAccel
	float phi_a;
	float theta_a;
	
	// Used for CompFilterWithPI
	float psi;
	float phi;
	float theta;
	float prevPhi; 
	float prevTheta; 
	float prevPsi;
	
	// Used for BodyToInertial function
	float dotPhi;
	float dotTheta;
	float dotPsi;
	
	// Used for PILawPhi
	float p_hat;
	float prevP;
	float prevdelPhi;
	
	// Used for PILawTheta
	float prevQ;
	float prevdelTheta;
	float q_hat;
	
	CF(){ }  // default constructor
	
	public void CompFilterWithPI(float p, float q, float r, float x, float y, float dt) { 
		
		if (this.p_hat == 0) {
			this.p_hat = 0;
			this.q_hat = 0;
			
			this.prevPhi = 0;
			this.prevTheta = 0;
			this.prevPsi = 0;
		}
		
		EulerAccel(x, y);
		BodyToInertial(p, q, r, this.prevPhi, this.prevTheta);
		
		this.phi 	= this.prevPhi   + dt * (this.dotPhi - this.p_hat);
		this.theta 	= this.prevTheta + dt * (this.dotTheta - this.q_hat);
		this.psi 	= this.prevPsi   + dt * this.dotPsi;
		
		PILawPhi(this.phi - this.phi_a);
		PILawTheta(this.theta - this.theta_a);
		
		this.prevPhi = this.phi;
		this.prevTheta = this.theta;
		this.prevPsi = this.psi;
	}

	public void BodyToInertial(float p, float q, float r, float phi, float theta) {
		
		float sinPhi 	= (float) Math.sin(phi);
		float cosPhi 	= (float) Math.cos(phi);
		
		float cosTheta	= (float) Math.cos(theta);
		float tanTheta 	= (float) Math.tan(theta);
		
		this.dotPhi	= p + q * sinPhi * tanTheta + r * cosPhi * tanTheta;
		
		this.dotTheta	= q * cosPhi - r * sinPhi;
		
		this.dotPsi	= q * sinPhi / cosTheta + r * cosPhi / cosTheta;
 	}
	
	// Reference page 127 , Chapter 13
	public void EulerAccel(float ax, float ay) {
		
		float g = (float) 9.8;
		
		this.phi_a 	= (float) Math.asin(ax / g);

		// make ay --> -ay
		ay *= -1;
		
		this.theta_a 	= (float) Math.asin(ay / (g * Math.cos(this.theta_a)));
	}
	
	public void PILawPhi(float delPhi) {
		
		if(this.prevP == 0) {
			this.prevP = 0;
			this.prevdelPhi = 0;	
		}
		
		this.p_hat = (float) (this.prevP + 0.1415 * delPhi - 0.1414 * this.prevdelPhi);
		
		this.prevP = this.p_hat;
		this.prevdelPhi = delPhi;
	}
	
	public void PILawTheta(float delTheta) {
		
		if (this.prevQ == 0) {
			this.prevQ = 0;
			this.prevdelTheta = 0;
		}
		
		this.q_hat = (float) (this.prevQ + 0.1415 * delTheta - 0.1414 * this.prevdelTheta);
		
		this.prevQ = this.q_hat;
		this.prevdelTheta = delTheta;
	}
	
}
