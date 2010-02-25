#include "surfacemesh.h"
#include <stdlib.h>
#include <GL/gl.h>

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
			LoadVector3(vm->normals[a], 0.0f, 0.0f, 1.0f);
			a++;
		}
	}
}

void SurfaceMesh__dispose(SurfaceMesh *self) {
	free(self->pIndexes);
}

void SurfaceMesh__drawnormals(SurfaceMesh *self) {
	const VertexMatrix *vm = self->vm;
	glBegin(GL_LINES);
	glColor3f(0, 0, 0.1);
	Vector3 snorm, p2;
	for (int i = 0; i < self->nindexes; i++) {
		int idx = self->pIndexes[i];
		CopyVector3(snorm, vm->normals[idx]);
		ScaleVector3(snorm, 0.15);
		AddVectors3(p2, vm->vertices[idx], snorm);

		glVertex3fv(vm->vertices[idx]);
		glVertex3fv(p2);
	}
	glEnd();
}
