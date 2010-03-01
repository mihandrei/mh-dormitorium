#include "demo.h"
#include "../mesh/surfacemesh.h"

static Pulse gauss, gauss_prev, zagpulse, zagpulse_prev, spherepulse, mode,
		nilpulse;

static float ShiftedGauss(float s, float t, void *data) {
	s -= 0.01;
	t -= 0.01;
	return Pulse_gauss(s, t, data);
}
static float zag(float s, float t, int m[]) {
	float r = sqrt(s * s + t * t);
	if (r > 1)
		return 0;

	float phi = atan2(t, s);
	return 0.2 * sin(r * PI) * sin(5 * phi + r * 4);
}

static float shiftedzag(float s, float t, int m[]) {
	float r = sqrt(s * s + t * t);
	if (r > 1)
		return 0;

	float phi = atan2(t, s);
	phi += 0.03;
	return 0.2 * sin(r * PI) * sin(5 * phi + r * 4);
}

static float multimode(float s, float t, int m[]) {
	int nmodes = m[0];

	float ret = 0;
	for (int i = 0; i < nmodes; i += 2) {
		ret += Pulse_modes(s, t, &m[i]);
	}

	return 2*ret / nmodes;
}

static void multimode_deform(PhysSurf *physs) {
	int count = 8;
	int mn[count + 1];
	mn[0] = count;

	for (int i = 1; i < count + 1; i++) {
		mn[i] = (int) frand(1, 4);
	}

	Pulse_fn(&mode, multimode, mn);
	PhysSurf_deform(physs, 0, 0, &mode, &mode, 0);
}

static void nil_deform(PhysSurf *physs) {
	PhysSurf_deform(physs, 0, 0, &nilpulse, &nilpulse, 0);
}

static void mode_deform(PhysSurf *physs) {
	int mn[2];
	mn[0] = frand(1, 3);
	mn[1] = frand(1, 3);

	Pulse_fn(&mode, Pulse_modes, mn);
	PhysSurf_deform(physs, 0, 0, &mode, &mode, 0);
}

static void gauss_deform(PhysSurf *physs) {
	float c = frand(0.1, 0.3);
	int w = physs->vm->w;
	int h = physs->vm->h;

	Pulse_fn(&gauss, Pulse_gauss, &c);
	Pulse_fn(&gauss_prev, ShiftedGauss, &c);

	PhysSurf_deform(physs, w * 0.25, h * 0.25, &gauss, &gauss_prev, 0);
}

static void zag_deform(PhysSurf *physs) {
	int w = physs->vm->w;
	int h = physs->vm->h;
	PhysSurf_deform(physs, w * 0.1, h * 0.1, &zagpulse, &zagpulse_prev, 0);
}

static void sphere_deform(PhysSurf *physs) {
	int w = physs->vm->w;
	int h = physs->vm->h;
	PhysSurf_deform(physs, w * 0.25, h * 0.25, &spherepulse, &spherepulse, 0);
}

static int deforms_len = 5;
static int current_deform = 0;
static void (*deforms[])() = { zag_deform, mode_deform, multimode_deform,
		gauss_deform, sphere_deform };

void demo_nextdeform(PhysSurf *physs) {
	current_deform = (current_deform + 1) % deforms_len;
	nil_deform(physs);
	deforms[current_deform]();
}

void demo__init(PhysSurf *physs) {
	float c = 0.3;
	int mn[2] = { 2, 3 };
	int w = physs->vm->w;
	int h = physs->vm->h;
	Pulse__init(&gauss, w * 0.7, h * 0.7);
	Pulse__init(&gauss_prev, w * 0.7, h * 0.7);
	Pulse__init(&zagpulse, w * 0.8, h * 0.8);
	Pulse__init(&zagpulse_prev, w * 0.8, h * 0.8);
	Pulse__init(&spherepulse, w * 0.5, h * 0.3);
	Pulse__init(&mode, w, h);
	Pulse__init(&nilpulse, w, h);
	Pulse_fn(&gauss, Pulse_gauss, &c);
	Pulse_fn(&gauss_prev, ShiftedGauss, &c);
	Pulse_fn(&zagpulse, zag, 0);
	Pulse_fn(&zagpulse_prev, shiftedzag, 0);
	Pulse_fn(&spherepulse, Pulse_sphere, 0);
	Pulse_fn(&mode, Pulse_modes, mn);
}

void demo__dispose(void) {
	Pulse__dispose(&gauss);
	Pulse__dispose(&gauss_prev);
	Pulse__dispose(&zagpulse);
	Pulse__dispose(&zagpulse_prev);
	Pulse__dispose(&spherepulse);
	Pulse__dispose(&mode);
	Pulse__dispose(&nilpulse);
}

