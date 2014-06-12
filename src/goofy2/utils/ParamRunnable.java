package goofy2.utils;

public abstract class ParamRunnable implements Runnable {
	public Object param;
	
	public ParamRunnable(){
	}

	public ParamRunnable(Object aParam){
		param = aParam;
	}
}
