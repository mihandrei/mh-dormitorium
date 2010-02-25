#include "surfacephysics.h"
#include <stdlib.h>
#include <string.h>
/**
 * suprasciem valorile noi in bufferu vechi pt ca formula pt i,j,k+1
 * depinde de valoarea pasului anterior numai in punctul  i,j
 * Doar valorile vecinilor din pasul curent conteaza.
 */
/**
 * TODO: verifica ca bufferele sa fie consistente:
 * 0 1 2 3 ->y ; 3=w    y +-1 => buff +-1  ;x+=1 =>buff +-w
 * 4 5 6 7
 * x
 *
 * TODO fix normals. more than numerical stability.
 * after 2 iterations normals are too vertical eveen if surface is an aabrupt gaussian
 * */

int PhysSurf_checkstability(float d, float t, float c, float mu) {
	if (c <= 0 || t <= 0)
		return 0;
	if (c >= d / (2 * t) * sqrt(mu * t + 2))
		return 0;

	float f1 = c * c / d * d;
	if (t >= (mu + sqrt(mu * mu + 32 * f1)) / (8 * f1))
		return 0;
	return 1;
}

//width, height measured in d's
void PhysSurf__init(PhysSurf *self, VertexMatrix *vm, float d, float t,
		float c, float mu) {
	self->vm = vm;
	int count = vm->w * vm->h;
	self->prevbuffer = (Vector3*) malloc(sizeof(Vector3) * count);
	memcpy(self->prevbuffer, vm->vertices, sizeof(Vector3) * count);

	float c1 = c * c * t * t / (d * d);
	float c2 = 1.0f / (mu * t + 2);
	self->k1 = (4.0f - 8.0f * c1) * c2;
	self->k2 = (mu * t - 2) * c2;
	self->k3 = 2.0f * c1 * c2;
	self->d = d;

}

void PhysSurf__dispose(PhysSurf *self) {
	free(self->prevbuffer);
}

void PhysSurf_eval(PhysSurf *self) {
	//apply 13.25 from maths for 3d prog ...
	VertexMatrix * const vm = self->vm;
	const int w = vm->w;
	const int h = vm->h;


	for (int hi = 1; hi < h - 1; hi++) {
		//current rows in both buffers
		Vector3 *crnt = &vm->vertices[hi * w];
		Vector3 *prev = &self->prevbuffer[hi * w];

		for (int wi = 1; wi < w - 1; wi++) {
			float nextz = self->k1 * crnt[wi][2] + self->k2 * prev[wi][2];
			nextz += self->k3 * (crnt[wi + w][2] + crnt[wi - w][2] + crnt[wi
					+ 1][2] + crnt[wi - 1][2]);

			prev[wi][2] = nextz;
		}
	}

	//swap buffers
	Vector3 * tmpprv = self->prevbuffer;
	self->prevbuffer = vm->vertices;
	vm->vertices = tmpprv;

	//compute normals

	for (int hi = 1; hi < h - 1; hi++) {
		Vector3 *crnt = &vm->vertices[hi * w];
		Vector3 *crnt_norms = &vm->normals[hi * w];

		//TODO address numerical instability: these differences become 0 =>vertically biased normals
		for (int wi = 1; wi < w - 1; wi++) {
			LoadVector3(crnt_norms[wi], crnt[wi - w][2] - crnt[wi + w][2],
					crnt[wi - 1][2] - crnt[wi + 1][2] , 2*self->d);
			NormalizeVector(crnt_norms[wi]);
		}
	}
}

static void PhysSurf_puls2buff(PhysSurf *self, int bufferidx, int x, int y,
		float A, const Pulse *pulse, int additive) {
	x -= pulse->w / 2;
	y -= pulse->h / 2;

	int w = self->vm->w;

	Vector3 *buffs[] = { self->vm->vertices, self->prevbuffer };
	Vector3 *buff = buffs[bufferidx];

	for (int hi = 0; hi < pulse->h; hi++) {
		for (int wi = 0; wi < pulse->w; wi++) {
			int a = (x + hi) * w + y + wi;
			float ampl = A * pulse->ph[hi * pulse->w + wi];
			if (additive) {
				buff[a][2] += ampl;
			} else {
				buff[a][2] = ampl;
			}
		}
	}
}

void PhysSurf_deform(PhysSurf *self, int x, int y, float A, const Pulse *pulse,
		const Pulse *prevpulse, int additive) {
	PhysSurf_puls2buff(self, 0, x, y, A, pulse, additive);
	PhysSurf_puls2buff(self, 1, x, y, A, prevpulse, additive);
}

float conepulse(float s, float t) {
	float r = sqrt(s * s + t * t);
	if (r < 1)
		return 1 - r;
	else
		return 0;
}

float spherepulse(float s, float t) {
	float r2 = s * s + t * t;
	if (r2 < 1)
		return sqrt(1 - r2);
	else
		return 0;
}

float cosinepulse(float s, float t) {
	float r = sqrt(s * s + t * t);
	if (r < 1)
		return cos(r * PI / 2);
	else
		return 0;
}

float gauss(float s, float t) {
	const float c = 0.2f;

	float r2 = s * s + t * t;
	float a = 1/(c * sqrt(2*PI));
	float gauss =  a* exp(-r2 / (2*c*c) );
	return gauss;
}

float rectpulse(float s, float t) {
	return 1;
}
float planepulse(float s, float t) {
	return (s + 1) / 2.0;
}

void Pulse__init(Pulse *self, int w, int h) {
	self->w = w;
	self->h = h;
	self->ph = malloc(sizeof(float) * w * h);
}

void Pulse__dispose(Pulse *self) {
	free(self->ph);
}

void Pulse_init_fromfn(Pulse *self, int w, int h, pulsefn func) {
	Pulse__init(self, w, h);

	for (int hi = 0; hi < self->h; hi++) {
		float h = 2 * hi / (float) self->h - 1;

		for (int wi = 0; wi < self->w; wi++) {
			float t = 2 * wi / (float) self->w - 1;

			self->ph[hi * self->w + wi] = func(h, t);

		}
	}
}


