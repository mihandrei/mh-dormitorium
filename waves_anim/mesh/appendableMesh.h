#ifndef _APPENDABLEMESH
#define _APPENDABLEMESH

#include "../math/math3d.h"
#include <stdbool.h>

typedef struct {
	unsigned short *pIndexes;
	Vector3 *pVerts;
	Vector3 *pNorms;
	Vector3 *pColor;
	Vector2 *pTexCoords;
	unsigned short nindexes;
	unsigned short nverts;
	unsigned int nMaxIndexes;// immutable
	bool hasnorms;
	bool hastex;
	bool hascolor;
} AppendableMesh;

AppendableMesh* AppendableMesh__init__(int nMaxIndexes, bool color, bool norms, bool tex);
void AppendableMesh__dispose__(AppendableMesh *m);

void AppendableMesh_appendTriangle(AppendableMesh *m,Vector3 verts[3], Vector3 vNorms[3],
		Vector2 vTexCoords[3],Vector3 vColor[3]);


void AppendableMesh_draw(AppendableMesh*m);

#endif
