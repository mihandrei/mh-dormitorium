#ifndef SURFACEPHYSICS_H_
#define SURFACEPHYSICS_H_
#include "../math/math3d.h"
#include "../mesh/surfacemesh.h"

//elasticity physical simulation
typedef struct {
	VertexMatrix *vm;
	Vector3 *prevbuffer;

	float k1, k2, k3;
}PhysSurf;

//width, height measured in d's
void PhysSurf__init(PhysSurf *self, VertexMatrix *vm, float d, float t,
		float c, float mu);
void PhysSurf_eval(PhysSurf *self);
void PhysSurf__dispose(PhysSurf *self);

//adds or replaces a region around x,y with the given pulse, no bounds check
void PhysSurf_deform(PhysSurf *self, int x, int y, int pulse_w, int pulse_h,
		float A, const float pulse[], int additive);
#endif /* SURFACEPHYSICS_H_ */
