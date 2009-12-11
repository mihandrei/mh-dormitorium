#include "Mesh.h"
#include <algorithm>
#include <vector>
#include <GL/gl.h>
#include <cmath>

Mesh::Mesh(float xmin, float xmax,
		float ymin, float ymax, int samples,
		palletteFn pallete, float color_interval) {
	this->xmin = xmin;
	this->xmax = xmax;
	this->ymin = ymin;
	this->ymax = ymax;
	this->step = std::max(xmax - xmin , ymax - ymin) / samples;
	this->color_interval = color_interval;
	this->pallete = pallete;
}

void Mesh::draw(surfaceFn func){
  //std::vector<float> stripefringecache(); todo

  bool oddstripe = false;

  for(float stripey = ymin; stripey < ymax ; stripey += step){

      float x0 = xmin;

      if(oddstripe){
          x0 += step/2;
      }
      oddstripe = ! oddstripe;

      bool offsetpoint = false;

      glBegin(GL_TRIANGLE_STRIP);
          for(float x = x0; x < xmax ; x+= step/2 ){

              float y = stripey;

              if(offsetpoint){
                  y += step;
              }
              offsetpoint = !offsetpoint;

              float funcval = func(x, y);
              float color_index = (funcval / color_interval + 1 ) / 2 ;
              color c = pallete(color_index);
              glColor3f(c.r,c.g,c.b);
              glVertex3f(x,y,funcval);
          }
      glEnd();
  }
}

color defpallete(float c){
      if(c < 0){        	//red->black funcval-> -inf
         float v = 1-c;
         return color( 1 / (v*v*v*v)  , 0 , 0);
      }
      else if(c < 0.25){ 	// red orange
         c = 4*c;
         return color( 1, c/2 , 0);
      }else if (c < 0.5){ 	//orange yellow
         c = 4*(c - 0.25);
         return color( 1, c/2 + 0.5 , 0);
      }else if (c < 0.75){ 	//yellow cyan
         c = 4*(c-0.5);
         return color( 1 - c, 1, c);
      }else if(c < 1){ 		// cyan blue
         c = 4*(c-0.75);
         return color( 0, 1-c, 1);
      }else{  				//blue->black for funcval->+inf
    	  return color( 0, 0 , 1 / (c*c*c*c) );
      }
}

color greypallete(float c){
	float tone = std::abs(c - 0.5)*2 + 0.2;
    return color(tone,tone,tone);
}


