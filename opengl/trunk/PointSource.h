#ifndef POINTSOURCE_H_
#define POINTSOURCE_H_

struct spatial_fragment{
	float r,A;
	spatial_fragment(float r, float A){
		this->r = r;
		this->A = A;
	}
};

/*
 *a point wave centered in the origin
 *uniform wave speed assumed => y(r,t) = A(r)sin(wt-kx) ; k = w/c = 2pi/lambda
 *wave hill equiv spring => pot energy ~ y^2
 *wave front a circle  => to maintain const. pot energy per wave front
 *E per front = integr dE ; dE = A^2 * k * (lambda * dl) ; [k] = N / m^2
 *A(r) = A0 * sqrt(lambda / r)
 */
class PointSource {
     float x0, y0 , A0 , phase0;
     float omega, lambda, k;

     float damping_coeff;

public:
	PointSource(float x0 , float y0, float A0 = 0.5,
			float freq = 2, float speed =1, float phase0 = 0,
			float damping_coeff = 0.1);

	//the wave at a point in space - time
	float z(float x , float y , float t);

	//optimized z takes a precomputed space fragment
	float z(spatial_fragment frag , float t);

	//computes values for this point in space. The values can be cached and not recomputed by z(x,y,t)
	spatial_fragment spatial(float x, float y);
};


#endif /* POINTSOURCE_H_ */
