

/*
	Empty function in c  
*/
void f(){
	return ;
}

/*
x86
*/

f: 
	ret // there is just one instruction : RET ,which return the execution to the caller

/* 
ARM
*/ 
f   PROC 
	BX		lr
	ENDP	
	//BX LR instructtion cause execution to jump to that address 

/*
MIPS 
*/
	j   $31
	nop

