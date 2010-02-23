#include <stdlib.h>
#include "obj_loader.h"
#include "obj_parser.h"
#include "../math/math3d.h"

AppendableMesh* loadmodel(char* model_file) {
	obj_scene_data data;
	AppendableMesh *obm = NULL;
	if (parse_obj_scene(&data, model_file)) {
		obm = AppendableMesh__init__(data.face_count * 3, false, true, true);

		for (int i = 0; i < data.face_count; i++) {
			Vector3 verts[3];
			Vector3 vNorms[3];
			Vector2 vTexCoords[3];
			Vector3 vColor[3];
			obj_vector *v, *t, *n;

			obj_face *o = data.face_list[i];
			bool havenorms = true;
			for (int j = 0; j < 3; j++) {
				int idx = o->vertex_index[j];
				v = data.vertex_list[idx];
				verts[j][0] = v->e[0];
				verts[j][1] = v->e[1];
				verts[j][2] = v->e[2];

				idx = o->normal_index[j];
				if (idx != -1) {
					n = data.vertex_normal_list[idx];
					vNorms[j][0] = n->e[0];
					vNorms[j][1] = n->e[1];
					vNorms[j][2] = n->e[2];
				} else {
					havenorms = false;
				}
				idx = o->texture_index[j];
				if (idx != -1) {
					t = data.vertex_texture_list[idx];
					vTexCoords[j][0] = t->e[0];
					vTexCoords[j][1] = t->e[1];
				} else {
					vTexCoords[j][0] = 0;
					vTexCoords[j][1] = 0;
				}
			}
			if (!havenorms) {
				Vector3 d1, d2;
				SubtractVectors3(d1, verts[1], verts[0]);
				SubtractVectors3(d2, verts[1], verts[2]);
				CrossProduct(vNorms[0], d2, d1);
				NormalizeVector(vNorms[0]);
				CopyVector3(vNorms[1], vNorms[0]);
				CopyVector3(vNorms[2], vNorms[0]);
			}
			AppendableMesh_appendTriangle(obm, verts, vNorms, vTexCoords, vColor);
		}

		delete_obj_data(&data);
	}
	return obm;
}
