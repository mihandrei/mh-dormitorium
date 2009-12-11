#include <iostream>
#include <sstream>

#include <GL/gl.h>
#include <GL/glu.h>
#include <GL/freeglut.h>

#include "common.h"

using namespace std;

static renderSettings settings;

static long frame = 0;

static float xRot = 0.0f;
static float zRot = 0.0f;
static int tik_base; //fps calc

string fps_str = string("fps");

void RenderScene(void){
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	glPushMatrix();
	  glTranslatef(-0.35,-0.35,-1);
	  glScalef(0.0001,0.0001,0.0001);
	  glutStrokeString(GLUT_STROKE_MONO_ROMAN,(const unsigned char*) fps_str.c_str() );
	glPopMatrix();

	glPushMatrix();

	glTranslatef(0,0,-4);

	glRotatef(xRot - 60, 1.0f, 0.0f, 0.0f);
	glRotatef(zRot - 135, 0.0f, 0.0f, 1.0f);

	renderaxes(1.0);

	settings.drawGeometry(frame);

	glPopMatrix();

	glutSwapBuffers();
}

void TimerFunction(int a){
    frame ++;
    glutPostRedisplay();

    if (frame % 100 == 0){
    	int tiks=glutGet(GLUT_ELAPSED_TIME);

    	if (tiks - tik_base > 1000) {
    		ostringstream fps_strm;
    		fps_strm<< "FPS:" << 100 * 1000.0/(tiks-tik_base) << "/" << settings.fps << endl;
    		fps_str = fps_strm.str();
			tik_base = tiks;
		}
    }

    glutTimerFunc(1000/settings.fps,TimerFunction,1);
}

void ChangeSize(GLsizei w, GLsizei h){
    GLfloat aspectRatio;
    // Prevent a divide by zero
    if(h == 0)
        h = 1;
    // Set Viewport to window dimensions
    glViewport(0, 0, w, h);
    // Reset coordinate system
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    // Establish clipping volume (left, right, bottom, top, near, far)
    aspectRatio = (GLfloat)w / (GLfloat)h;

    gluPerspective(40.0f,aspectRatio, 0.5, 10);

    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();
}

bool wires =false;

void processSpecialKeys(int key, int x, int y) {
    switch(key) {
          case GLUT_KEY_LEFT:
            zRot += 1;
            break;
          case GLUT_KEY_RIGHT:
            zRot -= 1;
            break;
          case GLUT_KEY_UP:
            xRot += 1;
            break;
          case GLUT_KEY_DOWN:
            xRot -= 1;
            break;
          case GLUT_KEY_F1:
        	if(wires){
        	  glPolygonMode(GL_FRONT_AND_BACK,GL_FILL);
        	}else{
        	  glPolygonMode(GL_FRONT_AND_BACK,GL_LINE);
        	}
        	wires = !wires;
           break;
          default:
        	  settings.onKey(key);
            break;
    }
}

void SetupRC(void){
	glClearColor(0.0f,0.0f,0.0f,1.0f);
	glShadeModel(GL_FLAT);
	glEnable(GL_DEPTH_TEST);
	glEnable(GL_LIGHTING);
	glEnable(GL_COLOR_MATERIAL);

	float v[] = {1.0,1.0,1.0,1.0};
	glLightModelfv(GL_LIGHT_MODEL_AMBIENT, v);
	glColorMaterial(GL_FRONT,GL_AMBIENT_AND_DIFFUSE);
}

int render_run(const renderSettings settings_, int argc, char* argv[]){
    settings = settings_;
	glutInit(&argc, argv);
	glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGBA | GLUT_DEPTH );
	glutInitWindowSize(700, 700);
	glutCreateWindow("Simple");

	glutDisplayFunc(RenderScene);
	glutReshapeFunc(ChangeSize);
	glutTimerFunc(53,TimerFunction,1);

	glutSpecialFunc(processSpecialKeys);

	SetupRC();
	glutMainLoop();
	return 0;
}

