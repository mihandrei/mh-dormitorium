const char a = 8;
const char b = 2;

const char c1 = 3;
const char c2 = 5;

int ef(){
     short t1 = a*c1 ;
     short t2 = b*b*c2;
     short d = a*a + a*b;
     return (t1+t2)/d-a-b;
}

int ef1(){
    return (a*c1+b*b*c2)/(a*a+a*b);
}

int ep(){
   return a*c1 + b*b*c2;
}

int ep3(){
   return a*c1;
}
   
int mul3(int k){
  return k*3;
}

long long ll(){
   long long xl = 113;
   long long yl = 24;
   
   return xl*yl;   
 }

#include <stdio.h>
int main(char *argc,char **argv){
    printf("%i\n",  ef1());
    int zz = mul3(4);
}
