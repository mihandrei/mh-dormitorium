#include <cmath>
#include <vector>
#include <ctime>
#include <cstdlib>
#include <iostream>

#include <GL/glut.h>
#include "common.h"
#include "math.h"
#include "Mesh.h"
#include "PointSource.h"

using namespace std;

namespace wave {

static const int samples = 100;

static Mesh mesh = Mesh(-1.0f, 1.0f, -1.0f, 1.0f, samples,defpallete, 0.5);
static long frame;
static vector<PointSource> sources = vector<PointSource> ();

void addSource() {
	if (sources.size() > 5)
		return;

	sources.push_back(PointSource(randf(-1, 1), randf(-1, 1), 0.5,
			randf(0.4, 5), 1, randf(0, PI)));
}

void removeSource() {
	if (sources.size() != 0)
		sources.pop_back();
}

float func(float x, float y) {
	//time grain  << T period to eliminate stobe effects
	//time grain 0.01
	float t = frame * 0.01;

	float result = 0;

	vector<PointSource>::iterator it = sources.begin();
	for (; it < sources.end(); it++) {
		//cache = {source: [nx][ny]}
		// cache.add(*it, x , y , value = (*it).spatial(x,y)
		//if cache contains (*it)
		//cache.get(*it,x,y);  result+=(*it).z(value,t)
		result += (*it).z(x, y, t);
	}

	return result;
}

void drawGeometry(long frame_) {
	frame = frame_;
	mesh.draw(func);
//	glutSolidTeapot(1);
}

void onKey(int key) {
	switch (key) {
	case GLUT_KEY_F2:
		addSource();
		break;
	case GLUT_KEY_F3:
		removeSource();
		break;
	}
}

}

int main(int argc, char* argv[]) {
	srand(time(NULL));
	renderSettings settings;
	settings.fps = 30;
	settings.onKey = wave::onKey;
	settings.drawGeometry = wave::drawGeometry;

	return render_run(settings, argc, argv);
}

