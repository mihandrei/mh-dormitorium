#ifndef SURFACEMESH_H_
#define SURFACEMESH_H_
#include "../math/math3d.h"

//a rectangular grid of vertices and their normals
typedef struct{
	//dimensions in vertices
	int w, h;
	Vector3 *vertices;
	Vector3 *normals;
}VertexMatrix;

void VertexMatrix__init(VertexMatrix *self, int w, int h);
void VertexMatrix__dispose(VertexMatrix *self);

//a mesh rendering a vertex matrix
typedef struct {
	unsigned int *pIndexes;
	unsigned int nindexes;
	float d;
	VertexMatrix *vm;
} SurfaceMesh;

void SurfaceMesh_initVertices(VertexMatrix* vm, float d);
void SurfaceMesh__dispose(SurfaceMesh *self);

void SimetricTriangleMesh__init(SurfaceMesh *self, VertexMatrix* vm, float d);
//need vertex array and normal array enabled
void SimetricTriangleMesh_draw(SurfaceMesh* self);

void SawtoothStripeMesh__init(SurfaceMesh *self, VertexMatrix* vm, float d);
void SawtoothStripeMesh_draw(SurfaceMesh* self);

#endif /* SURFACEMESH_H_ */
