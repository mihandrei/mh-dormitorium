#include "mesh.h"
#include <stdlib.h>
#include <GL/gl.h>

mesh* mesh__init__(int nMaxIndexes) {
	mesh *m = malloc(sizeof(mesh));
	m->pIndexes = malloc(sizeof(short) * nMaxIndexes);
	m->pVerts = malloc(sizeof(Vector3) * nMaxIndexes);
	m->pNorms = malloc(sizeof(Vector3) * nMaxIndexes);
	m->pColor = malloc(sizeof(Vector3) * nMaxIndexes);
	m->pTexCoords = malloc(sizeof(Vector2) * nMaxIndexes);
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
	free(m->pTexCoords);
	free(m);
}

static int pointidx(short sidx, short tidx, const short samples) {
	return (samples + 1) * sidx + tidx;
}

static void parametricmesh__computeIndices(parametricmesh* paramesh) {
	const short samples = paramesh->samples;
	mesh *m = paramesh->mesh;
	m->nindexes = 0;
	m->nverts = 0;

	//add indices if not on row 0

	for (int sidx = 1; sidx <= samples; sidx += 1) {
		for (int tidx = 0; tidx <= samples; tidx += 1) {

			//add index to left point
			int point_left_idx = pointidx(sidx - 1, tidx, samples);
			m->pIndexes[m->nindexes] = point_left_idx;
			m->nindexes += 1;

			//add index to point
			int idx = pointidx(sidx, tidx, samples);
			m->pIndexes[m->nindexes] = idx;
			m->nindexes += 1;
		}
	}
}

static void parametricmesh_compute_texcoords(parametricmesh* paramesh) {
	const short samples = paramesh->samples;

	mesh *m = paramesh->mesh;

	int idx = 0;
	for (int sidx = 0; sidx <= samples; sidx += 1) {
		for (int tidx = 0; tidx <= samples; tidx += 1) {
			m->pTexCoords[idx][0] = sidx/(samples+1.0f);
			m->pTexCoords[idx][1] = tidx/(samples+1.0f);

			idx++;
		}
	}

}

parametricmesh* parametricmesh__init__(short samples, float interval_width) {
	//samples+1 vertecsi pe rand
	//pentru capete 1 vertex un indice 2*(samples+1) ;in mijloc 2 indecsi
	int nMaxIndexes = 2 * samples * (samples + 1);
	parametricmesh *m = malloc(sizeof(parametricmesh));
	m->mesh = mesh__init__(nMaxIndexes);
	m->samples = samples;
	m->interval_width = interval_width;
	parametricmesh__computeIndices(m);
	parametricmesh_compute_texcoords(m);
	return m;
}

void parametricmesh__dispose__(parametricmesh *m) {
	mesh__dispose__(m->mesh);
	free(m);
}

static void subtract_points_at(Vector3 result, const parametricmesh *pm,
		short sidx1, short tidx1, short sidx2, short tidx2) {
	const mesh *m = pm->mesh;
	SubtractVectors3(result, m->pVerts[pointidx(sidx1, tidx1, pm->samples)],
			m->pVerts[pointidx(sidx2, tidx2, pm->samples)]);
}

static void parametricmesh_compute_normals(parametricmesh* paramesh) {
	mesh *m = paramesh->mesh;
	const short samples = paramesh->samples;
	Vector3 v1, v2, v3, v4, v5, v6;
	Vector3 n, n1, n2, n3, n4, n5, n6;
	Vector3 edgenormal = { 0, 0, 1 };

	// internal - 6 tri 6 vertices
	for (int sidx = 0; sidx <= samples; sidx++) {
		for (int tidx = 0; tidx <= samples; tidx++) {
			if (sidx == 0 || tidx == 0 || sidx == samples || tidx == samples) {
				CopyVector3(n, edgenormal);
			} else {
				subtract_points_at(v1, paramesh, sidx, tidx + 1, sidx, tidx);
				subtract_points_at(v2, paramesh, sidx - 1, tidx + 1, sidx, tidx);
				subtract_points_at(v3, paramesh, sidx - 1, tidx, sidx, tidx);
				subtract_points_at(v4, paramesh, sidx, tidx - 1, sidx, tidx);
				subtract_points_at(v5, paramesh, sidx + 1, tidx - 1, sidx, tidx);
				subtract_points_at(v6, paramesh, sidx + 1, tidx, sidx, tidx);

				CrossProduct(n1, v1, v2);
				CrossProduct(n2, v2, v3);
				CrossProduct(n3, v3, v4);
				CrossProduct(n4, v4, v5);
				CrossProduct(n5, v5, v6);
				CrossProduct(n6, v6, v1);

				AddVectors3(n, n1, n2);
				AddVectors3(n, n, n3);
				AddVectors3(n, n, n4);
				AddVectors3(n, n, n5);
				AddVectors3(n, n, n6);
				ScaleVector3(n, 1 / 6.0f);
				NormalizeVector(n);
			}

			int idx = pointidx(sidx, tidx, samples);
			CopyVector3(m->pNorms[idx], n);
		}
	}
}

static void fillcolor(float s, float t, const Vector3 point, Vector3 color);

void parametricmesh_compute_heightmap(parametricmesh* paramesh,
		parametricSurface func, void* fncontext) {

	const short samples = paramesh->samples;
	const float interval_width = paramesh->interval_width;

	mesh *m = paramesh->mesh;

	m->nverts = 0;

	for (int sidx = 0; sidx <= samples; sidx += 1) {
		float s = interval_width * sidx / samples;

		for (int tidx = 0; tidx <= samples; tidx += 1) {
			float t = interval_width * tidx / samples;

			//add next point
			int idx = m->nverts;
			float* point = m->pVerts[idx];
			float* color = m->pColor[idx];
			m->nverts += 1;

			//fill in point
			func(s, t, fncontext, point, sidx, tidx);
			fillcolor(s, t, point, color);

		}
	}

}


void computeParametricMesh(parametricmesh* paramesh, parametricSurface func,
		void* fncontext) {
	parametricmesh_compute_heightmap(paramesh, func, fncontext);
	parametricmesh_compute_normals(paramesh);
}

void drawParametricMesh(parametricmesh* pmesh) {
	mesh *m = pmesh->mesh;
	glVertexPointer(3, GL_FLOAT, 0, m->pVerts);
	glNormalPointer(GL_FLOAT, 0, m->pNorms);
	glColorPointer(3, GL_FLOAT, 0, m->pColor);
	glTexCoordPointer(2, GL_FLOAT, 0, m->pTexCoords);

	int indexes_per_row = 2 * (pmesh->samples + 1);

	for (int i = 0; i < pmesh->samples; i++) {
		glDrawElements(GL_TRIANGLE_STRIP, indexes_per_row, GL_UNSIGNED_SHORT,
				m->pIndexes + i * indexes_per_row);
	}
}

static void fillcolor(float s, float t, const Vector3 point, Vector3 color) {
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
