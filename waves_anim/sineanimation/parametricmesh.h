#ifndef _PARAMETRICMESH
#define _PARAMETRICMESH

#include "../mesh/appendableMesh.h"

#define COLOR_INTERVAL 0.5

typedef struct { /*immutable*/
	AppendableMesh *mesh;
	short samples;
	float interval_width;
	int colortype;

} parametricmesh;

typedef void parametricSurface(float s, float t, void* context, Vector3 result,
		int sidx, int tidx);

parametricmesh* parametricmesh__init__(short samples, float interval_width,
		bool usenormals,bool usecolor,bool gentexcoords,int colortype);
void parametricmesh__dispose__(parametricmesh *m);

void computeParametricMesh(parametricmesh *pm, parametricSurface func, void* fncontext);

void drawParametricMesh(parametricmesh* pmesh);

#endif
