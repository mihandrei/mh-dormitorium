//i do not neet  good quality randoms:
#include <cmath>
#include <cstdlib>

extern const float PI = 2 * acos(0); //const are implicit static
float randf(float lower, float upper){
	float d = upper-lower;
    return (float)rand() / (float) RAND_MAX * d + lower;
}
