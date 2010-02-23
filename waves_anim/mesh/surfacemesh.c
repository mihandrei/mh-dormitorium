#include "surfacemesh.h"
#include <stdlib.h>

void VertexMatrix__init(VertexMatrix *self, int w, int h) {
	self->w = w;
	self->h = h;
	int count = w * h;
	self->vertices = (Vector3*) malloc(sizeof(Vector3) * count);
	self->normals = (Vector3*) malloc(sizeof(Vector3) * count);

}

void VertexMatrix__dispose(VertexMatrix *self) {
	free(self->vertices);
	free(self->normals);
}

void SurfaceMesh_initVertices(VertexMatrix* vm, float d) {
	//initialise vertices
	int a = 0;
	for (int hi = 0; hi < vm->h; hi++) {
		float x = d * hi;
		for (int wi = 0; wi < vm->w; wi++) {
			LoadVector3(vm->vertices[a], x, d * wi, 0.0f);
			LoadVector3(vm->normals[a], 0.0f, 0.0f, 2.0f * d);
			a++;
		}
	}
}

void SurfaceMesh__dispose(SurfaceMesh *self) {
	free(self->pIndexes);
}
