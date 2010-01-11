#include "math3d.h"
#include <string.h>
#include <stdlib.h>

float frand(float min, float max) {
	float frand01 = rand()/(double)RAND_MAX;
	return frand01 * (max - min) + min;
}

void LoadVector2(Vector2 v, float x, float y)
    { v[0] = x; v[1] = y; }
void LoadVector3(Vector3 v, float x, float y, float z)
	{ v[0] = x; v[1] = y; v[2] = z; }

// Copy vector src into vector dst
void CopyVector2(Vector2 dst, const Vector2 src)
	{ memcpy(dst, src, sizeof(Vector2)); }
void CopyVector3(Vector3 dst, const Vector3 src)
	{ memcpy(dst, src, sizeof(Vector3)); }
void AddVectors2(Vector2 r, const Vector2 a, const Vector2 b)
	{ r[0] = a[0] + b[0];	r[1] = a[1] + b[1];  }
void AddVectors3(Vector3 r, const Vector3 a, const Vector3 b)
	{ r[0] = a[0] + b[0];	r[1] = a[1] + b[1]; r[2] = a[2] + b[2]; }
void SubtractVectors2(Vector2 r, const Vector2 a, const Vector2 b)
	{ r[0] = a[0] - b[0]; r[1] = a[1] - b[1];  }
void SubtractVectors3(Vector3 r, const Vector3 a, const Vector3 b)
	{ r[0] = a[0] - b[0]; r[1] = a[1] - b[1]; r[2] = a[2] - b[2]; }

// Scale Vectors (in place)
void ScaleVector2(Vector2 v, float scale)
	{ v[0] *= scale; v[1] *= scale; }
void ScaleVector3(Vector3 v, float scale)
	{ v[0] *= scale; v[1] *= scale; v[2] *= scale; }

void CrossProduct(Vector3 result, const Vector3 u, const Vector3 v)
	{
	result[0] = u[1]*v[2] - v[1]*u[2];
	result[1] = -u[0]*v[2] + v[0]*u[2];
	result[2] = u[0]*v[1] - v[0]*u[1];
	}

float DotProduct(const Vector3 u, const Vector3 v)
	{ return u[0]*v[0] + u[1]*v[1] + u[2]*v[2]; }

float GetAngleBetweenVectors(const Vector3 u, const Vector3 v)
    {
    double dTemp = DotProduct(u, v);
    return acos(dTemp);
    }

float GetVectorLengthSquared(const Vector3 u)
	{ return (u[0] * u[0]) + (u[1] * u[1]) + (u[2] * u[2]); }

float GetVectorLength(const Vector3 u)
	{ return sqrt(GetVectorLengthSquared(u)); }

 void NormalizeVector(Vector3 u)
	{ ScaleVector3(u, 1.0f / GetVectorLength(u)); }

