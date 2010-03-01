#include <GL/gl.h>
#include <GL/glu.h>
#include <stdbool.h>

//toolkit independent rendering routines

#define BR_NOLIGHT 0
#define BR_LIGHT_DIFFUSE 1
#define BR_LIGHT_DIFFUSE_AND_SPECULAR 2
#define BR_LIGHT_DIFFUSE_AND_SPECULAR_AND_FOG 3
#define BR_LIGHT_ALL_AND_TEX 4
#define BR_LIGHT_ENUMLEN 5

#define WRONG_TRANSPARENCY 0

void RenderScene(void);
void ChangeSize(GLsizei w, GLsizei h);
void SetupRC(bool usenormals, bool usecolor, bool gentexcoords);
void SetupLight(int lighting);

void drawGeometry();
void glerr(char* source);
