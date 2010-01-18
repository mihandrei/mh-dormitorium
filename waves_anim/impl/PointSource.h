#ifndef POINTSOURCE_H_
#define POINTSOURCE_H_

#include "../shared/math3d.h"
#include "../shared/mesh.h"

/*
 *a point wave centered in the origin
 *uniform wave speed assumed => y(r,t) = A(r)sin(wt-kx) ; k = w/c = 2pi/lambda
 *wave hill equiv spring => pot energy ~ y^2
 *wave front a circle  => to maintain const. pot energy per wave front
 *E per front = integr dE ; dE = A^2 * k * (lambda * dl) ; [k] = N / m^2
 *A(r) = A0 * sqrt(lambda / r)
 */
typedef struct{
     float x0, y0 , A0 , phase0;
     float omega, lambda, k;
     float damping_coeff;
}PointSource;


void PointSource__init__(PointSource *self, float x0 , float y0, float A0 ,
			float freq , float speed, float phase0,
			float damping_coeff);

float PointSource_pointwave(const PointSource *src, float x, float y, float t);

typedef struct{
	const PointSource psrc;			//the source
	const parametricmesh *paramesh;   //the mesh for which the source is precomputed
	Vector2* spatials;			//precomputed spatial components
}CachingSource;

void CachingSource__init__(CachingSource *self, const parametricmesh* pm ,
		float x0, float y0, float A0, float freq, float speed, float phase0, float damping_coeff);
void CachingSource__dispose(CachingSource *self);
void CachingSources__dispose__(CachingSource* sources, short nsources);
typedef struct{
	CachingSource *sources;
	short nsources;
	float time;
}waves_context;

void randPointSources(PointSource *sources, short nsources);
void CachingSources__init__(CachingSource* sources, short nsources,const parametricmesh *pm);

void waves_spatial_cache(float s, float t, void *context,Vector3 result,int sidx,int tidx) ;
void waves_no_cache(float s, float t, void *context,Vector3 result,int sidx,int tidx) ;
void viewspatial(float s, float t, void *ctxt, Vector3 result,
		int sidx, int tidx);



#endif /* POINTSOURCE_H_ */
