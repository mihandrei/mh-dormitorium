#include <GL/gl.h>
#include <stdlib.h>
#include "math3d.h"

#include "../common-geometry.h"

// Array containing the six vertices of the cube
static const GLfloat cube_corners[] = { -1.0f, 1.0f, 1.0f, // 0 // Front of cube
		1.0f, 1.0f, 1.0f, // 1
		1.0f, -1.0f, 1.0f,// 2
		-1.0f, -1.0f, 1.0f,// 3
		-1.0f, 1.0f, -1.0f,// 4 // Back of cube
		1.0f, 1.0f, -1.0f,// 5
		1.0f, -1.0f, -1.0f,// 6
		-1.0f, -1.0f, -1.0f };// 7

static const GLubyte cube_indexes[] = { 0, 1, 2, 3, // Front Face
		4, 5, 1, 0, // Top Face
		3, 2, 6, 7, // Bottom Face
		5, 4, 7, 6, // Back Face
		1, 5, 6, 2, // Right Face
		4, 0, 3, 7 };

void cube() {
	glDisable(GL_COLOR_ARRAY);
	glVertexPointer(3, GL_FLOAT, 0, cube_corners);
	glDrawElements(GL_QUADS, 24, GL_UNSIGNED_BYTE, cube_indexes);
	glEnable(GL_COLOR_ARRAY);
}
void wirecube() {
	glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
	cube();
	glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
}

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

////buggy
//void pyramid(short faces) {
//	Vector3 *vs = malloc(sizeof(Vector3)*(faces+1));
//	GLubyte *idx = malloc(sizeof(GLubyte)*(faces+2));
//	idx[0] = 0;
//	vs[0][0] = 0;
//	vs[0][1] = 0;
//	vs[0][2] = 1;
//
//	float angle;
//	for (short i = 0; i < faces; i++) {
//		angle = 2 * PI * i / (float) (faces-1) ;
//		vs[i+1][0] = cos(angle);
//		vs[i+1][1] = sin(angle);
//		vs[i+1][2] = 0;
//		idx[i+1] = i + 1;
//	}
//	idx[faces+1] = 0;
//
//	glDisable(GL_COLOR_ARRAY);
//	glVertexPointer(3, GL_FLOAT, 0, vs);
//	glDrawElements(GL_TRIANGLE_FAN, 24, GL_UNSIGNED_BYTE, idx);
//	vs[0][0] = 0;
//	vs[0][1] = 0;
//	vs[0][2] = 0;
//	glDrawElements(GL_TRIANGLE_FAN, 24, GL_UNSIGNED_BYTE, idx);
//	glEnable(GL_COLOR_ARRAY);
//	free(vs);
//	free(idx);
//}
//
//  glPushMatrix();
//      glColor3f(0.5f,0.2f,0.2f);
//      glTranslatef(1,0,0);
//      glRotatef(90,0,1,0);
//      glutSolidCone(0.03,0.1,8,2);
//  glPopMatrix();
//
//  glPushMatrix();
//      glColor3f(0.2f,0.5f,0.2f);
//      glTranslatef(0,1,0);
//      glRotatef(-90,1,0,0);
//      glutSolidCone(0.03,0.1,8,2);
//  glPopMatrix();
//
//  glPushMatrix();
//      glColor3f(0.2f,0.2f,0.5f);
//      glTranslatef(0,0,1);
//      glutSolidCone(0.03,0.1,8,2);
//  glPopMatrix();
//
//}
