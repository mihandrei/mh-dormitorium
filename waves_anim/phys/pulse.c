#include "surfacephysics.h"
#include <stdlib.h>

void Pulse__init(Pulse *self, int w, int h) {
	self->w = w;
	self->h = h;
	self->ph = malloc(sizeof(float) * w * h);
}

void Pulse__dispose(Pulse *self) {
	free(self->ph);
}

void Pulse_init_fromfn(Pulse *self, int w, int h, pulsefn func,void *data) {
	Pulse__init(self, w, h);

	for (int hi = 0; hi < self->h; hi++) {
		float h = 2 * hi / (float) self->h - 1;

		for (int wi = 0; wi < self->w; wi++) {
			float t = 2 * wi / (float) self->w - 1;

			self->ph[hi * self->w + wi] = func(h, t,data);

		}
	}
}


float Pulse_sphere(float s, float t,void *data) {
	float r2 = s * s + t * t;
	if (r2 < 1)
		return sqrt(1 - r2);
	else
		return 0;
}

float Pulse_gauss(float s, float t,float *cp) {
	float c;
	if(cp!=0)
		c = *cp;
	else
		c = 0.2f;

	float r2 = s * s + t * t;
	float a = 1/(c * sqrt(2*PI));
	float gauss =  a* exp(-r2 / (2*c*c) );
	return gauss;
}

float Pulse_rect(float s, float t,void *data) {
	return 1;
}

float Pulse_modes(float s, float t,int mn_array[2]) {
	int n = mn_array[0];
	int m = mn_array[1];

	float sc, tc;
	if (n % 2 == 0) {
		sc = sin(n * s * PI / 2);
	} else {
		sc = cos(n * s * PI / 2);
	}
	if (m % 2 == 0) {
		tc = sin(m * t * PI / 2);
	} else {
		tc = cos(m * t * PI / 2);
	}
	return sc * tc;
}



