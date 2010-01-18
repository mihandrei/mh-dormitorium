#ifndef _MESH
#define _MESH

#include "math3d.h"

#define COLOR_INTERVAL 0.5
//#define PARAMETRIC_COLOR

typedef struct {
	unsigned short *pIndexes;
	Vector3 *pVerts;
	Vector3 *pNorms;
	Vector3 *pColor;
	Vector2 *pTexCoords;
	unsigned short nindexes;
	unsigned short nverts;
	unsigned int nMaxIndexes;// immutable
} mesh;

typedef struct { /*immutable*/
	mesh *mesh;
	short samples;
	float interval_width;
} parametricmesh;

typedef void parametricSurface(float s, float t, void* context, Vector3 result,
		int sidx, int tidx);

mesh* mesh__init__(int nMaxIndexes);
void mesh__dispose__(mesh *m);

parametricmesh* parametricmesh__init__(short samples, float interval_width);
void parametricmesh__dispose__(parametricmesh *m);

void computeParametricMesh(parametricmesh *pm, parametricSurface func, void* fncontext);

void drawParametricMesh(parametricmesh* pmesh);

#endif
