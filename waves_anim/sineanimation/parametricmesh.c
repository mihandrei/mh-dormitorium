#include "parametricmesh.h"
#include <stdlib.h>
#include <GL/gl.h>

static inline int pointidx(short sidx, short tidx, const short samples) {
	return (samples + 1) * sidx + tidx;
}

static inline void subtract_points_at(Vector3 result, const parametricmesh *pm,
		short sidx1, short tidx1, short sidx2, short tidx2) {
	const AppendableMesh *m = pm->mesh;
	SubtractVectors3(result, m->pVerts[pointidx(sidx1, tidx1, pm->samples)],
			m->pVerts[pointidx(sidx2, tidx2, pm->samples)]);
}

static void parametricmesh__computeIndices(parametricmesh* paramesh) {
	const short samples = paramesh->samples;
	AppendableMesh *m = paramesh->mesh;
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
	AppendableMesh *m = paramesh->mesh;

	int idx = 0;
	for (int sidx = 0; sidx <= samples; sidx += 1) {
		for (int tidx = 0; tidx <= samples; tidx += 1) {
			m->pTexCoords[idx][0] = sidx / (samples + 1.0f);
			m->pTexCoords[idx][1] = tidx / (samples + 1.0f);

			idx++;
		}
	}
}

parametricmesh* parametricmesh__init__(short samples, float interval_width,
		bool usenormals, bool usecolor, bool gentexcoords,int colortype) {
	//samples+1 vertecsi pe rand
	//pentru capete 1 vertex un indice 2*(samples+1) ;in mijloc 2 indecsi
	int nMaxIndexes = 2 * samples * (samples + 1);

	parametricmesh *m = malloc(sizeof(parametricmesh));
	m->samples = samples;
	m->interval_width = interval_width;
	m->colortype = colortype;

	m->mesh = AppendableMesh__init__(nMaxIndexes, usecolor, usenormals, gentexcoords);

	parametricmesh__computeIndices(m);
	if (gentexcoords)
		parametricmesh_compute_texcoords(m);

	return m;
}

void parametricmesh__dispose__(parametricmesh *m) {
	AppendableMesh__dispose__(m->mesh);
	free(m);
}

static void parametricmesh_compute_normals(parametricmesh* paramesh) {
	AppendableMesh *m = paramesh->mesh;
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

static void fillcolor(float s, float t, const Vector3 point, Vector3 color,
		int parametric);

static void parametricmesh_compute_heightmap(parametricmesh* paramesh,
		parametricSurface func, void* fncontext) {

	const short samples = paramesh->samples;
	const float interval_width = paramesh->interval_width;

	AppendableMesh *m = paramesh->mesh;

	m->nverts = 0;

	for (int sidx = 0; sidx <= samples; sidx += 1) {
		float s = interval_width * sidx / samples;

		for (int tidx = 0; tidx <= samples; tidx += 1) {
			float t = interval_width * tidx / samples;

			//add next point
			int idx = m->nverts;
			float* point = m->pVerts[idx];
			m->nverts += 1;

			//fill in point
			func(s, t, fncontext, point, sidx, tidx);

		}
	}
}

static void parametricmesh_compute_colormap(parametricmesh* paramesh) {

	const short samples = paramesh->samples;
	const float interval_width = paramesh->interval_width;

	AppendableMesh *m = paramesh->mesh;

	int idx = 0;

	for (int sidx = 0; sidx <= samples; sidx += 1) {
		float s = interval_width * sidx / samples;

		for (int tidx = 0; tidx <= samples; tidx += 1) {
			float t = interval_width * tidx / samples;

			//add next point
			float* point = m->pVerts[idx];
			float* color = m->pColor[idx];
			idx += 1;

			fillcolor(s, t, point, color, paramesh->colortype);

		}
	}
}

void computeParametricMesh(parametricmesh* paramesh, parametricSurface func,
		void* fncontext) {

	parametricmesh_compute_heightmap(paramesh, func, fncontext);

	if (paramesh->mesh->hascolor)
		parametricmesh_compute_colormap(paramesh);
	if (paramesh->mesh->hasnorms)
		parametricmesh_compute_normals(paramesh);
}

void drawParametricMesh(parametricmesh* pmesh) {
	const AppendableMesh *m = pmesh->mesh;
	const int indexes_per_row = 2 * (pmesh->samples + 1);

	glVertexPointer(3, GL_FLOAT, 0, m->pVerts);

	if (pmesh->mesh->hasnorms)
		glNormalPointer(GL_FLOAT, 0, m->pNorms);
	if (pmesh->mesh->hascolor)
		glColorPointer(3, GL_FLOAT, 0, m->pColor);
	if (pmesh->mesh->hastex)
		glTexCoordPointer(2, GL_FLOAT, 0, m->pTexCoords);

	for (int i = 0; i < pmesh->samples; i++) {
		glDrawElements(GL_TRIANGLE_STRIP, indexes_per_row, GL_UNSIGNED_SHORT,
				m->pIndexes + i * indexes_per_row);
	}
}

static void fillcolor(float s, float t, const Vector3 point, Vector3 color,
		int type) {
	if (type == 0) {
		color[0] = 1;
		color[1] = 1;
		color[2] = 1;
	}else if (type == 1) {
		color[0] = 1 - t;
		color[1] = s;
		color[2] = t;
	} else {
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
	}
}
