/*
 * IN C ONE CANNOT HAVE FUNCTION BODIES IN H FILES!!!
 * THE H WILL BE INCLUDED MORE THAN ONCE EVEN IF GUARDED THE LINKER WILL MESS IT
 * */
#ifndef _MATH3D_LIBRARY_DIMINISHED
#define _MATH3D_LIBRARY_DIMINISHED

#include <math.h>

#define PI (3.14159265358979323846)

typedef float	Vector3[3];		// Vector of three floats (x, y, z)
typedef float 	Vector2[2];		// 3D points = 3D Vectors, but we need a

// 3x3 matrix - column major. X vector is 0, 1, 2, etc.
//		0	3	6	
//		1	4	7
//		2	5	8
typedef float Matrix33[9];		// A 3 x 3 matrix, column major (floats) - OpenGL Style
typedef float Matrix44[16];		// A 4 X 4 matrix, column major (floats) - OpenGL style

void LoadVector2(Vector2 v, float x, float y);
void LoadVector3(Vector3 v, float x, float y, float z);

// Copy vector src into vector dst
void CopyVector2(Vector2 dst, const Vector2 src);
void CopyVector3(Vector3 dst, const Vector3 src);
void AddVectors2(Vector2 r, const Vector2 a, const Vector2 b);
void AddVectors3(Vector3 r, const Vector3 a, const Vector3 b);
void SubtractVectors2(Vector2 r, const Vector2 a, const Vector2 b);
void SubtractVectors3(Vector3 r, const Vector3 a, const Vector3 b);
// Scale Vectors (in place)
void ScaleVector2(Vector2 v, float scale);
void ScaleVector3(Vector3 v, float scale);

void CrossProduct(Vector3 result, const Vector3 u, const Vector3 v);
float DotProduct(const Vector3 u, const Vector3 v);
float GetAngleBetweenVectors(const Vector3 u, const Vector3 v);
float GetVectorLengthSquared(const Vector3 u);
float GetVectorLength(const Vector3 u);
void NormalizeVector(Vector3 u);

float frand(float min, float max) ;
#endif

