#include "surfacephysics.h"
#include <stdlib.h>
#include <string.h>
/**
 * suprasciem valorile noi in bufferu vechi pt ca formula pt i,j,k+1
 * depinde de valoarea pasului anterior numai in punctul  i,j
 * Doar valorile vecinilor din pasul curent conteaza.
 */
/**
 * verifica ca bufferele sa fie consistente:
 * 0 1 2 3 ->y ; 3=w
 * 4 5 6 7
 * x
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
	memcpy(self->prevbuffer,vm->vertices,sizeof(Vector3) * count);

	float c1 = c * c * t * t / (d * d);
	float c2 = 1.0f / (mu * t + 2);
	self->k1 = (4.0f - 8.0f * c1) * c2;
	self->k2 = (mu * t - 2) * c2;
	self->k3 = 2.0f * c1 * c2;

}

void PhysSurf__dispose(PhysSurf *self) {
	free(self->prevbuffer);
}

void PhysSurf_eval(PhysSurf *self) {
	//apply 13.25 from maths for 3d prog ...
	VertexMatrix *const vm = self->vm;
	const int w = vm->w;
	const int h = vm->h;

	for (int hi = 1; hi < h - 1; hi++) {
		//current rows in both buffers
		Vector3 *crnt = &vm->vertices[hi * w];
		Vector3 *prev = &self->prevbuffer[hi * w];

		for (int wi = 1; wi < w - 1; wi++) {
			float nextz = self->k1 * crnt[wi][2] + self->k2 * prev[wi][2];
			nextz += self->k3 * (crnt[wi + 1][2] + crnt[wi - 1][2] + crnt[wi
					+ w][2] + crnt[wi - w][2]);

			prev[wi][2] = nextz;
		}
	}

	//swap buffers
	Vector3 * tmpprv = self->prevbuffer;
	self->prevbuffer = vm->vertices;
	vm->vertices = tmpprv;

	//compute normals

	for (long hi = 1; hi < h - 1; hi++) {
		Vector3 *crnt = &vm->vertices[hi * w];

		for (long wi = 1; wi < w - 1; wi++) {
			LoadVector3(vm->normals[hi * w + wi], crnt[wi - 1][2] - crnt[wi
					+ 1][2], crnt[wi - w][2] - crnt[wi + w][2], 1);
			NormalizeVector(vm->normals[hi * w + wi]);
		}
	}
}

void PhysSurf_deform(PhysSurf *self, int x, int y, int pulse_w, int pulse_h,
		float A, const float pulse[], int additive) {
	x -= pulse_w / 2;
	y -= pulse_h / 2;

	int w = self->vm->w;

	Vector3 *buffs[] = { self->vm->vertices, self->prevbuffer };

	for (int b = 0; b < 2; b++) {
		Vector3* buff = buffs[b];
		for (int hi = 0; hi < pulse_h; hi++) {
			for (int wi = 0; wi < pulse_w; wi++) {
				int a = (x + hi) * w + y + wi;
				float ampl = A * pulse[hi * pulse_w + wi];
				if (additive) {
					buff[a][2] += ampl;
				} else {
					buff[a][2] = ampl;
				}
			}
		}
	}
}
