#include <GL/gl.h>
#include <GL/glut.h>

void renderaxes(float scale){
  glBegin(GL_LINES);
    glColor3f(0.5f,0.2f,0.2f);
    glVertex3f(-scale,0.0f,0.0f);
    glVertex3f(scale,0.0f,0.0f);

    glColor3f(0.2f,0.5f,0.2f);
    glVertex3f(0.0f,-scale,0.0f);
    glVertex3f(0.0f,scale,0.0f);

    glColor3f(0.2f,0.2f,0.5f);
    glVertex3f(0.0f,0.0f,-scale);
    glVertex3f(0.0f,0.0f,scale);
  glEnd();

  glColor3f(1,1,1);
  glutWireCube(2*scale);

  glPushMatrix();
      glColor3f(0.5f,0.2f,0.2f);
      glTranslatef(1,0,0);
      glRotatef(90,0,1,0);
      glutSolidCone(0.03,0.1,8,2);
  glPopMatrix();

  glPushMatrix();
      glColor3f(0.2f,0.5f,0.2f);
      glTranslatef(0,1,0);
      glRotatef(-90,1,0,0);
      glutSolidCone(0.03,0.1,8,2);
  glPopMatrix();

  glPushMatrix();
      glColor3f(0.2f,0.2f,0.5f);
      glTranslatef(0,0,1);
      glutSolidCone(0.03,0.1,8,2);
  glPopMatrix();

}
