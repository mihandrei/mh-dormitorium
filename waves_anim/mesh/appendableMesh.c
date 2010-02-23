#include "appendableMesh.h"
#include <stdlib.h>
#include <string.h>
#include <GL/gl.h>

AppendableMesh* AppendableMesh__init__(int nMaxIndexes, bool color, bool norms, bool tex) {
	AppendableMesh *m = malloc(sizeof(AppendableMesh));
	m->pIndexes = malloc(sizeof(short) * nMaxIndexes);
	m->pVerts = malloc(sizeof(Vector3) * nMaxIndexes);

	m->hasnorms = norms;
	m->hastex = tex;
	m->hascolor = color;
	if (norms)
		m->pNorms = malloc(sizeof(Vector3) * nMaxIndexes);
	if (color)
		m->pColor = malloc(sizeof(Vector3) * nMaxIndexes);
	if (tex)
		m->pTexCoords = malloc(sizeof(Vector2) * nMaxIndexes);

	m->nindexes = 0;
	m->nverts = 0;
	m->nMaxIndexes = nMaxIndexes;

	return m;
}

void AppendableMesh__dispose__(AppendableMesh *m) {
	free(m->pIndexes);
	free(m->pVerts);

	if (m->pNorms != NULL)
		free(m->pNorms);
	if (m->pColor != NULL)
		free(m->pColor);
	if (m->pTexCoords != NULL)
		free(m->pTexCoords);

	free(m);
}

void AppendableMesh_appendTriangle(AppendableMesh*m, Vector3 verts[3], Vector3 vNorms[3],
		Vector2 vTexCoords[3], Vector3 vColor[3]) {
	for (int iVertex = 0; iVertex < 3; iVertex++) {
		int nNumVerts = m->nverts;
		memcpy(m->pVerts[nNumVerts], &verts[iVertex], sizeof(Vector3));
		if (m->hasnorms)
			memcpy(m->pNorms[nNumVerts], &vNorms[iVertex], sizeof(Vector3));
		if (m->hastex)
			memcpy(m->pTexCoords[nNumVerts], &vNorms[iVertex], sizeof(Vector3));
		if (m->hascolor)
			memcpy(m->pColor[nNumVerts], &vTexCoords[iVertex], sizeof(Vector2));

		m->pIndexes[m->nindexes] = nNumVerts;
		m->nverts += 1;
		m->nindexes += 1;
	}
}

void AppendableMesh_draw(AppendableMesh*m) {
	glVertexPointer(3, GL_FLOAT, 0, m->pVerts);

	if (m->hasnorms)
		glNormalPointer(GL_FLOAT, 0, m->pNorms);
	if (m->hascolor)
		glColorPointer(3, GL_FLOAT, 0, m->pColor);
	if (m->hastex)
		glTexCoordPointer(2, GL_FLOAT, 0, m->pTexCoords);

	glDrawElements(GL_TRIANGLES, m->nindexes, GL_UNSIGNED_SHORT, m->pIndexes);
}
