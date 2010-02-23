#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <time.h>
#include <GL/gl.h>

#include "PointSource.h"
#include "wavesAnimation.h"

static short NSOURCES;
static short SAMPLES;

static CachingSource *sources;

static parametricmesh *parammesh;
static waves_context ctxt;

static bool cache = true;
static parametricSurface *func = &waves_spatial_cache;

#ifdef DEBUG
static void printmesh() {
	for (int i = 0; i <= parammesh->mesh->nindexes; i++) {
		printf("%d ", parammesh->mesh->pIndexes[i]);
		if (i % (2 * (SAMPLES + 1)) == 2 * SAMPLES + 1)
			printf("\n");
	}
}
#endif

static void drawSourceLocations(){
	glColor3f(1,1,1);
	for(int i =0;i<NSOURCES ;i++){
	    glBegin(GL_LINES);
             glVertex3f(sources[i].psrc.x0,sources[i].psrc.y0,sources[i].psrc.A0);
             glVertex3f(sources[i].psrc.x0,sources[i].psrc.y0,-sources[i].psrc.A0);
    	glEnd();
    }
}

void wavesAnimation_drawGeometry(long frame) {
	ctxt.time = frame * 0.01;

	computeParametricMesh(parammesh, *func, &ctxt);
	drawSourceLocations();
	drawParametricMesh(parammesh);
}

void wavesAnimation_newSources(){
		//we trash here a bit; could make a in place modify of a caching source, no reallocating spatials
		CachingSources__dispose__(sources, NSOURCES);
		CachingSources__init__(sources, NSOURCES, parammesh);
}
bool wavesAnimation_toggle_cache(){
		cache = !cache;
		if(cache){
			func = &waves_spatial_cache;
		}else{
			func = &waves_no_cache;
		}
		return cache;
}

void wavesAnimation___init__(short nsources, short samples,
		bool usenormals,bool usecolor,bool gentexcoords,int colortype){
	NSOURCES = nsources;
	SAMPLES = samples;

	srand(time(NULL));
	sources =  malloc(sizeof(CachingSource)* NSOURCES);
	parammesh = parametricmesh__init__(SAMPLES, 1, usenormals, usecolor, gentexcoords,colortype);

	CachingSources__init__(sources, NSOURCES, parammesh);
	ctxt.nsources = NSOURCES;
	ctxt.sources = sources;
}

void wavesAnimation___dispose__(){
	parametricmesh__dispose__(parammesh);
	CachingSources__dispose__(sources, NSOURCES);
	free(sources);
}
