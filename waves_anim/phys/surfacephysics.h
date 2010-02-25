#ifndef SURFACEPHYSICS_H_
#define SURFACEPHYSICS_H_
#include "../math/math3d.h"
#include "../mesh/surfacemesh.h"

//elasticity physical simulation
typedef struct {
	VertexMatrix *vm;
	Vector3 *prevbuffer;

	float d;
	float k1, k2, k3;

} PhysSurf;

//width, height measured in d's
void PhysSurf__init(PhysSurf *self, VertexMatrix *vm, float d, float t,
		float c, float mu);
void PhysSurf_eval(PhysSurf *self);
void PhysSurf__dispose(PhysSurf *self);

typedef struct {
	float *ph;
	int w, h;
} Pulse;

//deforms the surface  around x,y with the given pulse,
//prevpulse specify the shape of the pulse at t-dt, use to specify speed
//no bounds check
void PhysSurf_deform(PhysSurf *self, int x, int y, float A, const Pulse *pulse,
		const Pulse *prevpulse, int additive);

//s t  -1 .. 1
typedef float pulsefn(float s, float t);
float conepulse(float s, float t);
float rectpulse(float s, float t);
float planepulse(float s, float t);
float spherepulse(float s, float t);
float cosinepulse(float s, float t);
float gauss(float s, float t);

void Pulse__init(Pulse *self, int w, int h);
void Pulse_init_fromfn(Pulse *self, int w, int h, pulsefn func);
void Pulse__dispose(Pulse *self);
#endif /* SURFACEPHYSICS_H_ */
