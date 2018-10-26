#include <stdio.h>
void function(int a , int b){
	int array[5];
}
main(){
	function(1,2);
	printf("This is where the return address points");
}

/*
 In this function, main are executed until a function call is encoutered. 
 1. push argument for function , a and b, backwards onto the stack
 When arguements are places onto the stack, the function is called, place the return address or (RET) onto the stack.
 RET is the address stored in the instruction pointer (EIP) at the time function called. RET is the location at which to continue execution when the function has completed, so the rest of the progam will be execute.
 In this program, the address of  printf statement, instruction will be pushed onto the stack
*/