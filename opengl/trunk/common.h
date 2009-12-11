#ifndef COMMON_H_
#define COMMON_H

extern const float PI;
float randf(float upper , float lower);
void renderaxes(float scale);

struct renderSettings{
	void (* onKey)(int key);
	void (* drawGeometry)(long frame);
	int fps;
};

int render_run(const renderSettings settings, int argc, char* argv[]);

#endif /* COMMON_H_ */
