#include "mesh.h"
#include <stdlib.h>
#include <GL/gl.h>

mesh* mesh__init__(int nMaxIndexes) {
	mesh *m = malloc(sizeof(mesh));
	m->pIndexes = malloc(sizeof(short) * nMaxIndexes);
	m->pVerts = malloc(sizeof(Vector3) * nMaxIndexes);
	m->pNorms = malloc(sizeof(Vector3) * nMaxIndexes);
	m->pColor = malloc(sizeof(Vector3) * nMaxIndexes);
	m->nindexes = 0;
	m->nverts = 0;
	m->nMaxIndexes = nMaxIndexes;
	return m;
}

void mesh__dispose__(mesh *m) {
	free(m->pIndexes);
	free(m->pVerts);
	free(m->pNorms);
	free(m->pColor);
	free(m);
}

parametricmesh* parametricmesh__init__(short samples, float interval_width) {
	//samples+1 vertecsi pe rand
	//pentru capete 1 vertex un indice 2*(samples+1) ;in mijloc 2 indecsi
	int nMaxIndexes = 2 * samples * (samples + 1);
	parametricmesh *m = malloc(sizeof(parametricmesh));
	m->mesh = mesh__init__(nMaxIndexes);
	m->samples = samples;
	m->interval_width = interval_width;
	return m;
}

void parametricmesh__dispose__(parametricmesh *m) {
	mesh__dispose__(m->mesh);
	free(m);
}

void fillcolor(float s, float t, const Vector3 point, Vector3 color);

//index array does not change , depend only on the nr of samples
//TODO: do not recompute
void computeParametricMesh(parametricmesh* paramesh, parametricSurface func,
		void* fncontext) {

	mesh *m = paramesh->mesh;
	m->nindexes = 0;
	m->nverts = 0;
	short samples = paramesh->samples;
	float interval_width = paramesh->interval_width;

	for (int sidx = 0; sidx <= samples; sidx += 1) {
		float s = interval_width * sidx / samples;

		for (int tidx = 0; tidx <= samples; tidx += 1) {
			float t = interval_width * tidx / samples;

			//add index to left point if not on row 0
			if (sidx != 0) {
				int point_left_idx = (sidx - 1) * (samples + 1) + tidx;
				m->pIndexes[m->nindexes] = point_left_idx;
				m->nindexes += 1;
			}

			//add next point
			int idx = m->nverts;
			float* point = m->pVerts[idx];
			float* color = m->pColor[idx];
			m->nverts += 1;

			//fill in point
			func(s, t, fncontext, point, sidx, tidx);
			fillcolor(s, t, point, color);

			if (sidx != 0) {//add index to point if not on row 0
				m->pIndexes[m->nindexes] = idx;
				m->nindexes += 1;
			}
		}
	}

}

void drawParametricMesh(parametricmesh* pmesh) {
	mesh *m = pmesh->mesh;
	glVertexPointer(3, GL_FLOAT, 0, m->pVerts);
	//glNormalPointer(GL_FLOAT, 0, m->pNorms);
	glColorPointer(3, GL_FLOAT, 0, m->pColor);

	int indexes_per_row = 2 * (pmesh->samples + 1);

	for (int i = 0; i < pmesh->samples; i++) {
		glDrawElements(GL_TRIANGLE_STRIP, indexes_per_row, GL_UNSIGNED_SHORT,
				m->pIndexes + i * indexes_per_row);
	}
}

void fillcolor(float s, float t, const Vector3 point, Vector3 color) {
#ifdef PARAMETRIC_COLOR
	color[0] = 1 - t;
	color[1] = s;
	color[2] = t;
#else
	float c = (point[2] / COLOR_INTERVAL + 1) / 2;
	c = 1 - c;
	if (c < 0) { //red->black funcval-> -inf
		c = 1 - c;
		color[0] = 1 / (c * c * c * c);
		color[1] = 0;
		color[2] = 0;
	} else if (c < 0.25) { // red orange
		color[0] = 1;
		color[1] = c * 2;
		color[2] = 0;
	} else if (c < 0.5) { //orange yellow
		color[0] = 1;
		color[1] = c * 2;
		color[2] = 0;
	} else if (c < 0.75) { //yellow cyan
		c = 4 * c - 2;
		color[0] = 1 - c;
		color[1] = 1;
		color[2] = c;
	} else if (c < 1) { // cyan blue
		color[0] = 0;
		color[1] = 4 - 4 * c;
		color[2] = 1;
	} else { //blue->black for funcval->+inf
		color[0] = 0;
		color[1] = 0;
		color[2] = 1 / (c * c * c * c);
	}
#endif
}
