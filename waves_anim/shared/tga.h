#include <GL/gl.h>
GLint gltWriteTGA(const char *szFileName);
GLbyte *gltLoadTGA(const char *szFileName, GLint *iWidth, GLint *iHeight,
		GLint *iComponents, GLenum *eFormat);
