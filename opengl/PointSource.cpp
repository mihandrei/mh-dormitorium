#include "PointSource.h"
#include <cmath>
#include "common.h"

PointSource::PointSource(float x0, float y0, float A0,
					     float freq, float speed, float phase0,
					     float damping_coeff) {
   this->x0=x0;
   this->y0=y0;
   this->A0=A0;
   this->phase0 = phase0;
   this->damping_coeff = damping_coeff;

   this->omega = 2 * PI * freq;
   this->k = omega/speed;
   this->lambda = speed/freq;
}

spatial_fragment PointSource::spatial(float x, float y){
	x = x + x0;
	y = y + y0;

	float r = sqrt(x*x + y*y);
	float damping = exp( - omega * damping_coeff * r);
	float A = A0 * damping * sqrt( lambda / (r + lambda));

	return spatial_fragment(r,A);
}

float PointSource::z(spatial_fragment frag , float t){
	return frag.A * sin(omega * t - k * frag.r + phase0);
}

float PointSource::z(float x,float y,float t){
	return z( spatial(x,y) , t);
}
