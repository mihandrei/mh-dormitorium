#include <GL/gl.h>
#include <stdlib.h>
#include "../math/math3d.h"

#include "common-geometry.h"

static GLfloat axes_v[] = {
		-1, 0.0f, 0.0f,
		1, 0.0f, 0.0f,
		0.0f, -1, 0.0f,
		0.0f, 1, 0.0f,
		0.0f, 0.0f, -1,
		0.0f, 0.0f, 1 };

static GLfloat axes_c[] = {
		0.8f, 0.5f, 0.5f,
		0.8f, 0.5f, 0.5f,
		0.5f, 0.8f,	0.5f,
		0.5f, 0.8f, 0.5f,
		0.5f, 0.5f, 0.8f,
		0.5f, 0.5f, 0.8f };

void axes() {
	glVertexPointer(3, GL_FLOAT, 0, axes_v);
	glColorPointer(3, GL_FLOAT, 0, axes_c);
	glDrawArrays(GL_LINES, 0, 6);
}
