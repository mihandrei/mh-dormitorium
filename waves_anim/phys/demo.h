#ifndef DEMO_H_
#define DEMO_H_

#include "../phys/surfacephysics.h"

void demo__init(PhysSurf *physs) ;
void demo__dispose(void);
void demo_nextdeform(PhysSurf *physs);

#endif /* DEMO_H_ */
