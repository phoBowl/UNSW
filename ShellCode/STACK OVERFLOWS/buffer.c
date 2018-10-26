#include <stdio.h>
#include <string.h>

/*
	create an array size 5
	but we forgot that size five begin with array[0]
	so it ends with array[4]
*/

int main(){
	int array[5] = {1,2,3,4,5};
	printf("%d\n",array[5]);
}

