#include <GL/gl.h>
#include <GL/glu.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>

#include "../baserender.h"
#include "../shared/math3d.h"
#include "../shared/tga.h"
#include "common-geometry.h"

extern float xRot, zRot;
extern bool wires, renderplane;

float light0_pos[] = { 0, 0, 0.2, 1.0 };

void glerr(char* source){
	GLenum err = glGetError();
	if(err!= GL_NO_ERROR){
		printf("err in %s %d",source,err);
		exit(-2);
	}
}

static void light() {
	//light
	glColor3f(1.0, 1.0, 0.5);
	glBegin(GL_LINES);
	glVertex3f(light0_pos[0] - 0.1, light0_pos[1], light0_pos[2]);
	glVertex3f(light0_pos[0] + 0.1, light0_pos[1], light0_pos[2]);
	glVertex3f(light0_pos[0], light0_pos[1] - 0.1, light0_pos[2]);
	glVertex3f(light0_pos[0], light0_pos[1] + 0.1, light0_pos[2]);
	glVertex3f(light0_pos[0], light0_pos[1], light0_pos[2] - 0.1);
	glVertex3f(light0_pos[0], light0_pos[1], light0_pos[2] + 0.1);
	glEnd();

	glLightfv(GL_LIGHT0, GL_POSITION, light0_pos);
}

static void RenderSurroundings() {
	glColor3f(0.5, 0.5, 0.5);
	axes();

	//blended plane and wirecube

	glColor4f(0.5, 0.5, 0.5, 0.5);
	glEnable(GL_BLEND);
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	if (renderplane) {
		glBegin(GL_QUADS);
		glNormal3f(0, 0, 1);
		glVertex3f(-1, -1, 0);
		glVertex3f(-1, 1, 0);
		glVertex3f(1, 1, 0);
		glVertex3f(1, -1, 0);
		glEnd();
	}
	glColor4f(0.5, 0.5, 0.5, 0.1);
	wirecube();

	glDisable(GL_BLEND);
}

void RenderScene(void) {
	bool lighting = glIsEnabled(GL_LIGHTING);
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	glDisable(GL_LIGHTING);
	RenderStatusLine();
	//eye space light
	//	light();
	glPushMatrix();

	//push origin forward along z so that we can see it
	glTranslatef(0, 0, -3);

	glRotatef(xRot - 60, 1.0f, 0.0f, 0.0f);
	glRotatef(zRot - 135, 0.0f, 0.0f, 1.0f);

	//geometry space light; after the above rotations , not eye space
	light();

	///////

	if (wires) {
		glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
	} else {
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
	}
	if (lighting != BR_NOLIGHT) {
		glEnable(GL_LIGHTING);
	}
	drawGeometry();

	RenderSurroundings();

	glPopMatrix();

}

void ChangeSize(GLsizei w, GLsizei h) {
	GLfloat aspectRatio;
	// Prevent a divide by zero
	if (h == 0)
		h = 1;
	// Set Viewport to window dimensions
	glViewport(0, 0, w, h);
	// Reset coordinate system
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	// Establish clipping volume (left, right, bottom, top, near, far)
	aspectRatio = (GLfloat) w / (GLfloat) h;

	gluPerspective(40.0f, aspectRatio, 0.5, 10);

	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
}

void SetupRC(void) {

	glShadeModel(GL_SMOOTH);
	glEnable(GL_DEPTH_TEST);

	glEnableClientState(GL_VERTEX_ARRAY);
	glEnableClientState(GL_NORMAL_ARRAY);
	glEnableClientState(GL_COLOR_ARRAY);
	glEnableClientState(GL_TEXTURE_COORD_ARRAY);
	glerr("SetupRC clientstate");

	//light
	//glEnable(GL_LIGHTING);
	float globalambient[] = { 0, 0, 0 };
	glLightModelfv(GL_LIGHT_MODEL_AMBIENT, globalambient);

	static float moonlight = 0.5;
	float ambientLight[] = { 1 - moonlight, 1 - moonlight, 1 - moonlight, 1.0 };
	float diffuseLight[] = { moonlight, moonlight, moonlight, 1.0 };
	float specularLight[] = { 1.0, 1.0, 1.0, 1.0 };
	float specref[] = { 0.3, 0.3, 0.3, 1.0 };

	glLightfv(GL_LIGHT0, GL_AMBIENT, ambientLight);
	glLightfv(GL_LIGHT0, GL_DIFFUSE, diffuseLight);
	glLightfv(GL_LIGHT0, GL_SPECULAR, specularLight);
	glEnable(GL_LIGHT0);

	glEnable(GL_COLOR_MATERIAL);
	glColorMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE);
	glMaterialfv(GL_FRONT, GL_SPECULAR, specref);
	glMateriali(GL_FRONT, GL_SHININESS, 128);

	glClearColor(ambientLight[0], ambientLight[1], ambientLight[2],
			ambientLight[3]);
	glerr("SetupRC light");
	//fog
	//	glEnable(GL_FOG);
	glFogfv(GL_FOG_COLOR, ambientLight);
	glFogf(GL_FOG_START, 3.0f);
	glFogf(GL_FOG_END, 4.0f);

	glFogi(GL_FOG_MODE, GL_LINEAR);
	glerr("SetupRC fog");
	//texture

	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
//	glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);

	glerr("SetupRC tex");
}

void SetupLight(int lighting) {
	if (lighting >= BR_LIGHT_ALL_AND_TEX)
		glEnable(GL_TEXTURE_2D);
	else
		glDisable(GL_TEXTURE_2D);

	if (lighting >= BR_LIGHT_DIFFUSE_AND_SPECULAR_AND_FOG)
		glEnable(GL_FOG);
	else
		glDisable(GL_FOG);

	if (lighting >= BR_LIGHT_DIFFUSE_AND_SPECULAR) {
		float specref[] = { 0.3, 0.3, 0.3, 1.0 };
		glMaterialfv(GL_FRONT, GL_SPECULAR, specref);
	} else {
		float specref[] = { 0.0, 0.0, 0.0, 1.0 };
		glMaterialfv(GL_FRONT, GL_SPECULAR, specref);
	}

	if (lighting >= BR_LIGHT_DIFFUSE)
		glEnable(GL_LIGHTING);
	else
		glDisable(GL_LIGHTING);

	glerr("setuplight");
}
