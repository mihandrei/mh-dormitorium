#ifndef SURFACEMESH_H_
#define SURFACEMESH_H_

struct color{
	const float r,g,b;
	color(float r_ ,float g_,float b_): r(r_), g(g_), b(b_){
	}
};

typedef float (* surfaceFn) (float, float);
typedef color (* palletteFn) (float color_index);

color defpallete(float);
color greypallete(float);

/*
 *plots the surface by aproximating it with a triangle mesh
 *for f = 0 the triangles are equilateral
 *the mesh is drawn as a sequence of triangle strips
 */
class Mesh {
	surfaceFn func;
	float xmin,xmax,ymin,ymax;
	float step;
	float color_interval;
	palletteFn pallete;
	void setcolor(float color_index);
public:
	Mesh(float xmin = -1, float xmax = 1,
			float ymin = -1, float ymax = 1, int samples = 50,
			palletteFn pallette = defpallete, float color_interval = 1);
	void draw(surfaceFn func);
};

#endif /* SURFACEMESH_H_ */
