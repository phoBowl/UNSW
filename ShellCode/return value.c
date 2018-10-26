//in c 
int f(){
	return 123;
};

//x86
f: 
	mov	eax, 123
	ret

//ARM
f	PROC
	MOV 	r0,#0x7b; 123
	BX		lr
	ENDP

//MIPS
j	$31
li	$2,123		# 0x7b


//IN PRACTICE 

//c 
#include <stdio.h>

int main(){
	printf("hello, world\n");
	return 0;
}

//x86

