#include "PointSource.h"
#include <stdlib.h>
/* OPTIMIZE:
 * 1) precalculate spatial fragment
 * 2) precalculate temporal fragment during a semiperiod , extend to whole period by reflexion
 * combine : spatial_cache[x,y] -> A , r ; t,r -> t1 , temporal_cache[t1] -> S
 * return A*S
 %   cumulative   self               self     total
 time   seconds   seconds    calls   ms/call  ms/call  name
 49.66      8.72     8.72 276039060     0.00     0.00  osqrt
 32.07     14.36     5.63 138019530     0.00     0.00  pointwave
 7.46     15.67     1.31 13801953      0.00     0.00  waves
 2.87     16.18     0.51 138019530     0.00     0.00  oexp
 2.85     16.68     0.50     1353      0.37    12.79  constructParametricMesh
 2.45     17.11     0.43 13801953      0.00     0.00  fillcolor
 1.11     17.30     0.20 138019530     0.00     0.00  osin
 */
float osin(float x) {
	return sin(x);
}
float osqrt(float x) {
	return sqrt(x);
}
float oexp(float x) {
	return exp(x);
}
/*************pointsource*******************/
void PointSource__init__(PointSource *src, float x0, float y0, float A0,
		float freq, float speed, float phase0, float damping_coeff) {
	src->x0 = x0;
	src->y0 = y0;
	src->A0 = A0;
	src->phase0 = phase0;
	src->damping_coeff = damping_coeff;

	src->omega = 2 * PI * freq;
	src->k = src->omega / speed;
	src->lambda = speed / freq;
}

void PointSource_spatial_frag(const PointSource *src, float x, float y,
		float *r, float *A) {
	x = x - src->x0;
	y = y - src->y0;

	*r = osqrt(x * x + y * y);
	float damping = oexp(-src->omega * src->damping_coeff * *r);
	*A = src->A0 * damping * osqrt(src->lambda / (*r + src->lambda));
}

float PointSource_temporal_frag(const PointSource *src, float A, float r,
		float t) {
	return A * osin(src->omega * t - src->k * r + src->phase0);
}

float PointSource_pointwave(const PointSource *src, float x, float y, float t) {
	x = x - src->x0;
	y = y - src->y0;

	float r = osqrt(x * x + y * y);
	float damping = oexp(-src->omega * src->damping_coeff * r);
	float A = src->A0 * damping * osqrt(src->lambda / (r + src->lambda));

	return A * osin(src->omega * t - src->k * r + src->phase0);
}
/****************************caching pointsource**************************************/

void compute_spatials(const PointSource * psrc, Vector2 *ret, short samples,
		float interval_width) {

	for (int sidx = 0; sidx <= samples; sidx += 1) {
		float s = interval_width * sidx / samples;

		for (int tidx = 0; tidx <= samples; tidx += 1) {
			float t = interval_width * tidx / samples;

			float x = 2 * s - 1;
			float y = 2 * t - 1;

			float r, A;
			int idx = sidx * (samples + 1) + tidx;

			PointSource_spatial_frag(psrc, x, y, &r, &A);

			ret[idx][0] = r;
			ret[idx][1] = A;
		}
	}
}

void CachingSource__init__(CachingSource *self, const parametricmesh* pm,
		float x0, float y0, float A0, float freq, float speed, float phase0,
		float damping_coeff) {
	PointSource__init__(&self->psrc, x0, y0, A0, freq, speed, phase0,
			damping_coeff);
	self->paramesh = pm;
	self->spatials = malloc(sizeof(Vector2) * (pm->samples + 1) * (pm->samples
			+ 1));
	compute_spatials(&self->psrc, self->spatials, pm->samples,
			pm->interval_width);
}

void CachingSource__dispose(CachingSource *self){
	free(self->spatials);
}
/********************multisources*************************************/

void randPointSources(PointSource *sources, short nsources) {
	float minpuls = frand(0.3, 3);

	for (int i = 0; i < nsources; i++) {
		PointSource__init__(&sources[i], frand(-1, 1), frand(-1, 1), 0.4,
				minpuls + frand(0, i), 1, frand(0, PI), 0.1);
	}
}

void CachingSources__init__(CachingSource* sources, short nsources,
		const parametricmesh *pm) {
	float minpuls = frand(0.3, 3);
	for (int i = 0; i < nsources; i++) {
		CachingSource__init__(&sources[i], pm, frand(-1, 1), frand(-1, 1), 0.4,
								minpuls + frand(0, i), 1, frand(0, PI), 0.1);
	}
}
void CachingSources__dispose__(CachingSource* sources, short nsources){
	for (int i = 0; i < nsources; i++) {
		CachingSource__dispose(&sources[i]);
	}
}
void viewspatial(float s, float t, void *ctxt, Vector3 result,
		int sidx, int tidx) {
	waves_context* context = (waves_context*)ctxt;
	if (context->nsources > 0) {
		CachingSource *source = &context->sources[0];
		const int samples = source->paramesh->samples;

		const int idx = sidx * (samples + 1) + tidx;
		float z = 0;
		for (int i = 0; i < context->nsources; i++) {
			CachingSource *src = &context->sources[i];
			const float A = src->spatials[idx][1];
			z += A;
		}
		const float x = 2 * s - 1;
		const float y = 2 * t - 1;
		result[0] = x;
		result[1] = y;
		result[2] = z;
	}
}

void waves_spatial_cache(float s, float t, void* ctxt, Vector3 result, int sidx,
		int tidx) {
	waves_context* context = (waves_context*)ctxt;
	if (context->nsources > 0) {
		const short samples = context->sources[0].paramesh->samples;
		const int idx = sidx * (samples + 1) + tidx;

		float z = 0;

		for (int i = 0; i < context->nsources; i++) {
			CachingSource *src = &context->sources[i];
			const float r = src->spatials[idx][0];
			const float A = src->spatials[idx][1];
			z += PointSource_temporal_frag(&src->psrc, A, r, context->time);
		}

		const float x = 2 * s - 1;
		const float y = 2 * t - 1;

		result[0] = x;
		result[1] = y;
		result[2] = z;

	}
}

void waves_no_cache(float s, float t, void* ctxt, Vector3 result, int sidx,
		int tidx) {

		waves_context* context = (waves_context*)ctxt;
		const float x = 2 * s - 1;
		const float y = 2 * t - 1;
		float z = 0;

		for (int i = 0; i < context->nsources; i++) {
			CachingSource *src = &context->sources[i];
			z += PointSource_pointwave(&src->psrc,x,y,context->time);
		}

		result[0] = x;
		result[1] = y;
		result[2] = z;

}
