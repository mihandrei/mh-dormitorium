#include <cmath>
#include <vector>
#include <ctime>
#include <cstdlib>
#include <iostream>

#include <GL/glut.h>
#include "common.h"
#include "math.h"

using namespace std;

namespace morph {

static const int samples = 80;

static long frame;

struct point {
	const float x, y, z;
	point(float x_, float y_, float z_) :
		x(x_), y(y_), z(z_) {
	}
};

//find a fix for floating point errors when s is very close to R
point sphere_(float s, float t) {
	//return point(s,t,0);
	float R = 0.5;
	s = (s * 2 - 1) * R;
	t = t * 2 * PI;

	if (s > R)
		return point(NAN, NAN, NAN);

	float r = sqrt((R + s) * (R - s)); //written like this it is numerically more stable

	return point(cos(t) * r, sin(t) * r, s);
}

point blob(float s, float t) {
	float time = frame * 0.06;
	s = PI * s;
	t = PI * t * 2;

	float R = 0.55 + 0.45*sin(time) * sin(4*s) * sin(4*t);

	float r = R * sin (s) ;

	return point(
			r * cos(t),
			r * sin(t),
			R * cos(s) );
}

point melc(float s, float t) {
	float time = frame * 0.02 ;
	float cut = sin(time) ;

	s = PI * ( (0.5 - cut) + s * ( 2 * cut));
	t = PI * t * 4;

	float R =  t/PI/4;

	float r = R * sin (s) ;

	return point(
			r * cos(t),
			r * sin(t),
			R * cos(s) );
}

//s,t in [0..1]
point func(float s, float t) {
	return melc(t, s);
}

void print_point(point p) {
	//cout << "[" << p.x << "," << p.y << "," << p.z << "]" << endl;
}

//		for (float t = 0; t <= 1; t += step) {
// is *not* splitting 0..1 evenly but some 0..k, k>1 .
// 1 will never be reached
// floating point rounding makes step slightly bigger
// Fix: use integers for interval division; do floating point operations per dot
// 0..1 the divisions will not be equal as above but 1 will be reached

void drawGeometry(long frame_) {
	glPushMatrix();
	frame = frame_;
	glRotatef(-frame*3,0,0,1);
	const float interval_width = 1;

	for (int sdot = 0; sdot < samples; sdot += 1) {
		float s = (float) interval_width * sdot / (float) samples;

		glBegin(GL_TRIANGLE_STRIP);
		for (int tdot = 0; tdot <= samples; tdot += 1) {
			float t = (float) interval_width * tdot / (float) samples;

			point fl = func(s, t);
			if (fl.x != NAN) { //test if function is defined here
				print_point(fl);
				glColor3f(1 - t, s, t); //parametric colors
				glVertex3f(fl.x, fl.y, fl.z);
			}

			float next_s = (float) interval_width * (sdot + 1) / (float)samples;
			point fr = func(next_s, t);
			if (fr.x != NAN) {
				print_point(fr);
				glColor3f(1 - t, next_s, t); //parametric colors
				glVertex3f(fr.x, fr.y, fr.z);
			}
		}
		glEnd();
	}
	glPopMatrix();
}

}

int _main(int argc, char* argv[]) {
	srand(time(NULL));
	renderSettings settings;
	settings.fps = 20;
	settings.drawGeometry = morph::drawGeometry;

	return render_run(settings, argc, argv);
}

