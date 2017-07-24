
public class MapGenerator {
	private int width =0; //width
	private int height = 0; // height
	private final int OFFSET = 50;//distance from alignment (apply for new line '\n')
	private final int DISTANCE = 48; //object size
	private String string1 = " ####\n"
			   +"##  #\n"
			   +"#@$ #\n"
			   +"##$ ###\n"
			   +"#  $  #\n"
			   +"#.$   #\n"
			   +"#..^. #\n"
			   +"#######\n";;
	private String string2 = "#####\n"+
				"#@  #\n"+
				"# $$# ###\n"+
				"# $ # #.#\n"+
				"### ###.#\n"+
				" ##    .#\n"+
				" #   #  #\n"+
				" #   ####\n"+
				" #####\n";
	
	private String string3 = "########\n"
            +"#  #@  #\n"
            +"#  #   #\n"
            +"#   #  #\n"
            +"#  $.*  #\n"
            +"#  ##  #\n"
            +"########\n";
	
	private String string4 = "#####\n"
			+"#  ####\n"
			+"#.. $@#\n"
			+"### $ #\n"
			+"# $$# #\n"
			+"#..   #\n"
			+"#######\n";

	private String string5 = " ######\n"+
			"##. $@##\n"+
			"#    $.#\n"+
			"#.$ #$ #\n"+
			"####  ##\n"+
			"   # .# \n"+
			"   #### \n";
	
	private String string6 ="########\n"
			   +"#  #.. #\n"
			   +"#  @$  #\n"
			   +"# $##^ #\n"
			   +"#  # . #\n"
			   +"#  $   #\n"
			   +"#      #\n"
			   +"########\n";
	
	private String string7 = "############\n"  
			+"#..  #     ###\n"
			+"#..  # $  $  #\n"
			+"#..  #$####  #\n"
			+"#..    @ ##  #\n"
			+"#..  # #  $ ##\n"
			+"###### ##$ $ #\n"
			+"  # $  $ $ $ #\n"
			+"  #    #     #\n"
			+"  ############\n";
	
	/*
	 ####  
  # .#  
  #  #  
###$$## 
#..  .##
#  #$$@#
####   #
   #####
	 */
	private String random ="####\n"
				   +"# .#\n"
				   +"#  #\n"
				   +"###$$##\n"
				   +"#..  .##\n"
				   +"#  #$$@#\n"
				   +"####   #\n"
				   +"   #####\n";
	private String random2 = " #######\n"
							+"## .  .#\n"
							+"#      #\n"
							+"#  #  .#\n"
							+"#     ##\n"
							+"# $$$ # \n"
							+"##@ ###\n"
							+" ####   \n";
	private String random3 = "########"
			               +"#@  . .#\n"
			               +"# $$$  #\n"
			               +"#  #  .#\n"
			               +"########\n";
    private String random4 = "########\n"
    						 +"#   #@ #\n"
    						 +"#   $  #\n"
    						 +"##$#   #\n"
    						 +" # .#  #\n"
    						 +" #$ .  #\n"
    						 +" # .   #\n"
    						 +" #######\n";
					
	private String random5 = "####   \n"
							+"#  ####\n"
							+"#.. $@#\n"
							+"### $ #\n"
							+"# $$# #\n"
							+"#..   #\n"
							+" #######\n";
	private String random6 = "    ####\n"
							+" ####@ #\n"
							+" #  #  #\n"
							+"## . $ #\n"
							+"# $$ ###\n"
							+"# #   # \n"
							+"#  . .# \n"
							+"####### \n";
	
	private String random7 = "####   \n"
							+"#@ ####\n"
							+"# $.  #\n"
							+"###   #\n"
							+"####$ #\n"
							+"#   $ #\n"
							+"#  . .#\n"
							+"#######\n";
	private String random8 = "   #####\n"
							+" ###   #\n"
							+" #.  # #\n"
							+"## $   #\n"
							+"#..#$$##\n"
							+"#.  $@# \n"
							+"#  ####\n"
							+"####   \n";
	private String random9 = "########\n"
							+"#   .. #\n"
							+"# $    #\n"
							+"# $ #$##\n"
							+"##@# .# \n"
							+" #    #\n"
							+" ######\n";
							
	public MapGenerator(){
	
	}
	
	public String getString1(){
		return this.string1;
	}
	public String getString2(){
		return this.string2;
	}
	public String getString3(){
		return this.string3;
	}
	public String getString4(){
		return this.string4;
	}
	public String getString5(){
		return this.string5;
	}
	public String getString6(){
		return this.string6;
	}
	public String getString7(){
		return this.string7;
	}
}