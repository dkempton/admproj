package exceptions;

@SuppressWarnings("serial")
public class InvalidConfigException extends Exception {

	public InvalidConfigException(String message){
		super(message);
	}
}
