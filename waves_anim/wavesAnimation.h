#ifndef WAVES_ANIMATION
#define WAVES_ANIMATION

#include <stdbool.h>

void wavesAnimation_drawGeometry(long frame);
void wavesAnimation_newSources();
bool wavesAnimation_toggle_cache();
void wavesAnimation___init__(short nsources, short samples);
void wavesAnimation___dispose__();
#endif
