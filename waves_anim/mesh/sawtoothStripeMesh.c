#include "surfacemesh.h"
#include <stdlib.h>
#include <GL/gl.h>

void SawtoothStripeMesh__init(SurfaceMesh *self, VertexMatrix* vm, float d) {
	self->d = d;
	self->vm = vm;

	int count = vm->w * vm->h;
	self->nindexes = 2 * count - 2 * vm->w;
	self->pIndexes = (unsigned int *) malloc(sizeof(unsigned int)
			* self->nindexes);

	//compute indices
	int ci = 0;
	int a = 0;

	for (int hi = 0; hi < vm->h - 1; hi++) {
		for (int wi = 0; wi < vm->w; wi++) {
			self->pIndexes[ci] = a;
			self->pIndexes[ci + 1] = a + vm->w;
			ci += 2;
			a += 1;
		}
	}

	SurfaceMesh_initVertices(self->vm, d);
}

//need vertex array and normal array enabled
void SawtoothStripeMesh_draw(SurfaceMesh* self) {
	const VertexMatrix *vm = self->vm;
	const int indexes_per_row = 2 * vm->w;

	glVertexPointer(3, GL_FLOAT, 0, vm->vertices);
	glNormalPointer(GL_FLOAT, 0, vm->normals);

	for (int hi = 0; hi < vm->h - 1; hi++) {
		glDrawElements(GL_TRIANGLE_STRIP, indexes_per_row, GL_UNSIGNED_INT,
				self->pIndexes + hi * indexes_per_row);
	}
}
