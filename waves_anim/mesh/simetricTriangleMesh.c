#include "surfacemesh.h"
#include <stdlib.h>
#include <stdio.h>
#include <GL/gl.h>

static void surface__computeIndices(SurfaceMesh* self) {
	const int w = self->vm->w;
	const int h = self->vm->h;

	int ci = 0;
	int a = 0;

	int roweven = 1;
	int celleven = 1;

	for (int hi = 0; hi < h-1; hi++) {
		celleven = roweven;
		for (int wi = 0; wi < w-1; wi++) {
			if (celleven) {
				self->pIndexes[ci + 0] = a;
				self->pIndexes[ci + 1] = a + w;
				self->pIndexes[ci + 2] = a + w + 1;
				self->pIndexes[ci + 3] = a + w + 1;
				self->pIndexes[ci + 4] = a + 1;
				self->pIndexes[ci + 5] = a;
			} else {
				self->pIndexes[ci + 0] = a;
				self->pIndexes[ci + 1] = a + w;
				self->pIndexes[ci + 2] = a + 1;
				self->pIndexes[ci + 3] = a + 1;
				self->pIndexes[ci + 4] = a + w;
				self->pIndexes[ci + 5] = a + w + 1;
			}

			ci += 6;
			a++;
			celleven=!celleven;
		}
		roweven=!roweven;
		a++; //last vertex on row was skipped
	}
}

void SimetricTriangleMesh__init(SurfaceMesh *self, VertexMatrix* vm, float d) {
	self->d = d;
	self->vm = vm;

	self->nindexes = 6 * (vm->w - 1) * (vm->h - 1);
	self->pIndexes = (unsigned int *) malloc(sizeof(unsigned int)
			* self->nindexes);

	surface__computeIndices(self);
	SurfaceMesh_initVertices(self->vm, d);
}

//need vertex array and normal array enabled
void SimetricTriangleMesh_draw(SurfaceMesh* self) {
	const VertexMatrix *vm = self->vm;

	glVertexPointer(3, GL_FLOAT, 0, vm->vertices);
	glNormalPointer(GL_FLOAT, 0, vm->normals);
	glDrawElements(GL_TRIANGLES, self->nindexes, GL_UNSIGNED_INT,
			self->pIndexes);
}
