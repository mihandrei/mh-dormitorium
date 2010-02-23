#include <GL/gl.h>
#include <GL/glu.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>

#include "baserender.h"
#include "../math/math3d.h"
#include "tga.h"

extern float xRot, zRot;
extern bool wires, renderplane;

float light0_pos[] = { 0, 0, 0, 1.0 };
float lightdirection[] = { 0, 0, -1, 1.0 };

void glerr(char* source) {
	GLenum err = glGetError();
	if (err != GL_NO_ERROR) {
		printf("err in %s %d", source, err);
		exit(-2);
	}
}

static void light() {
	//light
	glLightfv(GL_LIGHT0,GL_SPOT_DIRECTION,lightdirection);
	glColor3f(1.0, 1.0, 0.5);
	glBegin(GL_LINES);
	glVertex3fv(light0_pos);
	glVertex3fv(lightdirection);
	glEnd();

	glLightfv(GL_LIGHT0, GL_POSITION, light0_pos);
}

void RenderScene(void) {
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	glDisable(GL_COLOR_ARRAY);
	bool lighting = glIsEnabled(GL_LIGHTING);
	bool tex = glIsEnabled(GL_TEXTURE_2D);
	glDisable(GL_LIGHTING);
	glDisable(GL_TEXTURE_2D);
	glColor3f(0.5, 0.5, 0.5);

	RenderStatusLine();

	glPushMatrix();
	//eye space light
	light();

	//push origin forward along z so that we can see it
	glTranslatef(0, 0, -4);

	glRotatef(xRot - 80, 1.0f, 0.0f, 0.0f);
	glRotatef(zRot - 135, 0.0f, 0.0f, 1.0f);

	//wirecube();

	if (lighting) {
		glEnable(GL_LIGHTING);
	}
	if(tex){
		glEnable(GL_TEXTURE_2D);
	}

	///////

	if (wires) {
		glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
	} else {
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
	}

	drawGeometry();

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

	gluPerspective(35.0f, aspectRatio, 0.6, 7.0);

	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
}

void SetupRC(bool usenormals, bool usecolor, bool gentexcoords) {

	glShadeModel(GL_SMOOTH);
	glEnable(GL_DEPTH_TEST);

	glEnableClientState(GL_VERTEX_ARRAY);
	if (usenormals)
		glEnableClientState(GL_NORMAL_ARRAY);
	if (usecolor)
		glEnableClientState(GL_COLOR_ARRAY);

	if (gentexcoords)
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);
	glerr("SetupRC clientstate");

	//light
	glEnable(GL_LIGHTING);
	float globalambient[] = { 0, 0, 0 };
	glLightModelfv(GL_LIGHT_MODEL_AMBIENT, globalambient);

	static float moonlight = 0.7;
	float ambientLight[] = { 1 - moonlight, 1 - moonlight, 1 - moonlight, 1.0 };
	float diffuseLight[] = { moonlight, moonlight, moonlight, 1.0 };
	float fogcolor[] = { 0,0,0};
	float specularLight[] = { 1.0, 1.0, 1.0, 1.0 };
	float specref[] = { 0.4, 0.4, 0.4, 1.0 };

	glEnable(GL_LIGHT0);
	glLightfv(GL_LIGHT0, GL_AMBIENT, ambientLight);
	glLightfv(GL_LIGHT0, GL_DIFFUSE, diffuseLight);
	glLightfv(GL_LIGHT0, GL_SPECULAR, specularLight);
	glLightf(GL_LIGHT0,GL_SPOT_CUTOFF,45.0f);
	glLightf(GL_LIGHT0,GL_SPOT_EXPONENT,10);

	glEnable(GL_COLOR_MATERIAL);
	glColorMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE);
	glMaterialfv(GL_FRONT, GL_SPECULAR, specref);
	glMateriali(GL_FRONT, GL_SHININESS, 128);
	glLightModeli(GL_LIGHT_MODEL_COLOR_CONTROL, GL_SEPARATE_SPECULAR_COLOR);

	glClearColor(0,0,0,0);
	glerr("SetupRC light");
	//fog
	glEnable(GL_FOG);
	glFogfv(GL_FOG_COLOR, fogcolor);
	glFogf(GL_FOG_START, 4.0f);
	glFogf(GL_FOG_END, 5.0f);

	glFogi(GL_FOG_MODE, GL_LINEAR);
	glerr("SetupRC fog");
	//texture

	glEnable(GL_TEXTURE_2D);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

	GLfloat sPlane[] = { 0.0f, 0.0f, 1.0f, 0.5f };
	GLfloat tPlane[] = { 0.0f, 0.0f, 1.0f, 0.5f };

	// Object Linear
	glEnable(GL_TEXTURE_GEN_S);
	glEnable(GL_TEXTURE_GEN_T);

	glTexGeni(GL_S, GL_TEXTURE_GEN_MODE, GL_OBJECT_LINEAR);
	glTexGeni(GL_T, GL_TEXTURE_GEN_MODE, GL_OBJECT_LINEAR);
	glTexGenfv(GL_S, GL_OBJECT_PLANE, sPlane);
	glTexGenfv(GL_T, GL_OBJECT_PLANE, tPlane);

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
