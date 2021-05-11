package com.evenliu.exceptions;

/**
 * 秒杀结束的异常
 * @author liu
 *
 */
public class SecKillCloseException extends SeckillException{

	public SecKillCloseException(String message, Throwable cause) {
		super(message, cause);
	}

	public SecKillCloseException(String message) {
		super(message);
	}
	
}
